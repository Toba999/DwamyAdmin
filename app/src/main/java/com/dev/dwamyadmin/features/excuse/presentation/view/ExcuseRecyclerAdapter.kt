package com.dev.dwamyadmin.features.excuse.presentation.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.ExcuseItemBinding
import com.dev.dwamyadmin.domain.models.ExcuseRequest
import com.dev.dwamyadmin.domain.models.ExcuseStatus
import com.dev.dwamyadmin.domain.models.ExcuseType

class ExcuseAdapter(
    private val excuseItems: MutableList<ExcuseRequest>, // Use MutableList for dynamic updates
    private val onAcceptClickListener: (ExcuseRequest, Int) -> Unit, // Pass position for updates
    private val onDeclineClickListener: (ExcuseRequest, Int) -> Unit // Pass position for updates
) : RecyclerView.Adapter<ExcuseAdapter.ExcuseViewHolder>() {

    inner class ExcuseViewHolder(private val binding: ExcuseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(excuseItem: ExcuseRequest) {
            binding.execuseTimestamp.text = excuseItem.issuedDate
            binding.execuseTitle.text = when (excuseItem.excuseType) {
                ExcuseType.LATE -> "طلب تاخير"
                ExcuseType.EARLY_LEAVE -> "طلب انصراف مبكر"
            }
            binding.execuseDateFrom.text = excuseItem.excuseDateFrom
            binding.execuseDateTo.text = excuseItem.excuseDateTo
            binding.execuseDate.text = excuseItem.excuseDate
            binding.execuseAplName.text = excuseItem.employeeName
            binding.execuseStatus.text = when (excuseItem.status) {
                ExcuseStatus.PENDING -> "قيد الانتظار"
                ExcuseStatus.ACCEPTED -> "مقبول"
                ExcuseStatus.REJECTED -> "مرفوض"
            }

            // Set initial visibility of buttons based on the status
            when (excuseItem.status) {
                ExcuseStatus.ACCEPTED -> {
                    binding.acceptExecuseBtn.visibility = View.GONE
                    binding.declineExecuseBtn.visibility = View.GONE
                    binding.execuseStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                }
                ExcuseStatus.REJECTED -> {
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
    fun updateItemStatus(position: Int, newStatus: ExcuseStatus) {
        if (position in 0 until excuseItems.size) {
            excuseItems[position].status = newStatus
            notifyItemChanged(position)
        }
    }
    fun updateList(newList: List<ExcuseRequest>) {
        excuseItems.clear()
        excuseItems.addAll(newList)
        notifyDataSetChanged()
    }
}
