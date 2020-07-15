package com.jetbrains.handson.mpp.mobile

import com.jetbrains.handson.mpp.mobile.api.FaresResponse
import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun setLabel(text: String)
        fun showAlert(text: String)
        fun showData(text: FaresResponse)
    }

    abstract class Presenter : CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onButtonPressed(origin: String, destination: String)
    }
}
