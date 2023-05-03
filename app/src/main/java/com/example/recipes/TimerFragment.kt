package com.example.recipes

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.recipes.databinding.FragmentTimerBinding
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

class TimerFragment : Fragment(R.layout.fragment_timer) {
    private var time: Int = 0;
    private var _binding: FragmentTimerBinding? = null;
    lateinit var timeSetter: SetTimerFragment;
    private val timer = Timer();
    lateinit var timerViewModel: TimerViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same ViewModel instance created by the first activity.

        //val timerViewModel: TimerViewModel by viewModels()
        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                timerViewModel.uiState.collect {
//                    // Update UI elements
//                }
//            }
//        }
        println(timerViewModel);

//        timeSetter = viewModel.findViewById(R.id.timePicker)
//        timeSetter.setOnClickListener {
//            val newFragment = TimePickerFragment()
//            newFragment.show(fragmentManager, "timePicker")
//        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)
        val timerText = view.findViewById<TextView>(R.id.time)
        val startButton = view.findViewById<Button>(R.id.startButton)
        val stopButton = view.findViewById<Button>(R.id.stopButton)
        val resetButton = view.findViewById<Button>(R.id.resetButton)
        val vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timerViewModel.updateTimer()
                val state = timerViewModel.uiState.value as TimerUiState;
                val hours: Int = state.remainingTime.toHours().toInt() % 60;
                val minutes: Int = state.remainingTime.toMinutes().toInt() % 60;
                val seconds: Int = state.remainingTime.seconds.toInt() % 60;
                val newValue = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                if (timerText.text != newValue) {
                    activity?.runOnUiThread {
                        timerText.text = newValue;
                    }
                }
                // handle vibration
                if (state.remainingTime == Duration.ZERO && state.isSet) {
                    vibrator.vibrate(1000);
                }
            }
        }, 0, 1000)
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                updateTimerUi(timerViewModel);
//            }
//        }

        startButton.setOnClickListener {
            println("start button clicked")
            timerViewModel.startTimer()
        }

        stopButton.setOnClickListener {
            println("stop button clicked")
            timerViewModel.pauseTimer()
        }

        resetButton.setOnClickListener {
            println("reset button clicked")
            timerViewModel.resetTimer()
        }

        timerText.setOnClickListener {
            // Approach #1: Replacing the time fragment with my own
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            val timePickerFragment = SetTimerFragment()
//            transaction.replace(R.id.timer_fragment_container, timePickerFragment)
//            transaction.disallowAddToBackStack()
//            transaction.commit()

            // Approach #2: use build in time picker
            // pop a time picker dialog
            var timeSet: Int;
            val onSet: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener {
                timePicker, hour, minute ->
                timeSet = hour * 60 + minute;
                timerViewModel.setTimer(timeSet.toDuration(DurationUnit.MINUTES).toJavaDuration());
                println(timeSet);
            }
            var dialog = TimePickerDialog(context, onSet, 0, 0, true);
            dialog.show();
        }



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }
}