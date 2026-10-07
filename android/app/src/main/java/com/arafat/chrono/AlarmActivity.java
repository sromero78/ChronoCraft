package com.arafat.chrono;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

public class AlarmActivity extends Activity {
 private String alarmId; private int snoozeMinutes=5;
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
 private GradientDrawable bg(int color,float radius){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;}
 private TextView text(String value,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(value);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
 @Override protected void onCreate(Bundle state){
  super.onCreate(state);AlarmDiagnostics.mark(this,"activity",getIntent().getStringExtra("alarmId"));
  setShowWhenLocked(true);setTurnScreenOn(true);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  alarmId=getIntent().getStringExtra("alarmId");String name=getIntent().getStringExtra("name");snoozeMinutes=getIntent().getIntExtra("snoozeMinutes",5);
  int navy=Color.rgb(15,23,42),muted=Color.rgb(148,163,184),indigo=Color.rgb(79,70,229),panel=Color.rgb(30,41,59);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(28),dp(44),dp(28),dp(44));root.setBackgroundColor(navy);
  TextView brand=text("ChronoCraft",18,Color.rgb(129,140,248),true);root.addView(brand,new LinearLayout.LayoutParams(-1,-2));
  TextView bell=text("◷",42,Color.rgb(129,140,248),false);LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.setMargins(0,dp(22),0,dp(8));root.addView(bell,bp);
  TextView title=text(name==null||name.isEmpty()?"Alarma":name,30,Color.WHITE,true);root.addView(title,new LinearLayout.LayoutParams(-1,-2));
  TextView time=text(new java.text.SimpleDateFormat("HH:mm",java.util.Locale.getDefault()).format(new java.util.Date()),68,Color.WHITE,true);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(-1,-2);tp.setMargins(0,dp(12),0,dp(6));root.addView(time,tp);
  TextView subtitle=text("ALARMA",12,muted,true);LinearLayout.LayoutParams subp=new LinearLayout.LayoutParams(-1,-2);subp.setMargins(0,0,0,dp(48));root.addView(subtitle,subp);
  Button snooze=new Button(this);snooze.setText("POSPONER  "+snoozeMinutes+" MIN");snooze.setTextSize(16);snooze.setTextColor(Color.WHITE);snooze.setTypeface(Typeface.DEFAULT,Typeface.BOLD);snooze.setAllCaps(false);snooze.setBackground(bg(indigo,16));snooze.setOnClickListener(v->snooze());LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(58));root.addView(snooze,sp);
  Button stop=new Button(this);stop.setText("DETENER");stop.setTextSize(16);stop.setTextColor(Color.WHITE);stop.setTypeface(Typeface.DEFAULT,Typeface.BOLD);stop.setAllCaps(false);stop.setBackground(bg(panel,16));stop.setOnClickListener(v->stopAlarm());LinearLayout.LayoutParams stp=new LinearLayout.LayoutParams(-1,dp(58));stp.setMargins(0,dp(14),0,0);root.addView(stop,stp);
  setContentView(root);
 }
 private void snooze(){Intent i=new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_SNOOZE).putExtra("alarmId",alarmId).putExtra("snoozeMinutes",snoozeMinutes);startService(i);finishAndRemoveTask();}
 private void stopAlarm(){startService(new Intent(this,AlarmSoundService.class).setAction(AlarmSoundService.ACTION_STOP).putExtra("alarmId",alarmId));finishAndRemoveTask();}
}