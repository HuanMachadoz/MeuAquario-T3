package br.edu.up.meuaquario.dados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EspecieDao {

    @Insert
    suspend fun inserir(especie: Especie): Long

    @Query("SELECT * FROM especies ORDER BY id ASC")
    suspend fun buscarTodos(): List<Especie>

    @Query("SELECT * FROM especies WHERE id = :id")
    suspend fun buscarPorId(id: Int): Especie?

    @Query("SELECT * FROM especies WHERE aquarioId = :aquarioId ORDER BY id ASC")
    suspend fun buscarDoAquario(aquarioId: Int): List<Especie>

    @Update
    suspend fun atualizar(especie: Especie)

    @Delete
    suspend fun deletar(especie: Especie)
}
