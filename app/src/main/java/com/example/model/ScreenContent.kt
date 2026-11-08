package com.example.model

data class ScreenContextItem(
    val title: String,
    val appSource: String,
    val rawContent: String,
    val summary: String,
    val keyInsights: List<String>,
    val suggestedActions: List<String>
)

typealias ScreenContentItem = ScreenContextItem


object ScreenSampleData {
    val samples = listOf(
        ScreenContextItem(
            title = "Flight Reservation - Indigo 6E-204",
            appSource = "Travel Booking",
            rawContent = """
                Flight: 6E 204 | Delhi (DEL) -> Bengaluru (BLR)
                Date: Friday, Nov 14 | Departure: 07:15 AM | Gate 4B
                PNR: VK8829 | Terminal 3 | Baggage: 15kg included
                Boarding Time: 06:30 AM | Seat: 12F (Window)
                Status: Confirmed. Web check-in completed.
            """.trimIndent(),
            summary = "You have an upcoming flight to Bengaluru on Friday departing at 7:15 AM from Terminal 3, Gate 4B. Web check-in is complete.",
            keyInsights = listOf(
                "Departure: 07:15 AM from Terminal 3",
                "Gate: 4B (Boarding closes 06:50 AM)",
                "Seat: 12F Window",
                "PNR: VK8829"
            ),
            suggestedActions = listOf(
                "Set departure reminder 2 hours before",
                "WhatsApp flight details to Alex",
                "Open Google Maps for airport traffic"
            )
        ),
        ScreenContextItem(
            title = "codeninjavik.in - System Architecture Note",
            appSource = "Chrome Browser",
            rawContent = """
                Project: MYRA Neural Voice Engine v2.4
                API Gateway: api.codeninjavik.in/v2/assistant
                Auth: OAuth Bearer Token + Biometric Device Signature
                Active Providers: Groq LPU, Gemini 2.5 Flash, Claude 3.5 Sonnet
                Preferences synced: 14 rules, 5 quick contacts
            """.trimIndent(),
            summary = "Viewing codeninjavik.in architecture docs showing MYRA neural voice engine with 3 active connected AI providers and cloud sync.",
            keyInsights = listOf(
                "codeninjavik.in API Gateway online",
                "Groq LPU + Gemini + Claude enabled",
                "Cloud sync active with 14 preferences"
            ),
            suggestedActions = listOf(
                "Sync latest preferences to device",
                "Test Groq LPU latency",
                "Share link to team"
            )
        ),
        ScreenContextItem(
            title = "WhatsApp Chat with Alex",
            appSource = "WhatsApp",
            rawContent = """
                Alex: Hey Vikram, are we meeting at the tech cafe at 5 PM?
                Alex: Let me know if you want me to bring the prototype board.
                You: Yeah! I'm finishing the MYRA voice orb styling now.
                Alex: Awesome, see you soon!
            """.trimIndent(),
            summary = "Alex asked about meeting at the tech cafe at 5 PM and offering to bring the prototype board.",
            keyInsights = listOf(
                "Meeting: Tech Cafe at 5:00 PM today",
                "Pending reply about the prototype board"
            ),
            suggestedActions = listOf(
                "Reply: 'Yes bring the prototype!'",
                "Call Alex",
                "Open Maps to Tech Cafe"
            )
        )
    )
}
