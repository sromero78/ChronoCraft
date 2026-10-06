package com.arafat.chrono;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.json.JSONObject;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        String id=intent.getStringExtra("alarmId");
        JSONObject alarm=AlarmStore.find(context,id);
        if(alarm!=null && alarm.optBoolean("enabled",true)) {
            // The next occurrence is scheduled immediately so weekly alarms survive this firing.
            AlarmScheduler.schedule(context,alarm);
            // Alarm sound/full-screen experience is added in the next native layer.
        }
    }
}
