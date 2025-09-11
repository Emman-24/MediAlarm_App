package com.emman.android.medialarm.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ListItemScheduleBinding
import com.emman.android.medialarm.domain.models.MedicineScheduleState

class MedicineScheduleAdapter(
    val onItemClick: (id: Long) -> Unit,
) : ListAdapter<MedicineScheduleState, MedicineScheduleAdapter.MedicineViewHolder>(
    MedicineDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ListItemScheduleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicineViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MedicineViewHolder(
        private val binding: ListItemScheduleBinding,
        private val onItemClick: (id: Long) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: MedicineScheduleState) {
            binding.tvMedicineName.text = medicine.name
            binding.textDosage.text = "${medicine.dosage} ${medicine.formType}"
            binding.textTime.text = medicine.timeToTake

            binding.root.setOnClickListener {
                onItemClick(medicine.id)
            }
        }
    }
}

class MedicineDiffCallback : DiffUtil.ItemCallback<MedicineScheduleState>() {
    override fun areItemsTheSame(
        oldItem: MedicineScheduleState,
        newItem: MedicineScheduleState,
    ): Boolean {
        return oldItem.id == newItem.id && oldItem.timeToTake == newItem.timeToTake
    }

    override fun areContentsTheSame(
        oldItem: MedicineScheduleState,
        newItem: MedicineScheduleState,
    ): Boolean {
        return oldItem == newItem
    }
}
