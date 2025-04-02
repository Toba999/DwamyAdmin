package com.dev.dwamyadmin.features.vacation.presentation.view
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.VacationItemBinding
import com.dev.dwamyadmin.domain.models.LeaveRequest
import com.dev.dwamyadmin.domain.models.LeaveStatus
import com.dev.dwamyadmin.domain.models.toArabic

class VacationAdapter(
    private val vacationItems: MutableList<LeaveRequest>,
    private val onAcceptClickListener: (LeaveRequest, Int) -> Unit,
    private val onDeclineClickListener: (LeaveRequest, Int) -> Unit
) : RecyclerView.Adapter<VacationAdapter.VacationViewHolder>() {

    inner class VacationViewHolder(private val binding: VacationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vacationItem: LeaveRequest) {
            binding.timestamp.text = vacationItem.issuedDate
            binding.title.text = vacationItem.requestType
            binding.descTv.text = vacationItem.reason
            binding.dateFrom.text = vacationItem.dayFrom
            binding.dateTo.text = vacationItem.dayTo
            binding.vacAplName.text = vacationItem.employeeName
            binding.status.text = vacationItem.status.toArabic()

            when (vacationItem.status) {
                LeaveStatus.ACCEPTED -> {
                    binding.acceptVacBtn.visibility = View.GONE
                    binding.declineVacBtn.visibility = View.GONE
                    binding.status.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                }
                LeaveStatus.REJECTED -> {
                    binding.acceptVacBtn.visibility = View.GONE
                    binding.declineVacBtn.visibility = View.GONE
                    binding.status.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                }
                else -> {
                    binding.acceptVacBtn.visibility = View.VISIBLE
                    binding.declineVacBtn.visibility = View.VISIBLE
                    binding.status.setTextColor(binding.root.context.getColor(android.R.color.darker_gray))
                }
            }

            binding.acceptVacBtn.setOnClickListener {
                onAcceptClickListener(vacationItem, adapterPosition)
            }

            binding.declineVacBtn.setOnClickListener {
                onDeclineClickListener(vacationItem, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = VacationItemBinding.inflate(inflater, parent, false)
        return VacationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VacationViewHolder, position: Int) {
        holder.bind(vacationItems[position])
    }

    override fun getItemCount(): Int {
        return vacationItems.size
    }

    fun updateItemStatus(position: Int, newStatus: LeaveStatus) {
        if (position in 0 until vacationItems.size) {
            vacationItems[position].status = newStatus
            notifyItemChanged(position)
        }
    }
    fun updateList(newList: List<LeaveRequest>) {
        vacationItems.clear()
        vacationItems.addAll(newList)
        notifyDataSetChanged()
    }
}

