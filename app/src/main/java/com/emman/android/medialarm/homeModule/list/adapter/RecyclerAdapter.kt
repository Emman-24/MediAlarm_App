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

    //    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
//        return MedicineViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.list_item_medicine, parent, false)
//        )
//    }
//
//    override fun getItemCount(): Int = listMedicines.size
//
//    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
//        val medicine = listMedicines[position]
//        holder.title.text = medicine.name
//        holder.dosage.text = medicine.dosage
//        holder.unit.text = medicine.unit
//        holder.frecuency.text = medicine.frequency
//        holder.pharmaceuticalForm.text = medicine.pharmaceuticalForm
//    }
//
//    class MedicineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val title = view.findViewById<TextView>(R.id.tvMedicineTitle)
//        val dosage = view.findViewById<TextView>(R.id.tvDosage)
//        val unit = view.findViewById<TextView>(R.id.tvUnit)
//        val frecuency = view.findViewById<TextView>(R.id.tvFrecuency)
//        val pharmaceuticalForm = view.findViewById<TextView>(R.id.tvPharmaceuticalForm)
//    }
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