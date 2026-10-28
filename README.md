# MeuAquário — Trabalho 3

Aplicativo Android em Jetpack Compose para quem cria peixe de aquário. Guarda as medidas de cada aquário, calcula o volume em litros e mostra quantos peixes cabem ali sem superlotar.

Este repositório é a continuação do Trabalho 2. As telas, a navegação e os formulários são os mesmos. O que mudou é a origem dos dados: saiu da memória e foi para o **Room**. Fechar o app não apaga mais o que foi cadastrado.

**Disciplina:** Desenvolvimento de Aplicativos Móveis  
**Integrantes:** Felipe Gabriel da Silva · Huan Machado de Oliveira

---

## Como rodar

### Android Studio

1. **File → Open** e escolha a pasta deste repositório (a que contém `settings.gradle.kts`).
2. Espere o Gradle sync terminar. Se pedir para baixar o SDK 34 ou aceitar licença, aceite.
3. O Android Studio cria o `local.properties` sozinho, apontando para o SDK da máquina. Esse arquivo não vai no Git.
4. Escolha o emulador (Pixel 10a ou outro com API 24 ou maior) ou um aparelho com depuração USB.
5. Clique em **Run**.

Na primeira abertura o banco está vazio, então o app grava quatro aquários e seis espécies de exemplo. A partir daí, tudo que for adicionado, editado ou removido fica no arquivo `meuaquario.db`.

### Linha de comando

```bat
gradlew.bat assembleDebug
```

O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

