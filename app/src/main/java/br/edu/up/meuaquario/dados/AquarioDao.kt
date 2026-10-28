package br.edu.up.meuaquario.dados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AquarioDao {

    @Insert
    suspend fun inserir(aquario: Aquario): Long

    @Query("SELECT * FROM aquarios ORDER BY id ASC")
    suspend fun buscarTodos(): List<Aquario>

    @Query("SELECT * FROM aquarios WHERE id = :id")
    suspend fun buscarPorId(id: Int): Aquario?

    @Update
    suspend fun atualizar(aquario: Aquario)

    @Delete
    suspend fun deletar(aquario: Aquario)

    @Query("DELETE FROM especies WHERE aquarioId = :aquarioId")
    suspend fun deletarEspeciesDoAquario(aquarioId: Int)
}
