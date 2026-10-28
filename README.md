# MeuAquário — Trabalho 3

App Android (Jetpack Compose) para cadastrar aquários e espécies. O volume e a ocupação são calculados. Os dados ficam no Room e continuam lá depois de fechar o app.

**Disciplina:** Desenvolvimento de Aplicativos Móveis  
**Integrantes:** Felipe Gabriel da Silva · Huan Machado de Oliveira

## Como rodar

1. Abra o Android Studio em **Open** e escolha esta pasta (a que tem o `settings.gradle.kts`).
2. Espere o Gradle sync.
3. Escolha um emulador ou aparelho e clique em **Run**.

Pela linha de comando: `gradlew.bat assembleDebug`.  
O APK sai em `app/build/outputs/apk/debug/app-debug.apk`.

Versões: AGP 8.2.0 · Kotlin 1.9.22 · KSP 1.9.22-1.0.17 · Room 2.6.1 · Compose BOM 2024.02.01 · `compileSdk` 34 · `minSdk` 24.

## O que mudou em relação ao Trabalho 2

No Trabalho 2 as listas ficavam em `mutableStateListOf`. Fechar o app apagava tudo. Agora a mesma interface lê e grava no Room.

- `dados/Modelos.kt` — `@Entity` `Aquario` e `Especie`, com `id` `@PrimaryKey(autoGenerate = true)`. O volume não é coluna: continua calculado pelas medidas.
- `dados/AquarioDao.kt` e `dados/EspecieDao.kt` — `@Insert`, `buscarTodos`, `@Update`, `@Delete`, todos `suspend`. Consulta filtrada: `buscarPorId` e `buscarDoAquario`.
- `dados/AppDatabase.kt` — `@Database` em singleton (`@Volatile` + `synchronized`). O enum `TipoAquario` entra por `@TypeConverter`.
- `dados/Repositorio.kt` — fachada `suspend` em cima dos DAOs. As contas (litros, ocupação, status) continuam funções puras.
- Telas — `LaunchedEffect` busca no banco ao abrir. Inserir, atualizar e remover rodam em `CoroutineScope(Dispatchers.IO)`. O Detalhe busca o item pelo `id` da rota (`buscarPorId`).

## Dificuldade da migração

`mutableStateListOf` atualizava a tela sozinho. Com Room a leitura é `suspend`, então cada tela guarda a lista em `mutableStateOf` e recarrega no `LaunchedEffect` e quando a tela volta (`ON_RESUME`). As contas que cruzavam as duas listas passaram a receber as listas já carregadas, para não chamar o banco dentro do composable.