```bat
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Versões: AGP 9.4.0 · Gradle 9.6.0 · Room 2.7.2 · Compose BOM 2024.02.01 · Navigation Compose 2.7.7 · `compileSdk` 35 · `minSdk` 24.

---

## 1. Como estava no Trabalho 2 e o que mudou

No Trabalho 2 as duas listas viviam num `object Repositorio` com `mutableStateListOf`. Adicionar, remover e mudar a quantidade funcionavam enquanto o processo estava vivo. Quando a Activity era destruída, a lista morria junto. Abrir de novo chamava `carregarExemplos()` e voltava ao estado inicial.

No Trabalho 3 a interface não ganhou tela. As sete rotas (`inicio`, `aquarios`, `especies`, `novo_aquario`, `nova_especie`, `detalhe_aquario/{id}`, `detalhe_especie/{id}`), a barra inferior e os formulários continuam iguais. O `Repositorio` deixou de ser a lista e passou a ser a fachada do banco:

- leitura e escrita são `suspend fun` e chamam o DAO;
- volume, litros ocupados, percentual e status continuam funções puras, recebendo as listas já lidas;
- cada tela guarda o resultado em `mutableStateOf` e busca de novo no `LaunchedEffect` e quando a tela volta a ficar visível (`ON_RESUME`).

O teste que o Trabalho 2 não passava agora passa: criar um aquário, tirar o app dos recentes e abrir de novo. O aquário continua lá. Os exemplos só são inseridos se a tabela `aquarios` estiver vazia, então um cadastro novo não é substituído na próxima abertura.

---

## 2. Entidades, DAOs e banco

### `Aquario` — tabela `aquarios` (`dados/Modelos.kt`)

| Campo | Por quê |
|---|---|
| `id` | `@PrimaryKey(autoGenerate = true)`. No Trabalho 2 o id era calculado na mão com `max + 1`. Agora o SQLite gera. |
| `nome` | O que a pessoa digita no formulário. |
| `comprimentoCm`, `larguraCm`, `alturaCm` | As três medidas. O volume **não** é coluna: `comprimento × largura × altura ÷ 1000` continua calculado na leitura, para não ficar desatualizado se a medida mudar. |
| `tipo` | Enum `DOCE`, `PLANTADO`, `MARINHO`. Um `@TypeConverter` grava o `name` do enum como texto. |

### `Especie` — tabela `especies`

| Campo | Por quê |
|---|---|
| `id` | `@PrimaryKey(autoGenerate = true)`. |
| `nome`, `nomeCientifico` | Nome popular e científico do formulário. |
| `quantidade`, `tamanhoAdultoCm`, `phIdeal` | Entram na carga (`tamanho × quantidade`) e na ficha. |
| `aquarioId` | Liga a espécie ao aquário. É o campo do `SELECT * FROM especies WHERE aquarioId = :aquarioId`. Não há `@ForeignKey`: ao apagar o aquário, `deletarEspeciesDoAquario` remove as espécies daquele id antes de apagar o tanque, para não sobrar linha órfã. |

### DAOs (`dados/AquarioDao.kt` e `dados/EspecieDao.kt`)

Os dois têm os quatro métodos pedidos, todos `suspend`:

- `@Insert` → `inserir`, devolve o `Long` do id gerado (usado para gravar as espécies de exemplo no aquário certo);
- `@Query("SELECT * FROM ... ORDER BY id ASC")` → `buscarTodos`;
- `@Update` → `atualizar`;
- `@Delete` → `deletar`.

Consultas além desses quatro:

- `buscarPorId(id)` nas duas tabelas. É a consulta filtrada da seção 3.1 e a que a tela de detalhe usa.
- `EspecieDao.buscarDoAquario(aquarioId)` — espécies de um tanque, para o detalhe do aquário e para a conta de ocupação.
- `AquarioDao.deletarEspeciesDoAquario(aquarioId)` — `DELETE` filtrado, numa query só, quando o aquário sai.

Não há `JOIN`. As duas tabelas são pequenas; a tela lê as duas e cruza em memória pelo `aquarioId`.

### `AppDatabase` (`dados/AppDatabase.kt`)

```kotlin
@Database(entities = [Aquario::class, Especie::class], version = 1, exportSchema = false)
@TypeConverters(ConversorTipoAquario::class)
abstract class AppDatabase : RoomDatabase()
```

O `companion object` guarda a instância em `@Volatile` e cria com `synchronized` + `Room.databaseBuilder(..., "meuaquario.db")`. `fallbackToDestructiveMigration(true)` está aqui porque ainda é a versão 1: se o esquema mudar numa entrega futura, a versão sobe e a migração entra no lugar desse fallback. `exportSchema = false` evita o aviso do KSP pedindo pasta de schema, que este trabalho não pede.

---

## 3. CRUD persistido

Toda chamada de DAO passa pelo `Repositorio` e roda em `CoroutineScope(Dispatchers.IO).launch`, com `try/catch` e `Log.e`. Nada de banco na thread principal.

| Ação | Onde | O que grava |
|---|---|---|
| Buscar listas | `TelaInicio`, `TelaListaAquarios`, `TelaListaEspecies` | `buscarTodos()` no `LaunchedEffect`. Ao voltar do formulário, `ON_RESUME` dispara de novo. |
| Inserir aquário | `TelaNovoAquario` | `aquarioDao.inserir` e `popBackStack`. |
| Inserir espécie | `TelaNovaEspecie` | `especieDao.inserir` com o `aquarioId` escolhido. |
| Atualizar | `TelaDetalheEspecie`, botões − e + | `alterarQuantidade` faz `buscarPorId`, `copy(quantidade = ...)` e `@Update`. |
| Remover | lixeira do cartão, do detalhe, e a barra de espécies marcadas | `deletar`. Remover aquário também apaga as espécies dele. |
| Detalhe | `TelaDetalheAquario` e `TelaDetalheEspecie` | Não recebem o objeto pela navegação. Recebem o `id` da rota e chamam `buscarPorId`. Se a linha não existe, a tela mostra "Item não encontrado". |

---

## 4. O que quebrou na migração

**A lista deixou de se redesenhar sozinha.** `mutableStateListOf` avisava o Compose. `suspend fun` não pode ser chamada no meio do composable. Cada tela passou a ter o seu `mutableStateOf<List<...>>` e a preencher depois da leitura.

**Voltar do formulário mostrava a lista velha.** `LaunchedEffect(Unit)` roda uma vez. O helper `lembrarGatilhoDeRecarga()` (`ui/componentes/CicloDeVida.kt`) observa `ON_RESUME` e incrementa um inteiro. Esse inteiro é a chave do `LaunchedEffect`, então a lista busca de novo quando a tela reaparece.

**A conta de ocupação lia a outra lista direto.** `litrosOcupados(aquarioId)` ia no `mutableStateListOf` de espécies. Agora `litrosOcupados(especies)`, `percentualOcupacao(aquario, litros)` e `aquariosSuperlotados(aquarios, especies)` recebem o que já foi lido. A UI faz duas leituras e cruza.

**`return` no meio de composable continua proibido.** Isso já tinha derrubado o formulário de espécie no Trabalho 2. As telas de detalhe tratam "não achou no banco" com `when`, não com `return`.

---

## 5. Teste de persistência

Procedimento que prova a seção 3.2:

1. Abra o app e anote os aquários de exemplo.
2. Toque em **Novo aquário**, salve um nome que não existe nos exemplos (por exemplo, "Teste de persistência", 50 × 30 × 40 cm).
3. Confira o cartão na lista.
4. Feche o app de verdade: tire dos recentes, não só volte para a home.
5. Abra de novo. O aquário novo continua, com o mesmo volume calculado.
6. Abra uma espécie, mude a quantidade com **+**, repita o fechar e abrir. A quantidade nova continua.
7. Apague esse aquário pela lixeira, feche e abra. Ele não volta.

Vídeo desse teste: *(colar o link aqui antes de apresentar)*.

---

## 6. Estrutura

```
app/src/main/java/br/edu/up/meuaquario/
├── MainActivity.kt
├── dados/
│   ├── Modelos.kt
│   ├── AquarioDao.kt
│   ├── EspecieDao.kt
│   ├── AppDatabase.kt
│   └── Repositorio.kt
├── navegacao/
│   ├── Rotas.kt
│   └── AppMeuAquario.kt
└── ui/
    ├── theme/
    ├── componentes/
    └── telas/
```

Não há servidor, login nem API. O banco é o SQLite local do Room, no aparelho.

---

## 7. Checklist do enunciado

- [x] Repositório com este README e o que é preciso para o Android Studio sincronizar
- [x] Room no Gradle: `room-runtime`, `room-ktx` e `ksp("androidx.room:room-compiler")`, com o plugin KSP aplicado
- [x] Duas `@Entity`, cada uma com `@PrimaryKey(autoGenerate = true)`
- [x] Dois `@Dao` com `@Insert`, `@Query`, `@Update` e `@Delete`, todos `suspend`
- [x] Consulta filtrada: `buscarPorId` e `buscarDoAquario`
- [x] Detalhe lê do banco pelo id da rota
- [x] `AppDatabase` singleton
- [x] Listas, inclusão, edição e remoção passam pelo banco
- [x] Operações de banco em `Dispatchers.IO`, com `try/catch` e `Log.e`
- [ ] Vídeo do teste de fechar e reabrir (seção 5) — gravar e colocar o link acima
