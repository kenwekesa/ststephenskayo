package com.ack.ststephenskayo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelfareConstitution : AppCompatActivity() {

    lateinit var textConstitution:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welfare_constitution)

        textConstitution = findViewById(R.id.textConstitution)
        val constitution = buildConstitutionStStephensKAYO()

        textConstitution.text = constitution
    }

    private fun buildConstitutionStStephensKAYO(): String {
        val constitution = """
ST STEPHEN CHURCH YOUTH WELFARE- KAYO

Executive Committee:
1. Chairperson - Maxwell Odhiambo
2. Secretary - Esther Otieno
3. Organizing Secretary - Allan Laban
4. Legal Advisor - Kelvin King'ori
5. Treasurer - Branice Matini
6. Member - Stanley Mwaumba
7. Member - Ann Wangare

Objectives:
1. To promote solidarity and mutual cooperation among members.
2. Assist members in times of needs, i.e., weddings, burials, and serious illness upon the committee assessment.

Rules and Regulations:
1. Registered member of St Stephen's Church Bamburi and KAYO.
2. KAYO member should be up to date with his/her monthly subscription.
3. Registration fee of Ksh 50 should be submitted to the treasurer annually.
4. Every member should make an effort to punctually attend services.
5. Every member should participate actively in KAYO activities.
6. Ksh 100 should be paid before the second Sunday of a new month.
7. Account statements will be issued every three months.
8. A new member joining in the middle of a financial year will have to pay the subscriptions of the previous months of that financial year.

Benefits:
1. A member gets a contribution of Ksh 5000 for his/her wedding as the principal member. The member should notify the committee one month before the wedding. The contribution will be disbursed upon assessment and approval by the executive committee.
2. Ksh 5000 for the principal member upon a funeral, two weeks after notifying the welfare committee.
3. Assistance for any serious illness of the principal member or nuclear family upon the assessment of the committee.

Cases that could result in exemption from the benefits:
1. Non-contribution for the past three months.
2. Unnotified exit from the association.
3. Arrears of six months - No longer a member hence need to register afresh.
4. One should pay previous months' dues to be eligible once again.
""".trimIndent()

        return constitution
    }
}
