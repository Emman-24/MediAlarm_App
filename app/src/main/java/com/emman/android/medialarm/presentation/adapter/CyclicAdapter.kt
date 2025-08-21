package com.emman.android.medialarm.presentation.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ListItemTimeBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CyclicAdapter(
    private val onTimeClicked: (item: MedicationTime, position: Int) -> Unit,
) : ListAdapter<MedicationTime, CyclicAdapter.ViewHolder>(MedicationTimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun updateMedicationTime(position: Int, medicationTime: MedicationTime) {
        if (position >= 0 && position < currentList.size) {
            val currentList = currentList.toMutableList()
            currentList[position] = medicationTime
            submitList(currentList)
        }
    }

    fun addMedicationTime(newMedicationTime: MedicationTime? = null) {
        val timeToAdd = newMedicationTime ?: MedicationTime(
            time = LocalTime.now(),
            amount = 1.0
        )
        val currentList = currentList.toMutableList()
        currentList.add(timeToAdd)
        submitList(currentList)
    }

    fun removeMedicationTime(position: Int) {
        if (position in currentList.indices && currentList.size > 1) {
            val currentList = currentList.toMutableList()
            currentList.removeAt(position)
            submitList(currentList)
        }
    }

    fun setMedicationTimes(medicationTimes: List<MedicationTime>) {
        submitList(medicationTimes)
    }

    fun getMedicationTimes(): List<MedicationTime> {
        return currentList
    }


    inner class ViewHolder(
        private val binding: ListItemTimeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var amountTextWatcher: TextWatcher? = null

        init {
            binding.btnTime.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onTimeClicked(getItem(bindingAdapterPosition), bindingAdapterPosition)
                }
            }

            binding.btnRemove.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    removeMedicationTime(bindingAdapterPosition)
                }
            }
        }

        fun bind(item: MedicationTime) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            binding.btnTime.text = item.time.format(formatter)

            if (amountTextWatcher != null) {
                binding.amountEditText.removeTextChangedListener(amountTextWatcher)
            }

            binding.amountEditText.setText(item.amount.toString())

            amountTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    val position = bindingAdapterPosition
                    if (s != null && position != RecyclerView.NO_POSITION) {
                        val text = s.toString()
                        if (text.isNotEmpty()) {
                            try {
                                val newAmount = text.toDouble()
                                val currentItem = getItem(position)
                                val updatedItem = currentItem.copy(amount = newAmount)
                                updateMedicationTime(position, updatedItem)
                            } catch (e: NumberFormatException) {
                                // Invalid input, do nothing
                            }
                        }
                    }
                }
            }

            binding.amountEditText.addTextChangedListener(amountTextWatcher)
            binding.btnRemove.isEnabled = currentList.size > 1
        }
    }
}

class MedicationTimeDiffCallback : DiffUtil.ItemCallback<MedicationTime>() {
    override fun areItemsTheSame(
        oldItem: MedicationTime,
        newItem: MedicationTime,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MedicationTime,
        newItem: MedicationTime,
    ): Boolean {
        return oldItem == newItem
    }

}
