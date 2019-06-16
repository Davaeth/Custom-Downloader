package com.example.davaeth.customdownloader

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Class of global variables,
 * that are shared in ever class in application.
 * CAUTION! : It will be vanished after app reset!
 */
class GlobalVariables : Application() {

    /***
     * Companion object that holds static fields
     * of the global variables.
     */
    companion object {
        private var notificationID: Int = 1

        private var urlString: String = ""

        @SuppressLint("StaticFieldLeak")
        lateinit var progressBar: ProgressBar

        @SuppressLint("StaticFieldLeak")
        lateinit var fileSize: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var fileType: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var downloadedBytes: TextView

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //region Properties
        var NotificationID: Int
            get() = this.notificationID
            set(value) {
                if (value == 0) {
                    this.notificationID = 1
                } else {
                    this.notificationID = value
                }
            }

        var UrlString: String
            get() = this.urlString
            set(value) {
                this.urlString = value
            }
        //endregion
    }
}