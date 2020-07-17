package com.jetbrains.handson.mpp.mobile

import com.jetbrains.handson.mpp.mobile.api.DateTime

data class Journey(val originStation: String, val destinationStation: String, val depTime: DateTime, val arrTime: DateTime, val duration: String, val date: DateTime, val button: String = "go to buy")