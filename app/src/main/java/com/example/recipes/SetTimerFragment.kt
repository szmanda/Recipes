package com.example.recipes

import android.app.Dialog
import android.app.DialogFragment
import android.widget.TimePicker
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.util.*

class SetTimerFragment : Fragment(R.layout.fragment_set_timer), TimePickerDialog.OnTimeSetListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_set_timer, container, false)

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return v
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //TODO add this
    }
}