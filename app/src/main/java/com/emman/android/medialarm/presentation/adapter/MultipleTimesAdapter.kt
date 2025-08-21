package com.emman.android.medialarm.presentation.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ListItemTimeMultipleBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MultipleTimesAdapter(
    times: Int,
    private val onTimeClicked: (item: MedicationTime, position: Int) -> Unit,
) : ListAdapter<MedicationTime, MultipleTimesAdapter.ViewHolder>(MedicationTimeDiffCallback()) {

    init {
        // Initialize the adapter with a list of default MedicationTime objects
        val initialList = List(times) { 
            MedicationTime(
                time = LocalTime.now().plusHours(it.toLong()),
                amount = 1.0
            )
        }
        submitList(initialList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ListItemTimeMultipleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun getMedicationTimes(): List<MedicationTime> {
        return currentList
    }

    override fun getItemCount(): Int = currentList.size

    inner class ViewHolder(
        private val binding: ListItemTimeMultipleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var amountTextWatcher: TextWatcher? = null

        init {
            binding.btnTime.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onTimeClicked(getItem(bindingAdapterPosition), bindingAdapterPosition)
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
        }
    }
}
