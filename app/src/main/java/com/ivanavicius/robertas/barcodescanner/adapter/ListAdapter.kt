package com.ivanavicius.robertas.barcodescanner.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ivanavicius.robertas.barcodescanner.R
import com.ivanavicius.robertas.barcodescanner.entities.Barcode
import com.ivanavicius.robertas.barcodescanner.fragments.ListFragmentDirections
import com.ivanavicius.robertas.barcodescanner.toNumeric
import com.ivanavicius.robertas.barcodescanner.tryNavigate
import kotlinx.android.synthetic.main.adapter_list.view.*

class ListAdapter(private val activity: Activity, data: List<Barcode> = ArrayList()): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val _data: ArrayList<Barcode> = ArrayList(data)

    fun updateData(data: List<Barcode>){
        _data.clear()
        _data.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        _data.getOrNull(position)?.apply {
            holder.barcode.text = barcode
            holder.quantity.text = quantity.toNumeric()
            holder.itemView.isEnabled = !isChecked
        }
    }

    override fun getItemCount(): Int =
        _data.size

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v){
        val barcode = v.barcode!!
        val quantity = v.quantity!!

        init {
            v.setOnClickListener {
                _data.getOrNull(adapterPosition)?.let {
                    if (!it.isChecked)
                        activity.tryNavigate(ListFragmentDirections.actionListFragmentToBarcodeFragment(it.barcode))
                }
            }
        }

    }
}
