package com.example.davaeth.customdownloader

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import android.os.StatFs
import android.util.Log


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
        GlobalVariables.notifyUser = notifyUser("Downloaded successfully!")

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

    fun notifyUser(content: String) {
        GlobalVariables.NotificationID += 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Your download result!"
            val descriptionText = "Result information of your recently downloaded file"

            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel("Download result", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, "Download result")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Your result!")
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(GlobalVariables.NotificationID, builder.build())
        }
    }

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
