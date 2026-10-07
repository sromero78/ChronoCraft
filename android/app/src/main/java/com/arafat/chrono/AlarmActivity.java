package com.arafat.chrono;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;

public class AlarmActivity extends Activity {
    private Ringtone ringtone;
    private Vibrator vibrator;
    private String alarmId;
    private int snoozeMinutes = 5;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        AlarmDiagnostics.mark(this,"activity",getIntent().getStringExtra("alarmId"));
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        alarmId=getIntent().getStringExtra("alarmId");
        String name=getIntent().getStringExtra("name");
        snoozeMinutes=getIntent().getIntExtra("snoozeMinutes",5);
        boolean vibrate=getIntent().getBooleanExtra("vibrate",true);

        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(48,48,48,48);

        TextView title=new TextView(this);
        title.setText(name==null || name.isEmpty() ? "Alarma" : name);
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);
        root.addView(title,new LinearLayout.LayoutParams(-1,-2));

        TextView time=new TextView(this);
        java.text.SimpleDateFormat fmt=new java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault());
        time.setText(fmt.format(new java.util.Date()));
        time.setTextSize(64);
        time.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams timeParams=new LinearLayout.LayoutParams(-1,-2);
        timeParams.setMargins(0,32,0,56);
        root.addView(time,timeParams);

        Button snooze=new Button(this);
        snooze.setText("POSPONER "+snoozeMinutes+" MIN");
        snooze.setOnClickListener(v -> snooze());
        root.addView(snooze,new LinearLayout.LayoutParams(-1,-2));

        Button stop=new Button(this);
        stop.setText("DETENER");
        stop.setOnClickListener(v -> stopAlarm());
        LinearLayout.LayoutParams stopParams=new LinearLayout.LayoutParams(-1,-2);
        stopParams.setMargins(0,24,0,0);
        root.addView(stop,stopParams);
        setContentView(root);

        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(uri==null) uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ringtone=RingtoneManager.getRingtone(this,uri);
        if(ringtone!=null) {
            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
            if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.P) ringtone.setLooping(true);
            ringtone.play();
        }
        if(vibrate) {
            vibrator=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            if(vibrator!=null) vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,700,500},0));
        }
    }

    private void silence() {
        if(ringtone!=null && ringtone.isPlaying()) ringtone.stop();
        if(vibrator!=null) vibrator.cancel();
    }

    private void snooze() {
        silence();
        Intent fire=new Intent(this,AlarmReceiver.class).setAction("com.arafat.chrono.FIRE_ALARM");
        fire.putExtra("alarmId",alarmId);
        JSONObject alarm=AlarmStore.find(this,alarmId);
        if(alarm!=null) {
            fire.putExtra("name",alarm.optString("name","Alarma"));
            fire.putExtra("snoozeMinutes",snoozeMinutes);
            fire.putExtra("vibrate",alarm.optBoolean("vibrate",true));
        }
        PendingIntent pi=PendingIntent.getBroadcast(this,AlarmScheduler.requestCode(alarmId+"-snooze"),fire,
            PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        if(am!=null) am.setAlarmClock(new AlarmManager.AlarmClockInfo(System.currentTimeMillis()+snoozeMinutes*60000L,null),pi);
        finishAndRemoveTask();
    }

    private void stopAlarm() { silence(); finishAndRemoveTask(); }

    @Override protected void onDestroy() { silence(); super.onDestroy(); }
}
