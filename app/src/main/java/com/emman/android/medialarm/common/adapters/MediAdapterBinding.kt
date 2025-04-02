package com.emman.android.medialarm.common.adapters

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emman.android.medialarm.data.local.Medicine
import com.emman.android.medialarm.homeModule.treatment.adapter.TreatmentAdapter
import com.emman.android.medialarm.homeModule.treatment.viewModel.TreatmentStatus


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Medicine>?) {
    val adapter = recyclerView.adapter as TreatmentAdapter
    adapter.submitList(data)
}

@BindingAdapter("setAdapterTreatment")
fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: TreatmentAdapter) {
    adapter.let {
        recyclerView.adapter = it
    }
}

@BindingAdapter("setVisibility")
fun setVisibility(view: View, status: TreatmentStatus) {
    when (status) {
        TreatmentStatus.LOADING -> {
            view.visibility = View.GONE
        }

        TreatmentStatus.EMPTY -> {
            view.visibility = View.GONE
        }

        TreatmentStatus.DONE -> {
            view.visibility = View.VISIBLE
        }

        TreatmentStatus.ERROR -> {
            view.visibility = View.GONE
        }
    }
}



