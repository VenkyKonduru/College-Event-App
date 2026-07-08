package com.example.collegeeventapp



data class Event(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val venue: String = "",
    var isRegistered: Boolean = false
)