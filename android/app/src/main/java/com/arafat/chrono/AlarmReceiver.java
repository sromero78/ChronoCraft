package com.arafat.chrono;
import android.content.*;
import android.os.Build;
import org.json.*;

public class AlarmReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context,Intent intent){
  String id=intent.getStringExtra("alarmId"); AlarmDiagnostics.mark(context,"receiver",id==null?"":id);
  JSONObject alarm=AlarmStore.find(context,id); boolean snooze=intent.getBooleanExtra("isSnooze",false);
  if(alarm==null || (!snooze && !alarm.optBoolean("enabled",true)))return;
  JSONArray days=alarm.optJSONArray("weekdays"); boolean oneShot=days==null||days.length()==0;
  if(!snooze){if(oneShot)AlarmStore.setEnabled(context,id,false);else AlarmScheduler.schedule(context,alarm);}
  Intent service=new Intent(context,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_START)
   .putExtra("alarmId",id).putExtra("name",alarm.optString("name","Alarma"))
   .putExtra("snoozeMinutes",alarm.optInt("snoozeMinutes",5)).putExtra("vibrate",alarm.optBoolean("vibrate",true));
  if(Build.VERSION.SDK_INT>=26)context.startForegroundService(service);else context.startService(service);
  AlarmDiagnostics.mark(context,"notification",id);
 }
}