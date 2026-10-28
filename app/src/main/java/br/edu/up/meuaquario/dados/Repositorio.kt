package br.edu.up.meuaquario.dados

import android.content.Context
import android.util.Log

enum class StatusOcupacao(val rotulo: String, val descricao: String) {
    LIVRE("Tranquilo", "Muita folga de espaço. Ainda cabe bastante peixe."),
    ADEQUADO("Bem povoado", "Ocupação saudável para o volume deste aquário."),
    ATENCAO("Quase cheio", "Perto do limite. Pense duas vezes antes de acrescentar."),
    SUPERLOTADO("Superlotado", "Passou do volume recomendado. Risco para os peixes.")
}

object Repositorio {

    @Volatile
    private var inicializado = false

    private lateinit var aquarioDao: AquarioDao
    private lateinit var especieDao: EspecieDao

    suspend fun iniciar(context: Context) {
        if (inicializado) return
        val db = AppDatabase.obter(context)
        aquarioDao = db.aquarioDao()
        especieDao = db.especieDao()
        inicializado = true
        try {
            if (aquarioDao.buscarTodos().isEmpty()) {
                popularExemplos()
            }
        } catch (e: Exception) {
            Log.e("Repositorio", "Erro ao preparar dados iniciais", e)
        }
    }

    private suspend fun popularExemplos() {
        val comunitario = Aquario(0, "Comunitário da sala", 100, 40, 50, TipoAquario.DOCE)
        val plantado = Aquario(0, "Plantado 60P", 60, 30, 36, TipoAquario.PLANTADO)
        val nano = Aquario(0, "Nano marinho", 45, 30, 30, TipoAquario.MARINHO)
        val pequeno = Aquario(0, "Aquário do Huan", 40, 25, 30, TipoAquario.DOCE)

        val idComunitario = aquarioDao.inserir(comunitario)
        val idPlantado = aquarioDao.inserir(plantado)
        val idNano = aquarioDao.inserir(nano)
        val idPequeno = aquarioDao.inserir(pequeno)

        especieDao.inserir(Especie(0, "Neon Cardinal", "Paracheirodon innesi", idComunitario.toInt(), 12, 3, 6.5))
        especieDao.inserir(Especie(0, "Coridora Paleatus", "Corydoras paleatus", idComunitario.toInt(), 4, 6, 7.0))
        especieDao.inserir(Especie(0, "Tetra Serpae", "Hyphessobrycon eques", idPlantado.toInt(), 8, 4, 6.8))
        especieDao.inserir(Especie(0, "Betta", "Betta splendens", idPlantado.toInt(), 2, 6, 7.0))
        especieDao.inserir(Especie(0, "Palhaço Ocelaris", "Amphiprion ocellaris", idNano.toInt(), 2, 8, 8.2))
        especieDao.inserir(Especie(0, "Guppy", "Poecilia reticulata", idPequeno.toInt(), 10, 5, 7.2))
    }

    suspend fun buscarAquarios(): List<Aquario> = aquarioDao.buscarTodos()

    suspend fun buscarAquarioPorId(id: Int): Aquario? = aquarioDao.buscarPorId(id)

    suspend fun inserirAquario(
        nome: String,
        comprimentoCm: Int,
        larguraCm: Int,
        alturaCm: Int,
        tipo: TipoAquario
    ): Long {
        return aquarioDao.inserir(
            Aquario(
                id = 0,
                nome = nome.trim(),
                comprimentoCm = comprimentoCm,
                larguraCm = larguraCm,
                alturaCm = alturaCm,
                tipo = tipo
            )
        )
    }

    suspend fun atualizarAquario(aquario: Aquario) = aquarioDao.atualizar(aquario)

    suspend fun removerAquario(id: Int) {
        val aquario = aquarioDao.buscarPorId(id) ?: return
        aquarioDao.deletarEspeciesDoAquario(id)
        aquarioDao.deletar(aquario)
    }

    suspend fun buscarEspecies(): List<Especie> = especieDao.buscarTodos()

    suspend fun buscarEspeciePorId(id: Int): Especie? = especieDao.buscarPorId(id)

    suspend fun buscarEspeciesDoAquario(aquarioId: Int): List<Especie> =
        especieDao.buscarDoAquario(aquarioId)

    suspend fun inserirEspecie(
        nome: String,
        nomeCientifico: String,
        aquarioId: Int,
        quantidade: Int,
        tamanhoAdultoCm: Int,
        phIdeal: Double
    ): Long {
        return especieDao.inserir(
            Especie(
                id = 0,
                nome = nome.trim(),
                nomeCientifico = nomeCientifico.trim(),
                aquarioId = aquarioId,
                quantidade = quantidade,
                tamanhoAdultoCm = tamanhoAdultoCm,
                phIdeal = phIdeal
            )
        )
    }

    suspend fun atualizarEspecie(especie: Especie) = especieDao.atualizar(especie)

    suspend fun alterarQuantidade(especieId: Int, novaQuantidade: Int) {
        val especie = especieDao.buscarPorId(especieId) ?: return
        if (novaQuantidade >= 1) {
            especieDao.atualizar(especie.copy(quantidade = novaQuantidade))
        }
    }

    suspend fun removerEspecie(id: Int) {
        val especie = especieDao.buscarPorId(id) ?: return
        especieDao.deletar(especie)
    }

    suspend fun removerEspecies(ids: Set<Int>) {
        ids.forEach { removerEspecie(it) }
    }

    fun volumeLitros(aquario: Aquario): Int =
        (aquario.comprimentoCm * aquario.larguraCm * aquario.alturaCm) / 1000

    fun litrosNecessarios(especie: Especie): Int =
        especie.tamanhoAdultoCm * especie.quantidade

    fun litrosOcupados(especies: List<Especie>): Int =
        especies.sumOf { litrosNecessarios(it) }

    fun volumeTotal(aquarios: List<Aquario>): Int =
        aquarios.sumOf { volumeLitros(it) }

    fun totalDePeixes(especies: List<Especie>): Int =
        especies.sumOf { it.quantidade }

    fun capacidadeRestante(aquario: Aquario, litrosOcupados: Int): Int =
        (volumeLitros(aquario) - litrosOcupados).coerceAtLeast(0)

    fun percentualOcupacao(aquario: Aquario, litrosOcupados: Int): Int {
        val volume = volumeLitros(aquario)
        if (volume <= 0) return 0
        return (litrosOcupados * 100) / volume
    }

    fun fracaoOcupacao(percentual: Int): Float =
        (percentual / 100f).coerceIn(0f, 1f)

    fun statusOcupacao(percentual: Int): StatusOcupacao = when {
        percentual > 100 -> StatusOcupacao.SUPERLOTADO
        percentual >= 90 -> StatusOcupacao.ATENCAO
        percentual >= 70 -> StatusOcupacao.ADEQUADO
        else -> StatusOcupacao.LIVRE
    }

    fun aquariosSuperlotados(aquarios: List<Aquario>, especies: List<Especie>): List<Aquario> =
        aquarios.filter {
            val ocupados = especies.filter { e -> e.aquarioId == it.id }.sumOf { e -> litrosNecessarios(e) }
            statusOcupacao(percentualOcupacao(it, ocupados)) == StatusOcupacao.SUPERLOTADO
        }
}
