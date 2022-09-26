package com.udacity

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager
    private lateinit var selectedButton: String

    private lateinit var binding: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat
            .getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(CHANNEL_ID, CHANNEL_NAME)

        custom_button.setOnClickListener {
            if (binding.rgButtons.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please select the file to download", Toast.LENGTH_SHORT).show()
            } else {
                custom_button.setState(ButtonState.Loading)
                download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val cursor = downloadManager?.query(DownloadManager.Query().setFilterById(downloadID))

                if (cursor != null && cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        notificationManager.sendNotification(
                            "$selectedButton download successful",
                            applicationContext,
                            CHANNEL_ID,
                            selectedButton,
                            true
                        )
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        notificationManager.sendNotification(
                            "$selectedButton download unsuccessful",
                            applicationContext,
                            CHANNEL_ID,
                            selectedButton,
                            false
                        )
                    }
                }
            }
        }
    }

    private fun download() {
        var url = selectedUrl()

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    // Method to get the selected url
    private fun selectedUrl() :String {
        return when (binding.rgButtons.checkedRadioButtonId) {
            binding.rbGlide.id -> {
                selectedButton = applicationContext.getString(R.string.glide)
                GLIDE_URL
            }
            binding.rbLoad.id -> {
                selectedButton = applicationContext.getString(R.string.loadapp)
                LOADAPP_URL
            }
            binding.rbRetrofit.id -> {
                selectedButton = applicationContext.getString(R.string.retrofit)
                RETROFIT_URL
            }
            else -> {
                selectedButton = applicationContext.getString(R.string.glide)
                GLIDE_URL
            }
        }
    }

    // Create notification channel
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification Channel"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL = "https://github.com/bumptech/glide/master.zip"

        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"

        private const val CHANNEL_ID = "channelId"

        private const val CHANNEL_NAME = "channelName"
    }

}
