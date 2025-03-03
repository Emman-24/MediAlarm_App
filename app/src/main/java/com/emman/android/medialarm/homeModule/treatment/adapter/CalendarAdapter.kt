package com.emman.android.medialarm.homeModule.treatment.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.data.local.DayItem
import com.emman.android.medialarm.databinding.ItemCalendarDayBinding

class CalendarAdapter(
    private val onDayClick: (DayItem) -> Unit
) : ListAdapter<DayItem, CalendarViewHolder>(DayItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding, onDayClick)
    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class CalendarViewHolder(
    private val binding: ItemCalendarDayBinding,
    private val onDayClick: (DayItem) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dayItem: DayItem) {
        binding.apply {
            tvCalendarDate.text = dayItem.calendarDate
            tvCalendarDay.text = dayItem.calendarDay
            root.isSelected = dayItem.isSelected
            root.backgroundTintList = ColorStateList.valueOf(
                when {
                    dayItem.isSelected -> Color.BLUE
                    else -> Color.TRANSPARENT
                }
            )
            root.setOnClickListener {
                if (!dayItem.isSelected) {
                    onDayClick(dayItem)
                }
            }

        }

    }
}

class DayItemDiffCallback : DiffUtil.ItemCallback<DayItem>() {

    override fun areItemsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
        return oldItem.calendarDate == newItem.calendarDate
    }

    override fun areContentsTheSame(oldItem: DayItem, newItem: DayItem): Boolean {
        return oldItem == newItem
    }

}
