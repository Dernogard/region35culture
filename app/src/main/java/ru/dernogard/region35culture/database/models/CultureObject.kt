package ru.dernogard.region35culture.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * https://www.opendata.gov35.ru/datasets/json.php?id_dataset=111504
 */

@Entity(indices = [Index(value = ["title"], unique = true)])
data class CultureObject(
    val number: Long,
    val title: String,
    val addressGov: String,
    val address: String,
    val documentName: String,
    val latitude: Float,
    val longitude: Float,
    val type: String,
    val borderX: String,
    val borderY: String
) {
    // We can use the object's number from API but it could changed
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CultureObject

        if (title != other.title) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

}