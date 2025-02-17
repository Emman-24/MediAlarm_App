package com.emman.android.medialarm.homeModule.list.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.data.local.Data

class ListViewModel : ViewModel() {

    val listMedicines = Data().listMedicines

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