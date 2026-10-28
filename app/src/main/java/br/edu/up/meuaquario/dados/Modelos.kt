package br.edu.up.meuaquario.dados

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TipoAquario(val rotulo: String) {
    DOCE("Água doce"),
    PLANTADO("Plantado"),
    MARINHO("Marinho")
}

@Entity(tableName = "aquarios")
data class Aquario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val comprimentoCm: Int,
    val larguraCm: Int,
    val alturaCm: Int,
    val tipo: TipoAquario
)

@Entity(tableName = "especies")
data class Especie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val nomeCientifico: String,
    val aquarioId: Int,
    val quantidade: Int,
    val tamanhoAdultoCm: Int,
    val phIdeal: Double
)
