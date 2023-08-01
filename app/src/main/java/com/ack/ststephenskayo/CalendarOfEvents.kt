package com.ack.ststephenskayo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class CalendarOfEvents : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val calendarText = getCalendarText() // Replace this with your actual calendar text
        val textViewCalendar = findViewById<TextView>(R.id.textViewCalendar)
        textViewCalendar.text = calendarText
    }


    private fun getCalendarText(): String {
        val eventsCalendar = """
St. Stephen's Anglican Church Youth Calendar of Events 2023

**January**
- December Babies Birthday Celebration (Church)

**March**
- 3rd March Babies Birthday Celebration (Thursday)

**April**
- 2nd April Babies Birthday Celebration (Sunday)

**May**
- 18th May Babies Birthday Celebration (Church)
- May Babies Birthday Celebration (Church) (21st June, Monday)

**July**
- In June Obesity Awareness Month (Church)

**September**
- 3rd August Birthday Celebration (Tuesday)

**October**
- 1st September Birthday Celebration (Thursday)
- 14th Mentorship Session (Thursday)

**November**
- 5th October Birthday Celebration (Tuesday)

**December**
- 10th End of Year Party (Sunday)
""".trimIndent()

        return eventsCalendar
    }

}






