package com.dev.dwamyadmin.features.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.dev.dwamyadmin.databinding.FragmentDeleteDialogBinding
import com.dev.dwamyadmin.features.employeesList.presentation.viewModel.EmployeesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DeleteDialogFragment : DialogFragment() {

    private val viewModel: EmployeesViewModel by activityViewModels()
    private var _binding: FragmentDeleteDialogBinding? = null
    private val binding get() = _binding!!
    private val args: DeleteDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteEmployeeResult.collect { success ->
                success?.let {
                    if (success) {
                        showSnackBar("تم مسح الموظف بنجاح", false)
                    } else {
                        showSnackBar("فشل مسح الموظف", true)
                    }
                }
            }
        }
        binding.apply {
            btnOk.setOnClickListener {
                viewModel.deleteEmployee(args.employee.id)
                setFragmentResult("delete_request", Bundle().apply {
                    putBoolean("isDeleted", true)
                })
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun showSnackBar(message: String, isError: Boolean) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if (isError) {
            snackBarView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            snackBarView.setBackgroundColor(
                resources.getColor(
                    android.R.color.holo_green_dark,
                    null
                )
            )
        }
        snackBar.show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val margin = 40
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            setLayout(screenWidth - (margin * 2), ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = attributes
            layoutParams.dimAmount = 0.6f
            attributes = layoutParams
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
    }
}
