package com.emman.android.medialarm.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.databinding.ItemCalendarDayBinding
import com.emman.android.medialarm.domain.models.CalendarDay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarAdapter(
    private val onDayClicked: (LocalDate) -> Unit,
) : ListAdapter<CalendarDay, CalendarAdapter.DayViewHolder>(DayDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DayViewHolder(binding, onDayClicked)
    }

    override fun onBindViewHolder(
        holder: DayViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }


    class DayViewHolder(
        private val _binding: ItemCalendarDayBinding,
        private val onDayClicked: (LocalDate) -> Unit,
    ) : RecyclerView.ViewHolder(_binding.root) {

        fun bind(day: CalendarDay) {
            _binding.tvDayName.text =
                day.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            _binding.tvDayNumber.text = day.date.dayOfMonth.toString()
            _binding.llDayContainer.isSelected = day.isSelected

            _binding.root.setOnClickListener {
                onDayClicked(day.date)
            }
        }
    }
}

class DayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(
        oldItem: CalendarDay,
        newItem: CalendarDay,
    ): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(
        oldItem: CalendarDay,
        newItem: CalendarDay,
    ): Boolean {
        return oldItem == newItem
    }

}