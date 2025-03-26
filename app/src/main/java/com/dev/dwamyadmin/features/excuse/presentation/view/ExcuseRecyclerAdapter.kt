package com.dev.dwamyadmin.features.excuse.presentation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.ExcuseItemBinding
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.toExcuseItem
import com.dev.dwamyadmin.domain.models.toVacationItem

class ExcuseAdapter(
    private val excuseItems: MutableList<ExcuseItem>, // Use MutableList for dynamic updates
    private val onAcceptClickListener: (ExcuseItem, Int) -> Unit, // Pass position for updates
    private val onDeclineClickListener: (ExcuseItem, Int) -> Unit // Pass position for updates
) : RecyclerView.Adapter<ExcuseAdapter.ExcuseViewHolder>() {

    inner class ExcuseViewHolder(private val binding: ExcuseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(excuseItem: ExcuseItem) {
            binding.execuseTimestamp.text = excuseItem.timestamp
            binding.execuseTitle.text = excuseItem.title
            binding.execuseDateFrom.text = excuseItem.fromTime
            binding.execuseDateTo.text = excuseItem.toTime
            binding.execuseAplName.text = excuseItem.applicantName
            binding.execuseStatus.text = excuseItem.status

            // Set initial visibility of buttons based on the status
            when (excuseItem.status) {
                "مقبول" -> {
                    binding.acceptExecuseBtn.visibility = View.GONE
                    binding.declineExecuseBtn.visibility = View.GONE
                    binding.execuseStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                }
                "مرفوض" -> {
                    binding.acceptExecuseBtn.visibility = View.GONE
                    binding.declineExecuseBtn.visibility = View.GONE
                    binding.execuseStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                }
                else -> {
                    binding.acceptExecuseBtn.visibility = View.VISIBLE
                    binding.declineExecuseBtn.visibility = View.VISIBLE
                    binding.execuseStatus.setTextColor(binding.root.context.getColor(android.R.color.darker_gray))
                }
            }

            // Set click listeners for the buttons
            binding.acceptExecuseBtn.setOnClickListener {
                onAcceptClickListener(excuseItem, adapterPosition)
            }

            binding.declineExecuseBtn.setOnClickListener {
                onDeclineClickListener(excuseItem, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcuseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExcuseItemBinding.inflate(inflater, parent, false)
        return ExcuseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExcuseViewHolder, position: Int) {
        holder.bind(excuseItems[position])
    }

    override fun getItemCount(): Int {
        return excuseItems.size
    }

    // Function to update the status of an item
    fun updateItemStatus(position: Int, newStatus: String) {
        if (position in 0 until excuseItems.size) {
            excuseItems[position].status = newStatus
            notifyItemChanged(position)
        }
    }
    fun updateList(newList: List<ExcuseRequest>) {
        excuseItems.clear()
        val mappedList = newList.map{it.toExcuseItem()}
        excuseItems.addAll(mappedList)
        notifyDataSetChanged()
    }
}

data class ExcuseItem(
    val id: String,
    val timestamp: String,
    val title: String,
    val fromTime: String,
    val toTime: String,
    val applicantName: String,
    var status: String
)