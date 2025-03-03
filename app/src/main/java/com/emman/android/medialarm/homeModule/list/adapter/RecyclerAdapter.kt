package com.emman.android.medialarm.homeModule.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.databinding.ListItemMedicineBinding

class RecyclerAdapter(
    private val clickListener: RecyclerListener

) : ListAdapter<Medicine, RecyclerAdapter.ViewHolder>(RecyclerDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListItemMedicineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Medicine, clickListener: RecyclerListener) {
            binding.medicine = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemMedicineBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

class RecyclerDiffCallback : DiffUtil.ItemCallback<Medicine>() {
    override fun areItemsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Medicine, newItem: Medicine): Boolean {
        return oldItem == newItem
    }
}

class RecyclerListener(val clickListener: (medicineId: Long) -> Unit) {
    fun onClick(medicine: Medicine) = clickListener(medicine.id)
}