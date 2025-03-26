package com.dev.dwamyadmin.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateAndTimePicker {
    fun showDatePicker(editText: EditText,context: Context) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        datePicker.show()
    }

    fun showTimePicker(editText: EditText,context: Context) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedTime.set(Calendar.MINUTE, selectedMinute)

                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                editText.setText(timeFormat.format(selectedTime.time))
            },
            hour, minute, false
        )

        timePickerDialog.show()
    }

    fun Long.toFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
        return sdf.format(Date(this * 1000)) // Convert seconds to milliseconds
    }

    fun Long.toDayOfWeek(): String {
        val sdf = SimpleDateFormat("EEEE", Locale("ar"))
        return sdf.format(Date(this * 1000)) // Convert seconds to milliseconds
    }
}