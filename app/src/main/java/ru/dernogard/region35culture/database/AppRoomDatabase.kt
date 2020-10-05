package ru.dernogard.region35culture.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.dernogard.region35culture.database.dao.CultureObjectDao
import ru.dernogard.region35culture.database.models.CultureObject

@Database(entities = [CultureObject::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase: RoomDatabase() {

    abstract fun cultureObjectDao(): CultureObjectDao

}