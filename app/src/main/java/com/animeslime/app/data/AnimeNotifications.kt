package com.animeslime.app.data

import android.app.*
import android.content.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.animeslime.app.MainActivity
object AnimeNotifications {
 const val CHANNEL="new_episodes"
 fun create(context:Context){if(android.os.Build.VERSION.SDK_INT>=26)context.getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL,"حلقات جديدة",NotificationManager.IMPORTANCE_DEFAULT))}
 fun show(context:Context,title:String,text:String){create(context);val open=PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);val n=NotificationCompat.Builder(context,CHANNEL).setSmallIcon(com.animeslime.app.R.drawable.anime_slime_logo).setContentTitle(title).setContentText(text).setContentIntent(open).setAutoCancel(true).addAction(0,"شاهد",open).build();NotificationManagerCompat.from(context).notify(title.hashCode(),n)}
}
