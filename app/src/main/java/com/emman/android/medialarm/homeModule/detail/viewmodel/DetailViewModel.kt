package com.emman.android.medialarm.homeModule.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.data.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository
) : ViewModel() {

    private val _medicine = MutableLiveData<Medicine>()
    var medicine: MutableLiveData<Medicine> = _medicine

    fun getMedicineById(medicineId: Long) {
        _medicine.value = medicineRepository.getMedicineById(medicineId)
    }


}