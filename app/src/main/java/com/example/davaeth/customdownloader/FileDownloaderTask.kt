package com.example.davaeth.customdownloader

import android.annotation.SuppressLint
import android.app.usage.ExternalStorageStats
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

class FileDownloaderTask : AsyncTask<URL, Int, Long>() {
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

                val iStream = BufferedInputStream(url.openStream(), 8192)

                println("ULR :: $params[0]")

                val folder: String =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "androiddeft/"

                val timestamp: String = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())

                val fileName: String = timestamp + "_" + params[0].toString().substring(
                    params[0].toString().lastIndexOf('/') + 1,
                    params[0].toString().length
                )

                val directory = File(folder)

                if (!directory.exists()) {
                    directory.mkdirs()
                }

                // Output stream to write file.
                val oStream: OutputStream = FileOutputStream(folder + fileName)

                val data = ByteArray(fileLength.toInt())

                while (iStream.read(data) != -1) {
                    totalSize += iStream.read(data)
                    publishProgress(((totalSize * 100) / fileLength).toInt())

                    println((totalSize * 100) / fileLength)

                    // Actually saving the file.
                    oStream.write(iStream.read(data))

                    // Checking if downloading was cancelled.
                    if (isCancelled) {
                        break
                    }
                }

                oStream.flush()

                // Closing file streams.
                oStream.close()
                iStream.close()

                GlobalVariables.notifyUser

                return fileLength

            } catch (e: Exception) {
                println(e.printStackTrace())
            }
        }

        return 0
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)

        if (values.isNotEmpty()) {
            GlobalVariables.downloadedBytes.text = ((totalSize * 100) / fileLength).toString()
            GlobalVariables.progressBar.progress = values[0]!!
        }
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)
    }

    override fun onCancelled() {
        super.onCancelled()

        GlobalVariables.progressBar.progress = 0
        GlobalVariables.downloadedBytes.text = ""
    }

    private fun notifyUser(function: () -> (Unit)) {

    }
}