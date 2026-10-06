package com.arafat.chrono;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.json.JSONObject;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String id=intent.getStringExtra("alarmId");
        JSONObject alarm=AlarmStore.find(context,id);
        if(alarm==null || !alarm.optBoolean("enabled",true)) return;

        // Schedule the next weekly occurrence before opening the ringing UI.
        AlarmScheduler.schedule(context,alarm);

        Intent ring=new Intent(context,AlarmActivity.class);
        ring.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ring.putExtra("alarmId",id);
        ring.putExtra("name",alarm.optString("name","Alarma"));
        ring.putExtra("snoozeMinutes",alarm.optInt("snoozeMinutes",5));
        ring.putExtra("vibrate",alarm.optBoolean("vibrate",true));
        context.startActivity(ring);
    }
}
