package br.edu.up.meuaquario.navegacao

object Rotas {

    const val INICIO = "inicio"
    const val AQUARIOS = "aquarios"
    const val ESPECIES = "especies"

    const val NOVO_AQUARIO = "novo_aquario"

    const val NOVA_ESPECIE = "nova_especie?aquarioId={aquarioId}"

    fun novaEspecie(aquarioId: Int = -1): String =
        if (aquarioId <= 0) "nova_especie" else "nova_especie?aquarioId=$aquarioId"

    const val DETALHE_AQUARIO = "detalhe_aquario/{id}"
    fun detalheAquario(id: Int): String = "detalhe_aquario/$id"

    const val DETALHE_ESPECIE = "detalhe_especie/{id}"
    fun detalheEspecie(id: Int): String = "detalhe_especie/$id"

    const val ARG_ID = "id"
    const val ARG_AQUARIO_ID = "aquarioId"

    val ITENS_DA_BARRA = listOf(INICIO, AQUARIOS, ESPECIES)
}
