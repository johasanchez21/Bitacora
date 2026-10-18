package cl.duoc.Bitacora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EntradaBitacoraEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BitacoraDatabase : RoomDatabase() {

    abstract fun entradaBitacoraDao(): EntradaBitacoraDao

    companion object {

        @Volatile
        private var INSTANCE: BitacoraDatabase? = null

        fun obtenerInstancia(context: Context): BitacoraDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    BitacoraDatabase::class.java,
                    "bitacora_database"
                ).build()

                INSTANCE = instancia

                instancia
            }
        }
    }
}

