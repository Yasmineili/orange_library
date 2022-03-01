package com.andrea.orangelibrary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class],
    version = 1 //,exportSchema = false
)
abstract class BookDB: RoomDatabase() {
    abstract fun getBookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: BookDB? = null

        fun getDatabase(context: Context): BookDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookDB::class.java,
                    "library_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}