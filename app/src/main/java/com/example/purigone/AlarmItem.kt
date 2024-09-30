package com.example.purigone
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class AlarmItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val switch: Switch
    private val timeText: TextView
    private var alarmTime: Calendar = Calendar.getInstance()

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.alarm_item, this, true)

        switch = findViewById(R.id.alarmSwitch)
        timeText = findViewById(R.id.alarmTime)

        updateTimeDisplay()

        timeText.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun updateTimeDisplay() {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        timeText.text = sdf.format(alarmTime.time)
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                alarmTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                alarmTime.set(Calendar.MINUTE, minute)
                updateTimeDisplay()
            },
            alarmTime.get(Calendar.HOUR_OF_DAY),
            alarmTime.get(Calendar.MINUTE),
            true
        ).show()
    }

    fun isAlarmOn(): Boolean = switch.isChecked

    fun getAlarmTime(): Date = alarmTime.time
}