package com.emman.android.medialarm.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ListItemTimeBinding
import com.emman.android.medialarm.domain.models.MedicationTime
import java.time.format.DateTimeFormatter

class MultipleAdapter(
    private val times: Int,
    private val onTimeClicked: (item: MedicationTime, position: Int) -> Unit,
) : RecyclerView.Adapter<MultipleAdapter.ViewHolder>() {

    private val medicationTimes = MutableList(times) { 
        MedicationTime(
            time = java.time.LocalTime.now(),
            amount = 1.0
        )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ListItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(medicationTimes[position])
    }

    fun updateMedicationTime(position: Int, medicationTime: MedicationTime) {
        if (position in medicationTimes.indices) {
            medicationTimes[position] = medicationTime
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = times


    inner class ViewHolder(
        private val binding: ListItemTimeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MedicationTime) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            binding.btnTime.text = item.time.format(formatter)
            binding.amountEditText.setText(item.amount.toString())

            binding.btnTime.setOnClickListener {
                onTimeClicked(item, bindingAdapterPosition)
            }

        }
    }

}
