package com.danefinlay.ttsutil

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import me.sid.smartcropper.R
import me.sid.smartcropper.views.activities.TranslationActivity

import org.jetbrains.anko.notificationManager

const val SPEAKING_NOTIFICATION_ID = 1
const val SYNTHESIS_NOTIFICATION_ID = 2

private fun getNotificationBuilder(ctx: Context, notificationId: Int):
        NotificationCompat.Builder {
    // Create an Intent and PendingIntent for when the user clicks on the
    // notification. This should just open/re-open MainActivity.
    val notificationIdUri = Uri.parse("notificationId:$notificationId")
    val onClickIntent = Intent(ctx,
            TranslationActivity::class.java).apply {
        data = notificationIdUri
        addFlags(START_ACTIVITY_FLAGS)
    }

    val contentPendingIntent = PendingIntent.getActivity(
            ctx, 0, onClickIntent, PendingIntent.FLAG_IMMUTABLE)

    // Just stop speaking for the delete intent (notification dismissal).
    val onDeleteIntent = Intent(ctx,
            SpeakerIntentService::class.java).apply {
        action = ACTION_STOP_SPEAKING
        data = notificationIdUri
    }
    val onDeletePendingIntent = PendingIntent.getService(ctx,
            0, onDeleteIntent, PendingIntent.FLAG_IMMUTABLE)

    // Set up the notification
    // Use the correct notification builder method
    val notificationBuilder = when {
        Build.VERSION.SDK_INT >= 26 -> {
            val id = ctx.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_LOW
            ctx.notificationManager.createNotificationChannel(
                    NotificationChannel(id, id, importance)
            )
            NotificationCompat.Builder(ctx, id)
        }
        else -> {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(ctx)
        }
    }
    return notificationBuilder.apply {
        // Set the icon for the notification
        setSmallIcon(android.R.drawable.ic_btn_speak_now)

        // Set pending intents.
        setContentIntent(contentPendingIntent)
        setDeleteIntent(onDeletePendingIntent)

        // Make it so the notification stays around after it's clicked on.
        setAutoCancel(false)

        // Add a notification action for stop speaking.
        // Re-use the delete intent.
        addAction(android.R.drawable.ic_delete,
                ctx.getString(R.string.stop_button),
                onDeletePendingIntent)
    }
}


fun speakerNotificationBuilder(ctx: Context, notificationId: Int):
        NotificationCompat.Builder {
    val builder = getNotificationBuilder(ctx, notificationId)
    return builder.apply {
        // Set the title and text depending on the notification ID.
        when (notificationId) {
            SPEAKING_NOTIFICATION_ID -> {
                setContentTitle(ctx.getString(R.string.speaking_notification_title))
                setContentText(ctx.getString(R.string.speaking_notification_text))
            }
            SYNTHESIS_NOTIFICATION_ID -> {
                setContentTitle(ctx.getString(R.string.synthesis_notification_title))
                setContentText(ctx.getString(R.string.synthesis_notification_text))
            }
            else -> {

            }
        }
    }
}
