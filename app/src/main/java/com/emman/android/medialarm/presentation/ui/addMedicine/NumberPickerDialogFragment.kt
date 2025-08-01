package com.emman.android.medialarm.presentation.ui.addMedicine

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.emman.android.medialarm.R


class NumberPickerDialogFragment : DialogFragment() {


    interface NumberPickerListener {
        fun onValuesSelected(intakeDays: Int, pauseDays: Int)
    }

    private var listener: NumberPickerListener? = null
    private var initialIntakeDays: Int = 90
    private var initialPauseDays: Int = 90

    fun setNumberPickerListener(listener: NumberPickerListener) {
        this.listener = listener
    }

    fun setInitialValues(intakeDays: Int, pauseDays: Int) {
        this.initialIntakeDays = intakeDays
        this.initialPauseDays = pauseDays
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_number_picker_dialog, null)

        val numberPickerIntake = dialogView.findViewById<NumberPicker>(R.id.numberPickerIntake)
        val numberPickerPause = dialogView.findViewById<NumberPicker>(R.id.numberPickerPause)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        numberPickerIntake.minValue = 1
        numberPickerIntake.maxValue = 100
        numberPickerIntake.value = initialIntakeDays

        numberPickerPause.minValue = 1
        numberPickerPause.maxValue = 100
        numberPickerPause.value = initialPauseDays

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = builder.create()

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnOk.setOnClickListener {
            listener?.onValuesSelected(numberPickerIntake.value, numberPickerPause.value)
            dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog

    }


}
