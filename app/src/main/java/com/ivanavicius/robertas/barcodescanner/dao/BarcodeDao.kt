package com.ivanavicius.robertas.barcodescanner.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ivanavicius.robertas.barcodescanner.entities.Barcode

@Dao
interface BarcodeDao {

    @Query("SELECT 0 AS id, barcode, SUM(quantity) AS quantity, isChecked FROM Barcode GROUP BY barcode, isChecked ORDER BY isChecked")
    fun getAll(): List<Barcode>

    @Query("SELECT 0 AS id, barcode, SUM(quantity) AS quantity, isChecked FROM Barcode WHERE isChecked = 0 GROUP BY barcode, isChecked")
    fun getUndone(): List<Barcode>

    @Query("SELECT 0 AS id, barcode, SUM(quantity) AS quantity, isChecked FROM Barcode WHERE barcode=:barcode AND isChecked = 0 GROUP BY barcode, isChecked LIMIT 1")
    fun getQuantity(barcode: String): Barcode

    @Query("UPDATE Barcode SET isChecked = 1 WHERE barcode=:barcode AND isChecked = 0")
    suspend fun markAsDone(barcode: String)

    @Query("UPDATE Barcode SET isChecked = 0 WHERE isChecked = 1")
    suspend fun reset()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(barcode: Barcode)

    @Query("DELETE FROM Barcode")
    suspend fun deleteAll()

}