package br.edu.up.meuaquario.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class ConversorTipoAquario {

    @TypeConverter
    fun paraCodigo(tipo: TipoAquario): String = tipo.name

    @TypeConverter
    fun deCodigo(codigo: String): TipoAquario = TipoAquario.valueOf(codigo)
}

@Database(entities = [Aquario::class, Especie::class], version = 1, exportSchema = false)
@TypeConverters(ConversorTipoAquario::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun aquarioDao(): AquarioDao
    abstract fun especieDao(): EspecieDao

    companion object {

        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obter(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                INSTANCIA ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "meuaquario.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCIA = it }
            }
        }
    }
}
