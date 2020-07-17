package com.jetbrains.handson.mpp.mobile

data class Journey(val inbound: String, val outbound: String, val depTime: String, val arrTime: String, val duration: String, val date: String, val button: String = "go to buy")


data class Ticket(val inbound: String, val outbound: String, val month: Int, val day: Int, val hours: Int, val minutes: Int, val returnBool: Boolean)