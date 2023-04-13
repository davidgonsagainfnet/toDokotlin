package exemple.com.todo.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import exemple.com.todo.R

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p0 != null){
            val titleNotify = p1!!.getStringExtra("titlemsg")
            val textNotify = p1!!.getStringExtra("msg")
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(p0, "TODO").setSmallIcon(
                R.drawable.ic_launcher_background)
                .setContentTitle(titleNotify)
                .setContentText(textNotify)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val manager = p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification.build())
        }
    }
}