package com.ivanavicius.robertas.barcodescanner.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.ivanavicius.robertas.barcodescanner.R
import com.ivanavicius.robertas.barcodescanner.adapter.ListAdapter
import com.ivanavicius.robertas.barcodescanner.getViewModel
import com.ivanavicius.robertas.barcodescanner.hideKeyboard
import com.ivanavicius.robertas.barcodescanner.showKeyboard
import com.ivanavicius.robertas.barcodescanner.viewModels.BarcodeViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment: Fragment() {

    private val viewModel: BarcodeViewModel by lazy { getViewModel(this) }
    private val adapter: ListAdapter by lazy { ListAdapter(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter

        viewModel.barcodes.observe(viewLifecycleOwner, {
            adapter.updateData(it)
        })
        viewModel.getAll()

        add.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128, IntentIntegrator.CODE_39, IntentIntegrator.EAN_13)
            integrator.setPrompt("")
            integrator.setBeepEnabled(false)
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.clear -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Clear all info")
                    .setMessage("Are you sure what to remove all items?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.removeAllItems()
                    }
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null){
            val barcode = result.contents
            if (barcode != null){
                enterQuantity(barcode)
            }
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("InflateParams")
    private fun enterQuantity(barcode: String){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.view_enter_quantity, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Enter quantity for barcode: $barcode")
            .setNegativeButton("Cancel"){ _, _ -> requireActivity().hideKeyboard()}
            .setPositiveButton("Add", null)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        dialogView.findViewById<TextInputLayout>(R.id.quantityLayout).editText?.apply {
            requestFocus()
            setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_GO){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).callOnClick()
                    true
                } else
                    false
            }
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialogView.findViewById<TextInputLayout>(R.id.quantityLayout).apply {
                editText?.text?.toString()?.toIntOrNull().let {
                    if (it != null && it > 0) {
                        dialog.dismiss()
                        viewModel.addBarcode(barcode, it)
                        requireActivity().hideKeyboard()
                    } else
                        error = "Must be greater than 0"
                }
            }
        }

        requireActivity().showKeyboard()

    }

}