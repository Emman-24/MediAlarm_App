package com.emman.android.medialarm.createModule.searchView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.createModule.searchView.SearchAdapter.SearchViewHolder
import com.emman.android.medialarm.data.local.MedicineX
import com.emman.android.medialarm.databinding.ItemViewBinding


class SearchAdapter(
    private val onItemClicked: (MedicineX) -> Unit,
) : ListAdapter<MedicineX, SearchViewHolder>(SearchDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val medicine = getItem(position)
        holder.bind(medicine)
    }


    inner class SearchViewHolder(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: MedicineX) {
            binding.tvMedicineName.text = medicine.medicineName
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedMedication = getItem(position)
                    onItemClicked(clickedMedication)
                }
            }
        }
    }


}

class SearchDiffCallback : DiffUtil.ItemCallback<MedicineX>() {
    override fun areItemsTheSame(
        oldItem: MedicineX,
        newItem: MedicineX,
    ): Boolean {
        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(
        oldItem: MedicineX,
        newItem: MedicineX,
    ): Boolean {
        return oldItem == newItem
    }

}
