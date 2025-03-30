package com.example.crudexample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Author::class, Book::class],
    version = 2,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun authorDao(): AuthorDao
    abstract fun bookDao(): BookDao
    abstract fun authorWithBooksDao(): AuthorWithBooksDao

    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        // เพิ่ม Migration จาก version 1 ไป 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE books ADD COLUMN isbn TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE books ADD COLUMN purchaseDate TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE books ADD COLUMN notes TEXT")
            }
        }

        fun getDatabase(context: Context): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "library_database"
                )
                    .addMigrations(MIGRATION_1_2) // เพิ่มการใช้ Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}