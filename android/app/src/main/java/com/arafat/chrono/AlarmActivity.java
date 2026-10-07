package com.arafat.chrono;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public class AlarmActivity extends Activity {
 private String alarmId; private int snoozeMinutes=5;
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
 private GradientDrawable bg(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
 private TextView text(String value,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(value);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
 @Override protected void onCreate(Bundle state){
  super.onCreate(state);AlarmDiagnostics.mark(this,"activity",getIntent().getStringExtra("alarmId"));
  requestWindowFeature(Window.FEATURE_NO_TITLE);setShowWhenLocked(true);setTurnScreenOn(true);
  Window w=getWindow();w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_DIM_BEHIND);
  WindowManager.LayoutParams wp=w.getAttributes();wp.width=(int)(getResources().getDisplayMetrics().widthPixels*.88f);wp.height=WindowManager.LayoutParams.WRAP_CONTENT;wp.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL;wp.y=(int)(getResources().getDisplayMetrics().heightPixels*.20f);wp.dimAmount=.38f;w.setAttributes(wp);w.setBackgroundDrawableResource(android.R.color.transparent);
  alarmId=getIntent().getStringExtra("alarmId");String name=getIntent().getStringExtra("name");snoozeMinutes=getIntent().getIntExtra("snoozeMinutes",5);
  int navy=Color.rgb(15,23,42),muted=Color.rgb(148,163,184),indigo=Color.rgb(79,70,229),panel=Color.rgb(30,41,59);
  LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setGravity(Gravity.CENTER);card.setPadding(dp(18),dp(14),dp(18),dp(16));card.setBackground(bg(navy,28));
  TextView title=text(name==null||name.isEmpty()?"Alarma":name,18,Color.WHITE,true);card.addView(title,new LinearLayout.LayoutParams(-1,-2));
  TextView time=text(new java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault()).format(new java.util.Date()),40,Color.WHITE,true);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.setMargins(0,dp(2),0,dp(10));card.addView(time,tp);
  LinearLayout actions=new LinearLayout(this);actions.setOrientation(LinearLayout.HORIZONTAL);actions.setGravity(Gravity.CENTER);
  Button snooze=new Button(this);snooze.setText("Posponer "+snoozeMinutes+" min");snooze.setTextSize(14);snooze.setTextColor(Color.WHITE);snooze.setTypeface(Typeface.DEFAULT,Typeface.BOLD);snooze.setAllCaps(false);snooze.setBackground(bg(indigo,14));snooze.setOnClickListener(v->snooze());
  Button stop=new Button(this);stop.setText("Detener");stop.setTextSize(14);stop.setTextColor(Color.WHITE);stop.setTypeface(Typeface.DEFAULT,Typeface.BOLD);stop.setAllCaps(false);stop.setBackground(bg(panel,14));stop.setOnClickListener(v->stopAlarm());
  LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,dp(46),1);ap.setMargins(dp(4),0,dp(4),0);actions.addView(snooze,ap);actions.addView(stop,new LinearLayout.LayoutParams(0,dp(46),1));card.addView(actions,new LinearLayout.LayoutParams(-1,-2));
  FrameLayout shell=new FrameLayout(this);shell.setPadding(0,0,0,0);shell.addView(card,new FrameLayout.LayoutParams(-1,-2));setContentView(shell);
 }
 private void finishAlarmUi(){finishAndRemoveTask();}
 private void snooze(){Intent i=new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_SNOOZE).putExtra("alarmId",alarmId).putExtra("snoozeMinutes",snoozeMinutes);startService(i);finishAlarmUi();}
 private void stopAlarm(){startService(new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_STOP).putExtra("alarmId",alarmId));finishAlarmUi();}
 @Override public void onBackPressed(){/* Alarm requires an explicit action. */}
}