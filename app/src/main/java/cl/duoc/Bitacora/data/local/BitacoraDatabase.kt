package cl.duoc.Bitacora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        EntradaBitacoraEntity::class,
        BitacoraEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BitacoraDatabase : RoomDatabase() {

    abstract fun entradaBitacoraDao(): EntradaBitacoraDao

    abstract fun bitacoraDao(): BitacoraDao

    companion object {

        @Volatile
        private var INSTANCE: BitacoraDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `bitacoras` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `asignatura` TEXT NOT NULL,
                        `mes` TEXT NOT NULL,
                        `anio` INTEGER NOT NULL,
                        `curso` TEXT NOT NULL,
                        `estado` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
            ALTER TABLE entradas_bitacora
            ADD COLUMN fotoUri TEXT
            """.trimIndent()
                )
            }
        }
        fun obtenerInstancia(context: Context): BitacoraDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BitacoraDatabase::class.java,
                    "bitacora_database"
                )

                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
