package com.example.davaeth.customdownloader.AsyncTask

import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.widget.Toast
import com.example.davaeth.customdownloader.GlobalVariables
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
    private lateinit var fileName: String
    private lateinit var folder: String

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

                folder =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + "androiddeft/"

                val timestamp: String = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(Date())

                fileName = timestamp + "_" + params[0].toString().substring(
                    params[0].toString().lastIndexOf('/') + 1,
                    params[0].toString().length
                )

                val directory = File(folder)

                if (!directory.exists()) {
                    directory.mkdirs()
                }

                // Output stream to write file.
                val oStream: OutputStream = FileOutputStream(folder + fileName)

                val data = ByteArray(2048)

                // While loop that checks if there still is any bytes to write.
                do {
                    // Getting actual bytes to save.
                    val bytes = iStream.read(data)

                    // Sending downloaded bytes to the progress bar.
                    totalSize += bytes
                    publishProgress(((totalSize * 100) / fileLength.toInt()))

                    // Actually saving the file.
                    oStream.write(data, 0, bytes)

                    // Checking if downloading was cancelled.
                    if (isCancelled) {
                        break
                    }
                } while (bytes != -1)

                oStream.flush()

                // Closing file streams.
                oStream.close()
                iStream.close()

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
            // Managing displayed values.
            GlobalVariables.downloadedBytes.text = ((totalSize * 100) / fileLength).toString()
            GlobalVariables.progressBar.progress = values[0]!!
        }
    }

    override fun onPostExecute(result: Long?) {
        super.onPostExecute(result)

        Toast.makeText(GlobalVariables.context, "Downloaded successfully!", Toast.LENGTH_LONG).show()
    }

    override fun onCancelled() {
        super.onCancelled()

        // Deleting file when downloading is cancelled.
        val fileToDelete = File(folder, fileName)
        fileToDelete.delete()

        GlobalVariables.progressBar.progress = 0
        GlobalVariables.downloadedBytes.text = ""
    }
}