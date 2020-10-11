package ru.dernogard.region35culture.api

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import ru.dernogard.region35culture.database.models.CultureObjectResponse

/**
 * Interface for retrofit
 */

internal interface CultureObjectApi {

    // id_dataset is pointer to required dataset on the server
    @GET("json.php")
    fun getData(@Query("id_dataset") idDataset: String): Observable<List<CultureObjectResponse>>

}