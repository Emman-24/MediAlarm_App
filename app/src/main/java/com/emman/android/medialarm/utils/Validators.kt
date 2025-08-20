package com.emman.android.medialarm.utils


sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errorMessage: String) : ValidationResult()
}


object Validators {
    fun notEmpty(value: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Invalid("This field cannot be empty")
        } else {
            ValidationResult.Valid
        }
    }

    fun isDosageDecimal(value: String): ValidationResult {
        return try {
            val dosage = value.toDouble()
            if (dosage <= 0) {
                ValidationResult.Invalid("Dosage must be greater than 0")
            } else {
                ValidationResult.Valid
            }
        } catch (e: NumberFormatException) {
            ValidationResult.Invalid("Invalid dosage format")
        }
    }

    fun isValidateUnit(value: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Invalid("Unit is required")
        } else {
            ValidationResult.Valid
        }
    }

    fun isValidateFormType(value: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Invalid("Form type is required")
        } else {
            ValidationResult.Valid
        }
    }

    fun isValidNote(value: String): ValidationResult {
        return if (value.length > 100) {
            ValidationResult.Invalid("Notes cannot exceed 100 characters")
        } else {
            ValidationResult.Valid
        }
    }


}
