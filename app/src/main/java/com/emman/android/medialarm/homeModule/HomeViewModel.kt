package com.emman.android.medialarm.homeModule

import androidx.lifecycle.ViewModel
import com.emman.android.medialarm.domain.CheckMedicinesListEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    checkMedicinesListEmpty: CheckMedicinesListEmpty
) : ViewModel() {

    val isMedicinesListEmpty: Boolean = checkMedicinesListEmpty()

}