package com.emman.android.medialarm.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ListItemScheduleBinding
import com.emman.android.medialarm.presentation.viewmodels.MedicineUiItem

class ScheduleAdapter(
    private val onMedicineClicked: (Long) -> Unit,
) : ListAdapter<MedicineUiItem, ScheduleAdapter.MedicineViewHolder>(ScheduleDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MedicineViewHolder {
        val binding = ListItemScheduleBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicineViewHolder(binding, onMedicineClicked)
    }

    override fun onBindViewHolder(
        holder: MedicineViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }


    class MedicineViewHolder(
        private val _binding: ListItemScheduleBinding,
        private val onMedicineClicked: (Long) -> Unit,
    ) : RecyclerView.ViewHolder(_binding.root) {
        fun bind(medicine: MedicineUiItem) {
            _binding.tvMedicineName.text = medicine.name
            _binding.textTime.text = medicine.scheduleText
            _binding.textDosage.text = medicine.dosage
            _binding.root.setOnClickListener {
                onMedicineClicked(medicine.id)
            }
        }
    }
}

class ScheduleDiffCallback : DiffUtil.ItemCallback<MedicineUiItem>() {
    override fun areItemsTheSame(
        oldItem: MedicineUiItem,
        newItem: MedicineUiItem,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MedicineUiItem,
        newItem: MedicineUiItem,
    ): Boolean {
        return oldItem == newItem
    }

}
