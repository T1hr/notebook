package com.example.mynavigationdemo.ui.dashboard

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mynavigationdemo.R
import com.example.mynavigationdemo.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var countdownViewModel: CountdownViewModel
    private lateinit var textViewCountdown: TextView
    private lateinit var editTextEventName: EditText
    private lateinit var editTextHours: EditText
    private lateinit var editTextMinutes: EditText
    private lateinit var editTextSeconds: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // 全屏
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        countdownViewModel=ViewModelProvider(this).get(CountdownViewModel::class.java)
        // 初始化视图组件
        textViewCountdown = root.findViewById(R.id.textViewCountdown)

            editTextEventName = root.findViewById(R.id.editTextEventName)
            editTextHours = root.findViewById(R.id.editTextHours)
            editTextMinutes = root.findViewById(R.id.editTextMinutes)
            editTextSeconds = root.findViewById(R.id.editTextSeconds)
            val buttonStart = root.findViewById<Button>(R.id.buttonStart)
            val buttonStop = root.findViewById<Button>(R.id.buttonStop)

            buttonStart.setOnClickListener {
                val eventName = editTextEventName.text.toString()
                val hoursInput = editTextHours.text.toString()
                val minutesInput = editTextMinutes.text.toString()
                val secondsInput = editTextSeconds.text.toString()

                if (hoursInput.isNotEmpty() || minutesInput.isNotEmpty() || secondsInput.isNotEmpty()) {
                    val hours = if (hoursInput.isEmpty()) 0 else hoursInput.toLong()
                    val minutes = if (minutesInput.isEmpty()) 0 else minutesInput.toLong()
                    val seconds = if (secondsInput.isEmpty()) 0 else secondsInput.toLong()
                    val timeInMillis = (hours * 3600 + minutes * 60 + seconds) * 1000
                    countdownViewModel.startCountdown(timeInMillis)
                }
            }

            buttonStop.setOnClickListener {
                countdownViewModel.stopCountdown()
            }

        // 观察 ViewModel 数据
        countdownViewModel.timeRemaining.observe(viewLifecycleOwner, Observer { millisUntilFinished ->
            val hours = millisUntilFinished / 1000 / 3600
            val minutes = (millisUntilFinished / 1000 % 3600) / 60
            val seconds = millisUntilFinished / 1000 % 60
            textViewCountdown.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        })

        countdownViewModel.isFinished.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished) {
                textViewCountdown.text = "Time's up!"
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



