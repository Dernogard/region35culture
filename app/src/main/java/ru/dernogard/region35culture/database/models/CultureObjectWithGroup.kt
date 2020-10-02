package ru.dernogard.region35culture.database.models

import androidx.room.Embedded
import androidx.room.Relation

data class CultureObjectWithGroup(
    @Embedded val cultureObject: CultureObject,

)