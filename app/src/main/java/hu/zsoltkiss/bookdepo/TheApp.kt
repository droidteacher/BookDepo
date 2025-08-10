package hu.zsoltkiss.bookdepo

import android.app.Application
import hu.zsoltkiss.bookdepo.data.BookDepoDatabase

class TheApp: Application() {

    private lateinit var database: BookDepoDatabase

    override fun onCreate() {
        super.onCreate()

        database = BookDepoDatabase.getDatabase(this)
    }
}