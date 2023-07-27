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
        return """
        January 2023
        - New Year's Day Celebration: January 1, 2023, 12:00 PM, Church Hall
        - Youth Fellowship Meeting: January 15, 2023, 3:00 PM, Youth Center
        - Community Outreach: January 22, 2023, 10:00 AM, Local Park

        February 2023
        - Valentine's Day Social: February 14, 2023, 7:00 PM, Church Garden
        - Bible Study Session: February 18, 2023, 6:30 PM, Fellowship Hall
        - Youth Choir Practice: February 25, 2023, 5:00 PM, Choir Room

        March 2023
        - Women's Day Celebration: March 8, 2023, 9:00 AM, Church Sanctuary
        - Sports Day Tournament: March 18, 2023, 2:00 PM, Sports Ground
        - Youth Leadership Training: March 25-26, 2023, 9:00 AM, Youth Center

        April 2023
        - Easter Egg Hunt: April 9, 2023, 10:30 AM, Church Grounds
        - Movie Night: April 15, 2023, 6:00 PM, Youth Center
        - Good Friday Service: April 21, 2023, 12:00 PM, Church Sanctuary

        May 2023
        - Mother's Day Brunch: May 14, 2023, 11:00 AM, Church Hall
        - Graduation Ceremony: May 20, 2023, 3:00 PM, Church Sanctuary
        - Youth Retreat: May 27-28, 2023, All Day, Campsite

        June 2023
        - Youth Talent Show: June 10, 2023, 7:00 PM, Fellowship Hall
        - Summer Picnic: June 17, 2023, 12:30 PM, Beach Park
        - Vacation Bible School: June 26-30, 2023, 9:00 AM, Youth Center

        July 2023
        - Independence Day Celebration: July 4, 2023, 5:00 PM, Church Grounds
        - Leadership Workshop: July 15, 2023, 9:30 AM, Youth Center
        - Youth Camping Trip: July 22-24, 2023, All Day, Campsite

        August 2023
        - Back-to-School Drive: August 6, 2023, 10:00 AM, Church Hall
        - Youth Sports Tournament: August 12, 2023, 2:00 PM, Sports Ground
        - Youth Mission Trip: August 20-25, 2023, All Day, Mission Location

        September 2023
        - Labor Day BBQ: September 4, 2023, 12:00 PM, Church Garden
        - Workshop on Financial Planning: September 16, 2023, 6:30 PM, Fellowship Hall
        - Youth Talent Development Program: September 23, 2023, 9:00 AM, Youth Center

        October 2023
        - Fall Festival: October 14, 2023, 3:00 PM, Church Grounds
        - Harvest Thanksgiving Service: October 22, 2023, 11:00 AM, Church Sanctuary
        - Youth Halloween Party: October 31, 2023, 6:00 PM, Youth Center

        November 2023
        - Youth Bible Quiz Competition: November 5, 2023, 2:30 PM, Fellowship Hall
        - Thanksgiving Community Outreach: November 18, 2023, 9:00 AM, Local Park
        - Youth Worship Night: November 24, 2023, 7:00 PM, Church Sanctuary

        December 2023
        - Christmas Carol Service: December 17, 2023, 5:30 PM, Church Sanctuary
        - Youth Christmas Party: December 23, 2023, 8:00 PM, Youth Center
        - Christmas Day Potluck: December 25, 2023, 12:30 PM, Church Hall
    """.trimIndent()
    }

}






