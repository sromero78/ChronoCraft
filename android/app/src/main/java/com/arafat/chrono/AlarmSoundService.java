package com.arafat.chrono;
import android.app.*;
import android.content.*;
import android.media.*;
import android.net.Uri;
import android.os.*;
import org.json.JSONObject;

public class AlarmSoundService extends Service {
 public static final String ACTION_START="com.arafat.chrono.ALARM_START", ACTION_STOP="com.arafat.chrono.ALARM_STOP", ACTION_SNOOZE="com.arafat.chrono.ALARM_SNOOZE";
 private static final int NOTIFICATION_ID=7301; private static final String CHANNEL="chronocraft_alarm_active";
 private Ringtone ringtone; private Vibrator vibrator; private String alarmId; private int snoozeMinutes=5;
 @Override public int onStartCommand(Intent i,int flags,int startId){
  if(i==null)return START_NOT_STICKY; String a=i.getAction();
  if(ACTION_STOP.equals(a)){stopAlarm();return START_NOT_STICKY;}
  alarmId=i.getStringExtra("alarmId"); snoozeMinutes=i.getIntExtra("snoozeMinutes",5);
  if(ACTION_SNOOZE.equals(a)){scheduleSnooze();stopAlarm();return START_NOT_STICKY;}
  String name=i.getStringExtra("name"); boolean vibrate=i.getBooleanExtra("vibrate",true);
  startForeground(NOTIFICATION_ID,notification(name==null?"Alarma":name));
  if(ringtone==null){Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);if(uri==null)uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);ringtone=RingtoneManager.getRingtone(this,uri);if(ringtone!=null){ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());if(Build.VERSION.SDK_INT>=28)ringtone.setLooping(true);ringtone.play();}}
  if(vibrate&&vibrator==null){vibrator=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(vibrator!=null)vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,700,500},0));}
  return START_NOT_STICKY;
 }
 private Notification notification(String name){
  NotificationManager nm=getSystemService(NotificationManager.class);
  if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL,"Alarma activa",NotificationManager.IMPORTANCE_HIGH);ch.setSound(null,null);ch.enableVibration(false);ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);nm.createNotificationChannel(ch);}
  Intent open=new Intent(this,AlarmActivity.class).putExtra("alarmId",alarmId).putExtra("name",name).putExtra("snoozeMinutes",snoozeMinutes);
  PendingIntent fs=PendingIntent.getActivity(this,AlarmScheduler.requestCode(alarmId+"-ring"),open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Intent snooze=new Intent(this,AlarmSoundService.class).setAction(ACTION_SNOOZE).putExtra("alarmId",alarmId).putExtra("snoozeMinutes",snoozeMinutes);
  Intent stop=new Intent(this,AlarmSoundService.class).setAction(ACTION_STOP).putExtra("alarmId",alarmId);
  PendingIntent ps=PendingIntent.getService(this,AlarmScheduler.requestCode(alarmId+"-snooze-action"),snooze,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  PendingIntent pt=PendingIntent.getService(this,AlarmScheduler.requestCode(alarmId+"-stop-action"),stop,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);
  return b.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(name).setContentText("Alarma activa")
   .setCategory(Notification.CATEGORY_ALARM).setVisibility(Notification.VISIBILITY_PUBLIC)
   .setOngoing(true).setOnlyAlertOnce(true).setFullScreenIntent(fs,true).setContentIntent(fs)
   .setStyle(new Notification.BigTextStyle().bigText("Alarma activa · usa los botones para posponer o detener"))
   .addAction(new Notification.Action.Builder(null,"POSPONER "+snoozeMinutes+" MIN",ps).build())
   .addAction(new Notification.Action.Builder(null,"DETENER",pt).build()).build();
 }
 private void scheduleSnooze(){
  JSONObject alarm=AlarmStore.find(this,alarmId); if(alarm==null)return;
  AlarmStore.setEnabled(this,alarmId,true);
  Intent fire=new Intent(this,AlarmReceiver.class).setAction("com.arafat.chrono.FIRE_ALARM").putExtra("alarmId",alarmId).putExtra("isSnooze",true);
  PendingIntent pi=PendingIntent.getBroadcast(this,AlarmScheduler.requestCode(alarmId+"-snooze"),fire,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);if(am!=null)am.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+snoozeMinutes*60000L,null),pi);
 }
 private void stopAlarm(){if(ringtone!=null){ringtone.stop();ringtone=null;}if(vibrator!=null){vibrator.cancel();vibrator=null;}stopForeground(STOP_FOREGROUND_REMOVE);stopSelf();}
 @Override public void onDestroy(){if(ringtone!=null)ringtone.stop();if(vibrator!=null)vibrator.cancel();super.onDestroy();}
 @Override public android.os.IBinder onBind(Intent i){return null;}
}