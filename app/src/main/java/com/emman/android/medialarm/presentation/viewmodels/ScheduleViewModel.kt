package com.emman.android.medialarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.domain.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val medicineRepository: MedicineRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineListUiState())
    val uiState: StateFlow<MedicineListUiState> = _uiState.asStateFlow()

    init {
        loadMedicines()
    }

    private fun loadMedicines() {

    }

}


/**
 * this data class should be in the Usercase layer and not in the presentation layer
 */
data class MedicineUiItem(
    val id: Long,
    val name: String,
    val dosage: String,
    val formIconRes: Int,
    val scheduleText: String,
    val nextIntakeTime: String?,
    val isTaken: Boolean,
)

data class MedicineListUiState(
    val medicines: List<MedicineUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
