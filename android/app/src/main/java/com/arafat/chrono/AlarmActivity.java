package com.arafat.chrono;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.*;

public class AlarmActivity extends Activity {
 private String alarmId; private int snoozeMinutes=5;
 @Override protected void onCreate(Bundle state){
  super.onCreate(state); AlarmDiagnostics.mark(this,"activity",getIntent().getStringExtra("alarmId"));
  setShowWhenLocked(true);setTurnScreenOn(true);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  alarmId=getIntent().getStringExtra("alarmId");String name=getIntent().getStringExtra("name");snoozeMinutes=getIntent().getIntExtra("snoozeMinutes",5);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(48,48,48,48);
  TextView title=new TextView(this);title.setText(name==null||name.isEmpty()?"Alarma":name);title.setTextSize(34);title.setGravity(Gravity.CENTER);root.addView(title,new LinearLayout.LayoutParams(-1,-2));
  TextView time=new TextView(this);time.setText(new java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault()).format(new java.util.Date()));time.setTextSize(64);time.setGravity(Gravity.CENTER);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.setMargins(0,32,0,56);root.addView(time,tp);
  Button snooze=new Button(this);snooze.setText("POSPONER "+snoozeMinutes+" MIN");snooze.setOnClickListener(v->snooze());root.addView(snooze,new LinearLayout.LayoutParams(-1,-2));
  Button stop=new Button(this);stop.setText("DETENER");stop.setOnClickListener(v->stopAlarm());LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.setMargins(0,24,0,0);root.addView(stop,sp);setContentView(root);
 }
 private void snooze(){Intent i=new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_SNOOZE).putExtra("alarmId",alarmId).putExtra("snoozeMinutes",snoozeMinutes);startService(i);finishAndRemoveTask();}
 private void stopAlarm(){startService(new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_STOP).putExtra("alarmId",alarmId));finishAndRemoveTask();}
}