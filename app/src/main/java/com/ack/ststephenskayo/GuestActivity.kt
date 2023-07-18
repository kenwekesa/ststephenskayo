package com.ack.ststephenskayo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView

class GuestActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)

        expandableListView = findViewById(R.id.expandableListView)
        val adapter = MyExpandableListAdapter()
        expandableListView.setAdapter(adapter)
    }



    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
        private val groupList = listOf("KAYO", "Our Services", "Our activities")
        private val childList = listOf(
            listOf("KAYO means Kenya Anglican Youth Organisation. It was started in December 1961 at a Church leaders conference at Kahuhia one of the mission stations in Mt Kenya Central Diocese. Under the leadership of the late bishop Obadiah Kariuki, the Organisation started as Anglican Youth Organisation (A.Y.O) whose aim was; \n" +
                    "\n" +
                    "To help young people who converted to Christianity to grow in the Faith.\n" +
                    "To help young people win others for Christ.\n" +
                    "To prepare leaders both for the Church and the mission Schools.  \n" +
                    "KAYO is a key department that is strategically positioned to serve the young people within the anglican church structure right from the local Church to the Provincial Level for their Spiritual formation, empowerment and development. KAYO has a ministry to the young people both within and outside the Church.\n" +
                    "\n" +
                    "MOTTO: Wholesome health is is wealth for the youth (2nd John 1:1-2)\n" +
                    "VISION: A strengthened KAYO Department built on the foundation of the apostolic faith able to strive through the challenges that faced them.\n" +
                    "MISSION: To bring the young people to a living relationship with God through Jesus Christ by preaching, teaching, training, healing and social-economic transformation and enable them to live life in its fullness.  \n" +
                    "\n" +

                    "OBJECTIVES \n" +
                    " 1. To bring young people to know, love and serve Jesus Christ as Saviour and LORD.\n" +
                    "2. To build up young people in Christian faith, the Bible being the foundation.\n" +
                    " 3. To educate the young people in Christian character and to encourage them to be good leaders of our church and community. \n" +
                    " 4. To help the Church and parents understand their responsibility towards the young people.\n" +
                    " 5. To encourage young people to live responsible and meaningful lives, morally, socially, economically and politically.\n" +
                    "6. To work in close partnership with other Anglican Dioceses and organizations with sound Christian doctrine in Kenya and beyond.\n" +
                    "CORE VALUES:  \n" +
                    "1. Faith- without Faith it is impossible to please God.\n" +
                    "2. Excellence- our LORD is honored when our efforts are evidenced in care, wisdom, thoughtfulness, knowledge and skills.\n" +
                    "3. Holiness-we are committed to striving towards a personal and corporate lifestyle that is worthy of the holiness of God.\n" +
                    "4. Service- we value service to others in Christ’s Name which is given freely in response the love of God.\n" +
                    "5. Community- we have realized that Spiritual, social and economic development is accomplished within the context of a warm Christian community that together worships and glorify God.\n" +
                    "6. Integrity-We are committed to integrity in every aspect of our operation and pledge ourselves to the highest standard of truthfulness and obeying the law.\n" +
                    "7. Giving voice to the youth-we believe that when the youth are empowered, the Church and society remain strong and vibrant.\n" +
                    "8. Self-reliance- we realized that the youth should be empowered to be self-reliant by encouraging them work hard. Hence, the youth department values hard work and abhors laziness.  \n" +
                    "KAYO PRAYER:\n" +
                    "Teach me good LORD to serve you with all my strength, body, soul and mind. To love you and rejoice in you always. To offer my services without counting the cost. I pray that your Holy Spirit will give me courage, to ﬁght against the wicked Spiritual forces, by the help of your Word. To labor in your vineyard, looking forward to eternal life, in the Name of the Father, Son and the Holy Spirit. Amen.\n" +
                    "\n" +
                    "KAYO ANTHEM: \n" +
                    "Ee KAYO, mashahindi WA Yesu, Chama kiki, chenye kusudi Kubwa, kinawapendeza vijana, wasichana na wavulana, oh KAYO mashahidi wa Yesu x2."),
            listOf("Welcome to the Youth Department of ACK St. Stephens Bamburi! We are delighted to offer our order of services for you:\n" +
                    "\n" +
                    "English Service:\n" +
                    "\n" +
                    "Time: 7:00 am to 9:00 am\n" +
                    "Language: English\n" +
                    "Youth Service:\n" +
                    "\n" +
                    "Time: 9:00 am to 11:00 am\n" +
                    "Language: English\n" +
                    "Kiswahili Service:\n" +
                    "\n" +
                    "Time: 11:00 am to 1:00 pm\n" +
                    "Language: Kiswahili\n" +
                    "We invite you to join us in worship and fellowship during these designated service hours. Feel free to engage, participate, and connect with the vibrant community here at ACK St. Stephens Bamburi. If you have any questions or need further information, please don't hesitate to reach out to our Youth Department. We look forward to having you with us!"),
            listOf("As the Youth Department of ACK St. Stephens Bamburi, we organize various annual activities to engage, empower, and inspire our youth community. Here is an updated example of our annual activities:\n" +
                    "\n" +
                    "Sports Tournament:\n" +
                    "\n" +
                    "Description: A friendly competition where youth participate in various sports activities such as football, basketball, and volleyball.\n" +
                    "Date: March 20-21\n" +
                    "Location: Church sports grounds.\n" +
                    "Talent Show:\n" +
                    "\n" +
                    "Description: An exciting event showcasing the talents and creativity of our youth, including music, dance, poetry, and drama performances.\n" +
                    "Date: May 10\n" +
                    "Location: Church auditorium.\n" +
                    "Community Service Day:\n" +
                    "\n" +
                    "Description: A day dedicated to serving the community through activities like volunteering at local charities, cleaning up public spaces, or organizing donation drives.\n" +
                    "Date: September 5\n" +
                    "Location: Various locations within the community.\n" +
                    "Leadership Conference:\n" +
                    "\n" +
                    "Description: A conference aimed at equipping and empowering young leaders with valuable skills, knowledge, and inspiration.\n" +
                    "Date: November 15-17\n" +
                    "Location: Church conference hall.\n" +
                    "Youth Retreat:\n" +
                    "\n" +
                    "Description: A weekend retreat focused on spiritual growth, team-building, and personal development.\n" +
                    "Date: December 10-12\n" +
                    "Location: Retreat Center in a serene natural environment.\n" +
                    "Please note that these dates and activities are for illustrative purposes only. The actual annual activities and their specific dates may vary each year. Stay connected with our Youth Department to receive updated information and invitations to these exciting events throughout the year. We look forward to your active participation!")
        )

        override fun getGroupCount(): Int {
            return groupList.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            return childList[groupPosition].size
        }

        override fun getGroup(groupPosition: Int): Any {
            return groupList[groupPosition]
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return childList[groupPosition][childPosition]
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }

        override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
            groupTextView.text = getGroup(groupPosition).toString()
            return view
        }

        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
            val childTextView = view.findViewById<TextView>(R.id.childTextView)
            childTextView.text = getChild(groupPosition, childPosition).toString()
            return view
        }
    }

}