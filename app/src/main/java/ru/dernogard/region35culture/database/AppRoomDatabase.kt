package ru.dernogard.region35culture.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.dernogard.region35culture.database.dao.CultureObjectDao
import ru.dernogard.region35culture.database.models.CultureObject

@Database(entities = [CultureObject::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase: RoomDatabase() {

    abstract fun cultureObjectDao(): CultureObjectDao

}

// Object for creating app database in Dependency Injection's Module.
internal object DatabaseDiCreator {

    fun createAppRoomDatabase(appContext: Context) =
        Room.databaseBuilder(
            appContext, AppRoomDatabase::class.java, "Region35Culture.db"
        ).build()

}