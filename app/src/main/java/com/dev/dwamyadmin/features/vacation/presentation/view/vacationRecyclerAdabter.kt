package com.dev.dwamyadmin.features.vacation.presentation.view
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev.dwamyadmin.databinding.VacationItemBinding

class VacationAdapter(
    private val vacationItems: MutableList<VacationItem>, // Use MutableList for dynamic updates
    private val onAcceptClickListener: (VacationItem, Int) -> Unit, // Pass position for updates
    private val onDeclineClickListener: (VacationItem, Int) -> Unit // Pass position for updates
) : RecyclerView.Adapter<VacationAdapter.VacationViewHolder>() {

    inner class VacationViewHolder(private val binding: VacationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vacationItem: VacationItem) {
            binding.timestamp.text = vacationItem.timestamp
            binding.title.text = vacationItem.title
            binding.dateFrom.text = vacationItem.fromTime
            binding.dateTo.text = vacationItem.toTime
            binding.vacAplName.text = vacationItem.applicantName
            binding.status.text = vacationItem.status

            // Set initial visibility of buttons based on the status
            when (vacationItem.status) {
                "مقبول" -> {
                    binding.acceptVacBtn.visibility = View.GONE
                    binding.declineVacBtn.visibility = View.GONE
                    binding.status.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
                }
                "مرفوض" -> {
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

            // Set click listeners for the buttons
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

    // Function to update the status of an item
    fun updateItemStatus(position: Int, newStatus: String) {
        if (position in 0 until vacationItems.size) {
            vacationItems[position].status = newStatus
            notifyItemChanged(position)
        }
    }
}

data class VacationItem(
    val timestamp: String,
    val title: String,
    val fromTime: String,
    val toTime: String,
    val applicantName: String,
    var status: String
)