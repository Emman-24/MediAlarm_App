package com.emman.android.medialarm.homeModule.treatment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.data.local.IntakeAdvice
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.databinding.ItemMedicineTreatmentBinding
import com.emman.android.medialarm.homeModule.treatment.adapter.TreatmentAdapter.TreatmentViewHolder

class TreatmentAdapter : ListAdapter<Medicine, TreatmentViewHolder>(TreatmentDiffCallback()) {

    class TreatmentViewHolder(private var binding: ItemMedicineTreatmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: Medicine) {
            binding.tvMedicineName.text = medicine.name
            binding.tvMedicineTime.text = medicine.getHour
            if (medicine.intakeAdvice != IntakeAdvice.NONE) {
                binding.tvMedicineIntake.text = medicine.intakeAdvice.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatmentViewHolder {
        val binding = ItemMedicineTreatmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TreatmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TreatmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}




class TreatmentDiffCallback : DiffUtil.ItemCallback<Medicine>() {

    override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
        return oldItem == newItem
    }

}
