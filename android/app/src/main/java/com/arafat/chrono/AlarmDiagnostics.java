package com.arafat.chrono;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
public final class AlarmDiagnostics {
 private static final String PREFS="chronocraft_alarm_diagnostics";
 private AlarmDiagnostics(){}
 private static SharedPreferences p(Context c){return c.getSharedPreferences(PREFS,Context.MODE_PRIVATE);}
 public static void mark(Context c,String key,String value){p(c).edit().putString(key,value).putLong(key+"At",System.currentTimeMillis()).apply();}
 public static void scheduled(Context c,String id,long trigger){p(c).edit().putString("scheduledId",id).putLong("scheduledFor",trigger).putLong("scheduledAt",System.currentTimeMillis()).apply();}
 public static JSONObject snapshot(Context c){SharedPreferences s=p(c);JSONObject o=new JSONObject();try{o.put("scheduledId",s.getString("scheduledId",""));o.put("scheduledFor",s.getLong("scheduledFor",0));o.put("scheduledAt",s.getLong("scheduledAt",0));for(String k:new String[]{"receiver","notification","activity"}){o.put(k,s.getString(k,""));o.put(k+"At",s.getLong(k+"At",0));}}catch(Exception ignored){}return o;}
 public static void clear(Context c){p(c).edit().clear().apply();}
}