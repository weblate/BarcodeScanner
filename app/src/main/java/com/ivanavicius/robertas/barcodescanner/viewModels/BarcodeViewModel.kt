package com.ivanavicius.robertas.barcodescanner.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ivanavicius.robertas.barcodescanner.DataBase
import com.ivanavicius.robertas.barcodescanner.dao.BarcodeDao
import com.ivanavicius.robertas.barcodescanner.entities.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarcodeViewModel(application: Application): AndroidViewModel(application) {

    private val db: BarcodeDao by lazy { DataBase.getDatabase(application).barcodeDao() }

    val barcodes = MutableLiveData<List<Barcode>>()
    val nextBarcode = MutableLiveData<String?>()

    fun getAll(){
        CoroutineScope(Dispatchers.IO).launch {
            barcodes.postValue(db.getAll())
        }
    }

    fun addBarcode(barcode: String, quantity: Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.insert(Barcode(0, barcode, quantity))
            CoroutineScope(Dispatchers.Main).launch {
                getAll()
            }
        }
    }

    fun removeAllItems(){
        CoroutineScope(Dispatchers.IO).launch {
            db.deleteAll()
            barcodes.postValue(ArrayList())
        }
    }

    fun getInfo(barcode: String){
        CoroutineScope(Dispatchers.IO).launch {
            barcodes.postValue(listOf(db.getQuantity(barcode)))
        }
    }

    fun getNextBarcode(barcode: String){
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.getUndone()
            val index = list.indexOfLast { it.barcode == barcode }
            nextBarcode.postValue(
                list.getOrNull(index + 1)?.barcode?: if(index != 0) list.getOrNull(0)?.barcode else null
            )
        }
    }

    fun markAsDone(barcode: String): LiveData<Boolean>{
        val rez = MutableLiveData<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {
            db.markAsDone(barcode)
            rez.postValue(true)
        }
        return rez
    }

}