package ru.dernogard.region35culture.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import ru.dernogard.region35culture.database.models.CultureObjectResponse

/**
 * Api interface for retrofit
 */

interface CultureObjectApi {

    @GET("json.php")
    fun getData(@Query("id_dataset") idDataset: String): Observable<List<CultureObjectResponse>>

}