package hu.zsoltkiss.bookdepo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.zsoltkiss.bookdepo.data.dao.BookDao

@Database(entities = [Book::class], version = 1)
abstract class BookDepoDatabase: RoomDatabase() {
    abstract fun bookDao(): BookDao


    companion object {
        @Volatile
        private var INSTANCE: BookDepoDatabase? = null

        fun getDatabase(context: Context): BookDepoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance: BookDepoDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    BookDepoDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}