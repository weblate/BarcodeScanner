package com.ivanavicius.robertas.barcodescanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.ivanavicius.robertas.barcodescanner.*
import com.ivanavicius.robertas.barcodescanner.viewModels.BarcodeViewModel
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.fragment_barcode.*
import java.lang.Exception

class BarcodeFragment: Fragment() {

    private val _barcode: String by lazy { BarcodeFragmentArgs.fromBundle(requireArguments()).barcode }
    private val viewModel: BarcodeViewModel by lazy { getViewModel(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_barcode, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments == null){
            requireActivity().onBackPressed()
            return
        }
        try {
            GlideApp.with(this)
                .load(BarcodeEncoder().encodeBitmap(_barcode, BarcodeFormat.EAN_13, 900, 300))
                .fitCenter()
                .into(barcode)
        } catch (e: Exception){
            e.printStackTrace()
            requireActivity().onBackPressed()
            return
        }

        barcodeText.text = _barcode

        viewModel.barcodes.observe(viewLifecycleOwner, {
            if (it.size == 1){
                quantity.text = it[0].quantity.toNumeric()
            }
        })
        viewModel.getInfo(_barcode)

        viewModel.nextBarcode.observe(viewLifecycleOwner, {
            skip.isVisible = it != null
        })
        viewModel.getNextBarcode(_barcode)

        done.setOnClickListener {
            viewModel.markAsDone(_barcode).observe(viewLifecycleOwner, {
                if (it && viewModel.nextBarcode.value != null)
                    requireActivity().tryNavigate(BarcodeFragmentDirections.actionBarcodeFragmentSelf(viewModel.nextBarcode.value!!))
                else
                    activity?.onBackPressed()
            })
        }

        skip.setOnClickListener {
            requireActivity().tryNavigate(BarcodeFragmentDirections.actionBarcodeFragmentSelf(viewModel.nextBarcode.value!!))
        }

    }

}