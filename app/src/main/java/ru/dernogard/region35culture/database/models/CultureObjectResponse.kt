package ru.dernogard.region35culture.database.models

import com.google.gson.annotations.SerializedName

/**
 * This class using for representation server response from Internet
 * https://www.opendata.gov35.ru/datasets/json.php?id_dataset=111504
 */

data class CultureObjectResponse (
    @SerializedName("\uFEFF№ п.п.") val number: String,
    @SerializedName("Наименование объекта") val title: String,
    @SerializedName("Адрес объекта в соответствии  актом органа государственной власти о постановке под госохрану") val addressGov: String,
    @SerializedName("Адрес объекта уточнённый") val address: String,
    @SerializedName("Документ о   постановке под   государственную охрану") val documentName: String,
    @SerializedName("Широта") val latitude: String,
    @SerializedName("Долгота") val longitude: String,
    @SerializedName("Вид объекта") val type: String,
    @SerializedName("Координата границы территории   МСК 35 X") val borderX: String,
    @SerializedName("Координата границы территории МСК 35 Y") val borderY: String
)