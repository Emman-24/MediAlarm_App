package com.emman.android.medialarm.homeModule.list.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.data.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository
) : ViewModel() {

    val listMedicines : LiveData<List<Medicine>> = liveData {
        emit(medicineRepository.getActiveMedicines())
    }

    private val _navigateToDetail = MutableLiveData<Long?>()
    val navigateToDetail: LiveData<Long?>
        get() = _navigateToDetail

    fun onMedicineClicked(id: Long) {
        _navigateToDetail.value = id
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }


}