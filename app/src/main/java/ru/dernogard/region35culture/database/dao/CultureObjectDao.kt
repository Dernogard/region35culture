package ru.dernogard.region35culture.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.dernogard.region35culture.database.models.CultureObject

@Dao
interface CultureObjectDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveAll(list: List<CultureObject>)

    @Query("SELECT * FROM CultureObject")
    fun getAllObservable(): Observable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE type=:objectType")
    fun getByGroupObservable(objectType: String): Observable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE title LIKE '%'||:query||'%'")
    fun getByNameFlowable(query: String): Observable<List<CultureObject>>

    @Query("SELECT * FROM CultureObject WHERE id=:id")
    fun findByIdSingle(id: Long): Single<CultureObject>

    @Query("SELECT type FROM CultureObject WHERE LENGTH(type) > 1 GROUP BY type")
    fun getAllGroupObservable(): Observable<List<String>>

    @Query("SELECT * FROM CultureObject")
    fun getAllList(): List<CultureObject>

}