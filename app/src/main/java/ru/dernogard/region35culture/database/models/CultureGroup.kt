package ru.dernogard.region35culture.database.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for different culture's object types
 */

data class CultureGroup(
    val title: String
)