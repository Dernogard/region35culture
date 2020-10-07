package ru.dernogard.region35culture.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Single
import ru.dernogard.region35culture.database.models.CultureObject

@Dao
interface CultureObjectDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveAll(list: List<CultureObject>)

    @Query("SELECT * FROM CultureObject")
    fun getAllFlowable(): Flowable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE type=:objectType")
    fun getByGroupFlowable(objectType: String): Flowable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE title LIKE '%'||:query||'%'")
    fun getByNameFlowable(query: String): Flowable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE id=:id")
    fun findByIdSingle(id: Long): Single<CultureObject>

    // Some culture object don't have any group. That's why we using length filter
    @Query("SELECT type FROM CultureObject WHERE LENGTH(type) > 1 GROUP BY type")
    fun getAllGroupFlowable(): Flowable<List<String>>

}