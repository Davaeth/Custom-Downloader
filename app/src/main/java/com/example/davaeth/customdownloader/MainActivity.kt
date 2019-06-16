package com.example.davaeth.customdownloader

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import android.os.StatFs
import android.util.Log
import com.example.davaeth.customdownloader.AsyncTask.FileDownloaderTask
import com.example.davaeth.customdownloader.AsyncTask.FileInformationTask


class MainActivity : AppCompatActivity() {

    private var permissions: MutableList<String> = mutableListOf<String>() // List of required permissions.
    private var isAbleToDownload: Boolean = false
    private var wasDownloadExecuted: Boolean = false
    private var wasInformationExecuted: Boolean = false

    private val fileDownloaderTask = FileDownloaderTask()
    private val fileInformationTask = FileInformationTask()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adding permissions to the manifest for higher API.
        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        this.requestPermissions(permissions.toTypedArray(), 47) // Ask user for required permissions.

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            isAbleToDownload = true
        }

        GlobalVariables.progressBar = progressBar_downloadProgress
        GlobalVariables.fileSize = text_fileSizeText
        GlobalVariables.fileType = text_fileTypeText
        GlobalVariables.downloadedBytes = text_bytesDownloadedCount
        GlobalVariables.context = this

        val stat = StatFs(Environment.getExternalStorageDirectory().absolutePath)
        val bytesAvailable = stat.blockSizeLong * stat.blockCountLong
        val megAvailable = bytesAvailable / 1048576
        Log.i("", "Available MB : $megAvailable")
    }

    //region Buttons listeners
    fun getInformation(v: View) {
        if (!wasInformationExecuted) {
            if (tryToDownload()) {
                fileInformationTask.execute(URL(text_siteAddress.text.toString()))
                wasInformationExecuted = true
            }
        }
    }

    fun downloadFile(v: View) {
        if (!wasDownloadExecuted) {
            if (tryToDownload()) {
                fileDownloaderTask.execute(URL(text_siteAddress.text.toString()))
                wasDownloadExecuted = true
            }
        }
    }

    fun cancelDownloading(v: View) {
        fileDownloaderTask.cancel(true)
    }
    //endregion

    private fun tryToDownload(): Boolean {
        return if (text_siteAddress.text.isNotBlank() && isAbleToDownload) {
            if (text_siteAddress.text.startsWith("http://") || text_siteAddress.text.startsWith("https://")) {
                text_siteAddress.setBackgroundColor(0)
                GlobalVariables.UrlString = text_siteAddress.text.toString()

                true
            } else {
                Toast.makeText(this, "Address should start with https://", Toast.LENGTH_LONG).show()
                text_siteAddress.setBackgroundColor(Color.RED)

                false
            }
        } else {
            Toast.makeText(this, "Website field cannot be blank!", Toast.LENGTH_LONG).show()
            text_siteAddress.setBackgroundColor(Color.RED)

            false
        }
    }
}
