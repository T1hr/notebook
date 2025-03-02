package com.example.mynavigationdemo.ui.home;
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mynavigationdemo.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {

    private var sharedPreferences: SharedPreferences? = null
    private var planEditText: EditText? = null
    private var calendarView: CalendarView? = null
    private lateinit var textView: TextView
    private lateinit var deleteButton: ImageButton
    private lateinit var addButton: ImageButton
    private lateinit var clearButton: ImageButton
    private val CALENDAR_PERMISSION_REQUEST_CODE = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //全屏
        getActivity()?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 初始化SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // 初始化视图组件
        planEditText = view.findViewById(R.id.planEditText)
        calendarView = view.findViewById(R.id.calendarView)
        textView = view.findViewById(R.id.textView)
        deleteButton = view.findViewById(R.id.Delete)
        addButton = view.findViewById(R.id.Add)
        clearButton = view.findViewById(R.id.Clear)

        // 请求日历权限
        requestCalendarPermission()

        // 设置日历视图监听器
        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val selectedDate = sdf.format(selectedCalendar.time)

            // 从 SharedPreferences 中获取保存的事件列表
            val eventListJson = sharedPreferences?.getString(selectedDate, null)
            val gson = Gson()
            val type = object : TypeToken<List<String>>() {}.type
            val eventList = gson.fromJson<List<String>>(eventListJson, type)

            // 获取原有的计划列表
            val planList = getPlanList(selectedDate)

            deleteButton.setOnClickListener {
                val plan = planEditText?.text.toString()
                planList?.remove(plan)
                planList?.let { savePlanList(selectedDate, it) }
                Toast.makeText(requireContext(), "已删除 $selectedDate 的计划：$plan", Toast.LENGTH_SHORT).show()
            }

            addButton.setOnClickListener {
                val plan = planEditText?.text.toString()
                planList?.customAdd(plan)
                planList?.let { savePlanList(selectedDate, it) }
                Toast.makeText(requireContext(), "已保存 $selectedDate 的计划：$plan", Toast.LENGTH_SHORT).show()
            }

            clearButton.setOnClickListener {
                planList?.clear()
                planList?.let { savePlanList(selectedDate, it) }
                Toast.makeText(requireContext(), "已清空 $selectedDate 的计划", Toast.LENGTH_SHORT).show()
            }

            // 在 TextView 中显示事件列表
            if (eventList.isNullOrEmpty()) {
                textView.text = "没有事件"
            } else {
                val stringBuilder = StringBuilder()
                eventList.forEachIndexed { index, event ->
                    stringBuilder.append("事件 ${index + 1}: $event\n")
                }
                textView.text = stringBuilder.toString()
            }
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户授权了权限
                // 检查是否是第一次打开应用程序
                val isFirstTime = sharedPreferences?.getBoolean("isFirstTime", true)
                if (isFirstTime == true) {
                    // 第一次打开应用程序，读取手机日历事件
                    syncCalendarEvents()

                    // 更新标志为false，表示不是第一次打开应用程序
                    val editor = sharedPreferences?.edit()
                    if (editor != null) {
                        editor.putBoolean("isFirstTime", false)
                    }
                    if (editor != null) {
                        editor.apply()
                    }

                }
            } else {
                // 用户拒绝了权限请求，可以向用户解释为什么需要该权限
                Toast.makeText(
                    requireContext(),
                    "日历权限被拒绝，某些功能可能受限",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CALENDAR),
                CALENDAR_PERMISSION_REQUEST_CODE
            )
        } else {
            // 检查是否是第一次打开应用程序
            val isFirstTime = sharedPreferences?.getBoolean("isFirstTime", true)
            if (isFirstTime == true) {
                // 第一次打开应用程序，读取手机日历事件
                syncCalendarEvents()

                // 更新标志为false，表示不是第一次打开应用程序
                val editor = sharedPreferences?.edit()
                if (editor != null) {
                    editor.putBoolean("isFirstTime", false)
                }
                if (editor != null) {
                    editor.apply()
                }

            }
        }
    }

    private fun getPlanList(date: String): MutableList<String>? {
        val planListJson = sharedPreferences?.getString(date, null)
        return if (planListJson != null) {
            Gson().fromJson(planListJson, object : TypeToken<MutableList<String>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun savePlanList(date: String, planList: List<String>) {
        val planListJson = Gson().toJson(planList)
        val editor = sharedPreferences?.edit()
        editor?.putString(date, planListJson)
        editor?.apply()
    }

    private fun <E : Any> MutableList<E>?.customAdd(plan: E) {
        this?.add(plan)
    }

    @SuppressLint("Range")
    private fun syncCalendarEvents() {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val uri: Uri = CalendarContract.Events.CONTENT_URI
        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use { cursor ->
            while (cursor.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE))
                val startTime = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART))
                val endTime = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTEND))
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val startDate = sdf.format(startTime)
                val eventInfo = "$title\n开始时间: ${Date(startTime)}\n结束时间: ${Date(endTime)}"
                val gson = Gson()
                val eventListJson = sharedPreferences?.getString(startDate, null)
                val eventList: MutableList<String> = if (!eventListJson.isNullOrEmpty()) {
                    gson.fromJson(eventListJson, object : TypeToken<List<String>>() {}.type)
                } else {
                    mutableListOf()
                }
                eventList.add(eventInfo)
                val editor = sharedPreferences?.edit()
                editor?.putString(startDate, gson.toJson(eventList))
                editor?.apply()
            }
        }
    }
}
