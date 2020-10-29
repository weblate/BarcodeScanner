package com.ivanavicius.robertas.barcodescanner.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Barcode(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val barcode: String,
    var quantity: Int,
    var isChecked: Boolean = false
)