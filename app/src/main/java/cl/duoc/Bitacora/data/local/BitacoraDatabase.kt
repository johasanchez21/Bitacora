package cl.duoc.Bitacora.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.Bitacora.data.local.dao.BitacoraDao
import cl.duoc.Bitacora.data.local.dao.CursoDao
import cl.duoc.Bitacora.data.local.dao.EntradaBitacoraDao
import cl.duoc.Bitacora.data.local.dao.UsuarioDao
import cl.duoc.Bitacora.data.local.entity.BitacoraEntity
import cl.duoc.Bitacora.data.local.entity.CursoEntity
import cl.duoc.Bitacora.data.local.entity.EntradaBitacoraEntity
import cl.duoc.Bitacora.data.local.entity.EstudianteCursoEntity
import cl.duoc.Bitacora.data.local.entity.UsuarioEntity

@Database(
    entities = [
        EntradaBitacoraEntity::class,
        BitacoraEntity::class,
        CursoEntity::class,
        UsuarioEntity::class,
        EstudianteCursoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class BitacoraDatabase : RoomDatabase() {

    abstract fun entradaBitacoraDao(): EntradaBitacoraDao
    abstract fun bitacoraDao(): BitacoraDao
    abstract fun cursoDao(): CursoDao
    abstract fun usuarioDao(): UsuarioDao

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

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cursos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `codigo` TEXT NOT NULL,
                        `anio` INTEGER NOT NULL,
                        `semestre` INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `usuarios` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `correo` TEXT NOT NULL,
                        `password` TEXT NOT NULL,
                        `rol` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `estudiante_cursos` (
                        `estudianteId` INTEGER NOT NULL,
                        `cursoId` INTEGER NOT NULL,
                        PRIMARY KEY(`estudianteId`, `cursoId`)
                    )
                    """.trimIndent()
                )
                db.execSQL("ALTER TABLE bitacoras ADD COLUMN cursoId INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE bitacoras ADD COLUMN profesorId INTEGER NOT NULL DEFAULT 0")
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
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
