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
        return """
            Constitution of St. Stephens KAYO

            Preamble:
            We, the members of St. Stephens KAYO, in order to promote the welfare, unity, and common interests of our members, and to establish a framework for effective governance, do hereby adopt this constitution.

            Article I: Name and Nature of the Organization
            Section 1: Name
            The name of this organization shall be St. Stephens KAYO.

            Section 2: Nature
            St. Stephens KAYO is a non-profit, non-political, and non-discriminatory organization.

            Article II: Mission and Objectives
            Section 1: Mission
            The mission of St. Stephens KAYO is to [state the primary mission and purpose of the organization].

            Section 2: Objectives
            The objectives of St. Stephens KAYO are:
            a) [Objective 1]
            b) [Objective 2]
            c) [Objective 3]
            ... [add more objectives if needed]

            Article III: Membership
            Section 1: Membership Eligibility
            Membership in St. Stephens KAYO is open to all individuals who share a common interest in the objectives of the organization.

            Section 2: Categories of Membership
            There shall be the following categories of membership:
            a) Regular Members: [Description of regular membership criteria]
            b) Associate Members: [Description of associate membership criteria]
            c) Honorary Members: [Description of honorary membership criteria]

            Section 3: Rights and Privileges of Members
            All members of St. Stephens KAYO shall have the right to [list the rights and privileges of members].

            Section 4: Termination of Membership
            Membership in St. Stephens KAYO may be terminated for reasons such as [list the grounds for termination].

            Article IV: Organizational Structure
            Section 1: Executive Committee
            St. Stephens KAYO shall be governed by an Executive Committee composed of the following officers:
            a) President
            b) Vice President
            c) Secretary
            d) Treasurer
            e) ... [add other officer positions if needed]

            Section 2: Duties and Responsibilities
            The duties and responsibilities of each officer shall be as follows:
            a) President: [Description of the President's role]
            b) Vice President: [Description of the Vice President's role]
            c) Secretary: [Description of the Secretary's role]
            d) Treasurer: [Description of the Treasurer's role]
            e) ... [add descriptions for other officer roles if needed]

            Article V: Meetings
            Section 1: General Meetings
            St. Stephens KAYO shall hold regular general meetings, such as [describe the frequency and purpose of general meetings].

            Section 2: Special Meetings
            Special meetings may be called by [list who can call special meetings] for the purpose of [describe the purpose of special meetings].

            Article VI: Amendments
            Section 1: Proposal
            Amendments to this constitution may be proposed by [describe who can propose amendments].

            Section 2: Approval
            Proposed amendments shall be approved by [describe the approval process] and require a [describe the required majority] vote for adoption.

            Article VII: Dissolution
            Section 1: Dissolution
            In the event of the dissolution of St. Stephens KAYO, all assets shall be distributed [describe the distribution of assets].

            Article VIII: Ratification
            This constitution shall be ratified and take effect upon approval by a majority vote of the members present at the [describe the meeting for ratification].

            In witness whereof, we, the undersigned members of St. Stephens KAYO, hereby adopt and enact this constitution on this [date of ratification].

            ______________________
            President's Signature

            ______________________
            Secretary's Signature
        """.trimIndent()
    }
}
