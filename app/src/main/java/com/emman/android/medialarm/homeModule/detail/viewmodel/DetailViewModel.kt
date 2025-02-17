package com.emman.android.medialarm.homeModule.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.emman.android.medialarm.data.local.Data
import com.emman.android.medialarm.data.local.Medicine

class DetailViewModel(
    private val medicineId: Long
) : ViewModel() {

    /*
    Search for medicine in database with medicineId and assign it to medicine
     */

    val data = Data().listMedicines

    private val _medicine = MutableLiveData<Medicine>()
    var medicine: MutableLiveData<Medicine> = _medicine

    private val _medicineName = MutableLiveData<String>()
    var medicineName: MutableLiveData<String> = _medicineName

    init {
        findMedicineById(medicineId)
    }

    private fun findMedicineById(id: Long) {
        data.map { list ->
            list.find { it.id == id }
        }.observeForever { foundMedicine ->
            foundMedicine?.let {
                _medicine.value = it
                _medicineName.value = it.name
            }
        }
    }




}