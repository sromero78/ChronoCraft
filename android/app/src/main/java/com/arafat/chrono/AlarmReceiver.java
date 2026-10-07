package com.arafat.chrono;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID="chronocraft_ringing_alarm";

    @Override public void onReceive(Context context, Intent intent) {
        String id=intent.getStringExtra("alarmId");
        JSONObject alarm=AlarmStore.find(context,id);
        if(alarm==null || !alarm.optBoolean("enabled",true)) return;

        JSONArray weekdays=alarm.optJSONArray("weekdays");
        boolean oneShot=weekdays==null || weekdays.length()==0;
        if(oneShot) AlarmStore.setEnabled(context,id,false);
        else AlarmScheduler.schedule(context,alarm);

        String name=alarm.optString("name","Alarma");
        Intent ring=new Intent(context,AlarmActivity.class);
        ring.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ring.putExtra("alarmId",id);
        ring.putExtra("name",name);
        ring.putExtra("snoozeMinutes",alarm.optInt("snoozeMinutes",5));
        ring.putExtra("vibrate",alarm.optBoolean("vibrate",true));

        PendingIntent fullScreen=PendingIntent.getActivity(
            context,AlarmScheduler.requestCode(id+"-ring"),ring,
            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(nm==null) return;

        Uri sound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,"Alarmas",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Alarmas programadas de ChronoCraft");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.enableVibration(alarm.optBoolean("vibrate",true));
            AudioAttributes attrs=new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            channel.setSound(sound,attrs);
            nm.createNotificationChannel(channel);
        }

        Notification.Builder b=Build.VERSION.SDK_INT>=Build.VERSION_CODES.O
            ? new Notification.Builder(context,CHANNEL_ID)
            : new Notification.Builder(context);
        b.setSmallIcon(com.arafat.chrono.R.drawable.ic_stat_icon_config_sample)
            .setContentTitle(name)
            .setContentText("Alarma")
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreen,true)
            .setContentIntent(fullScreen);
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O) {
            b.setPriority(Notification.PRIORITY_MAX).setSound(sound);
        }
        nm.notify(AlarmScheduler.requestCode(id),b.build());
    }
}
