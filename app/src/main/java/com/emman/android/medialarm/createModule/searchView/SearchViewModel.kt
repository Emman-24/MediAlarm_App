package com.emman.android.medialarm.createModule.searchView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emman.android.medialarm.data.local.MedicineX
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val allMedications = listOf(
        MedicineX(
            _id = "67bb30e0eb6beebe41a637f4",
            acceleratedAssessment = false,
            activeSubstance = "Daptomycin",
            additionalMonitoring = false,
            advancedTherapy = false,
            atcCode = "J01XX09",
            atcVetCode = null,
            biosimilar = false,
            category = "Human",
            conditionalApproval = false,
            emaProductNumber = "EMEA/H/C/004310",
            europeanCommissionDecisionDate = "12/02/2025",
            exceptionalCircumstances = false,
            firstPublishedDate = "13/04/2018",
            genericOrHybrid = true,
            humanPharmacotherapeuticGroup = "Antibacterials",
            inn = "Daptomycin",
            lastUpdatedDate = "17/02/2025",
            latestProcedureAffectingProductInformation = "N/0000245626",
            marketingAuthorizationDate = "22/03/2017",
            marketingAuthorizationHolder = "Pfizer Europe",
            medicineName = "Daptomycin Hospira",
            medicineStatus = "Authorised",
            medicineUrl = "https://example.com",
            opinionAdoptedDate = "26/01/2017",
            opinionStatus = null,
            orphanMedicine = false,
            patientSafety = false,
            primePriorityMedicine = false,
            refusalOfMarketingAuthorizationDate = null,
            revisionNumber = "14",
            species = null,
            startOfEvaluationDate = null,
            startOfRollingReviewDate = null,
            suspensionOfMarketingAuthorizationDate = null,
            therapeuticArea = "Soft Tissue Infections",
            therapeuticIndication = "Treats bacterial infections",
            veterinaryPharmacotherapeuticGroup = null,
            withdrawalExpiryRevocationLapseOfMarketingAuthorizationDate = null,
            withdrawalOfApplicationDate = null
        ),
        MedicineX(
            _id = "67bb30e0eb6beebe41a637f5",
            acceleratedAssessment = false,
            activeSubstance = "Daptomycin",
            additionalMonitoring = false,
            advancedTherapy = false,
            atcCode = "J01XX09",
            atcVetCode = null,
            biosimilar = false,
            category = "Human",
            conditionalApproval = false,
            emaProductNumber = "EMEA/H/C/004310",
            europeanCommissionDecisionDate = "12/02/2025",
            exceptionalCircumstances = false,
            firstPublishedDate = "13/04/2018",
            genericOrHybrid = true,
            humanPharmacotherapeuticGroup = "Antibacterials",
            inn = "Daptomycin",
            lastUpdatedDate = "17/02/2025",
            latestProcedureAffectingProductInformation = "N/0000245626",
            marketingAuthorizationDate = "22/03/2017",
            marketingAuthorizationHolder = "Pfizer Europe",
            medicineName = "Daptomycin Hospira",
            medicineStatus = "Authorised",
            medicineUrl = "https://example.com",
            opinionAdoptedDate = "26/01/2017",
            opinionStatus = null,
            orphanMedicine = false,
            patientSafety = false,
            primePriorityMedicine = false,
            refusalOfMarketingAuthorizationDate = null,
            revisionNumber = "14",
            species = null,
            startOfEvaluationDate = null,
            startOfRollingReviewDate = null,
            suspensionOfMarketingAuthorizationDate = null,
            therapeuticArea = "Soft Tissue Infections",
            therapeuticIndication = "Treats bacterial infections",
            veterinaryPharmacotherapeuticGroup = null,
            withdrawalExpiryRevocationLapseOfMarketingAuthorizationDate = null,
            withdrawalOfApplicationDate = null
        )
    )

    private val _searchResults = MutableLiveData<List<MedicineX>>()
    val searchResults: MutableLiveData<List<MedicineX>> = _searchResults


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _searchQuery = MutableLiveData<String>("")

    private var searchJob: Job? = null
    private val debouncePeriod = 300L

    init {
        _searchResults.value = emptyList()
    }

    fun searchMedications(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery == _searchQuery.value) {
            return
        }
        _searchQuery.value = trimmedQuery
        searchJob?.cancel()

        if (trimmedQuery.isEmpty()) {
            _isLoading.value = false
            _searchResults.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
            val results = allMedications.filter {
                it.medicineName.contains(trimmedQuery, ignoreCase = true)
            }
            _searchResults.postValue(results)
            _isLoading.value = false
        }
    }
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList() // Or reset to initial state
        searchJob?.cancel()
        _isLoading.value = false
    }
}