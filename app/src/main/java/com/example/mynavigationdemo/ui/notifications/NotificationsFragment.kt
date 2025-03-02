package com.example.mynavigationdemo.ui.notifications

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mynavigationdemo.R
import com.example.mynavigationdemo.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // 应用视图绑定特性
    //private lateinit var binding: FragmentAudioPlayerBinding
    // 媒体播放器
    private lateinit var mediaPlayer: MediaPlayer
    // 音频资源ID列表
    private val audioTracks = listOf(R.raw.cloud, R.raw.stream, R.raw.deepsleep, R.raw.fire, R.raw.lin_an, R.raw.night, R.raw.rain, R.raw.ripplingwheat) // replace with your audio files
    private val audioTrackNames = listOf("Cloud", "Stream", "Deepsleep", "Fire", "Lin_an", "Night", "Rain", "Ripplingwheat") // corresponding names
    // 当前播放的音频索引
    private var selectedTrackIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //全屏
        getActivity()?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 实例化媒体播放器
        mediaPlayer = MediaPlayer.create(context, audioTracks[selectedTrackIndex])

        // 设置Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, audioTrackNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTracks.adapter = adapter
        binding.spinnerTracks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTrackIndex = position
                binding.lblInfo.text = "Selected: ${audioTrackNames[position]}"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        mediaPlayer.setOnCompletionListener {
            if (!mediaPlayer.isLooping) {
                binding.lblInfo.text = "音效播放结束。"
                binding.btnPlay.isEnabled = true
            }
        }

        binding.btnPlay.setOnClickListener {
            it.isEnabled = false
            playSound()
        }

        binding.btnStop.setOnClickListener {
            stopSound()
        }

        binding.checkBoxLoop.setOnCheckedChangeListener { _, isChecked ->
            mediaPlayer.isLooping = isChecked
        }
    }

    // 播放音乐，如果当前正在播放，则从头开始
    private fun playSound() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(context, audioTracks[selectedTrackIndex])
        mediaPlayer.isLooping = binding.checkBoxLoop.isChecked
        mediaPlayer.setOnCompletionListener {
            if (!mediaPlayer.isLooping) {
                binding.lblInfo.text = "音效播放结束。"
                binding.btnPlay.isEnabled = true
            }
        }
        binding.lblInfo.text = "音效正在播放……"
        mediaPlayer.start()
    }

    // 停止音乐播放
    private fun stopSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.reset()
            binding.lblInfo.text = "播放停止。"
            binding.btnPlay.isEnabled = true
        }
// 重新初始化 MediaPlayer，准备下次播放
        mediaPlayer = MediaPlayer.create(context, audioTracks[selectedTrackIndex])    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}