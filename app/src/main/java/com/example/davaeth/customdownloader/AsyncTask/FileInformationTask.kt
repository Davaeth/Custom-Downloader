package com.example.davaeth.customdownloader.AsyncTask

import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import com.example.davaeth.customdownloader.GlobalVariables
import java.lang.Exception
import java.net.URL
import java.net.URLConnection

class FileInformationTask : AsyncTask<URL, Int, Long>() {
    private var totalSize: Int = 0
    private var fileLength: Long = 0;
    private lateinit var connection: URLConnection
    private lateinit var url: URL

    //region Properties

    var Count: Int
        get() = this.totalSize
        set(value) {
            this.totalSize = value
        }

    var TotalSize: Long
        get() = this.fileLength
        set(value) {
            this.fileLength = value
        }

    //endregion

    @RequiresApi(Build.VERSION_CODES.N)
    override fun doInBackground(vararg params: URL?): Long {
        totalSize = params.count()

        if (GlobalVariables.UrlString.trim() != "") {
            url = URL(GlobalVariables.UrlString)
            try {

                connection = url.openConnection()

                connection.connect()

                fileLength = connection.contentLengthLong

                getInformation()
            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }

        return 0
    }

    private fun getInformation() {
        GlobalVariables.fileSize.text = fileLength.toString()
        GlobalVariables.fileType.text = connection.contentType
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)
    }
}