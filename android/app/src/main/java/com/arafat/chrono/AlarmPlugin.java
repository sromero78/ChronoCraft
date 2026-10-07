package com.arafat.chrono;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.getcapacitor.JSArray;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

@CapacitorPlugin(name = "ChronoAlarm")
public class AlarmPlugin extends Plugin {
    @PluginMethod
    public void sync(PluginCall call) {
        JSArray input = call.getArray("alarms");
        JSONArray alarms = input == null ? new JSONArray() : input;
        try {
            // Cancel everything from the previous native snapshot first. This also
            // removes alarms deleted in the web UI.
            JSONArray previous=AlarmStore.load(getContext());
            for(int i=0;i<previous.length();i++) {
                JSONObject old=previous.optJSONObject(i);
                if(old!=null) AlarmScheduler.cancel(getContext(),old.optString("id"));
            }
            AlarmStore.save(getContext(), alarms);
            for(int i=0;i<alarms.length();i++) {
                JSONObject alarm=alarms.optJSONObject(i);
                if(alarm!=null) AlarmScheduler.schedule(getContext(),alarm);
            }
            call.resolve();
        } catch (SecurityException e) {
            call.reject("EXACT_ALARM_PERMISSION_REQUIRED", e);
        } catch (Exception e) {
            call.reject("ALARM_SCHEDULE_FAILED", e);
        }
    }

    @PluginMethod
    public void getExactAlarmStatus(PluginCall call) {
        AlarmManager manager=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        boolean granted=android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S || (manager!=null && manager.canScheduleExactAlarms());
        com.getcapacitor.JSObject result=new com.getcapacitor.JSObject();
        result.put("granted",granted);
        call.resolve(result);
    }

    @PluginMethod
    public void getFullScreenIntentStatus(PluginCall call) {
        NotificationManager manager=(NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        boolean granted=android.os.Build.VERSION.SDK_INT < 34 || (manager!=null && manager.canUseFullScreenIntent());
        com.getcapacitor.JSObject result=new com.getcapacitor.JSObject();
        result.put("granted",granted);
        call.resolve(result);
    }

    @PluginMethod
    public void openFullScreenIntentSettings(PluginCall call) {
        if(android.os.Build.VERSION.SDK_INT >= 34) {
            Intent intent=new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                Uri.parse("package:"+getContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        call.resolve();
    }

    @PluginMethod
    public void openExactAlarmSettings(PluginCall call) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Intent intent=new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:"+getContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
        call.resolve();
    }

    @PluginMethod
    public void getDiagnostics(PluginCall call) {
        com.getcapacitor.JSObject result=new com.getcapacitor.JSObject();
        AlarmManager am=(AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        NotificationManager nm=(NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        boolean exact=android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S || (am!=null && am.canScheduleExactAlarms());
        boolean full=android.os.Build.VERSION.SDK_INT < 34 || (nm!=null && nm.canUseFullScreenIntent());
        boolean notifications=android.os.Build.VERSION.SDK_INT < 33 ||
            androidx.core.content.ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.POST_NOTIFICATIONS)==android.content.pm.PackageManager.PERMISSION_GRANTED;
        result.put("exactAlarm",exact); result.put("fullScreenIntent",full); result.put("notifications",notifications);
        org.json.JSONObject snap=AlarmDiagnostics.snapshot(getContext());
        java.util.Iterator<String> keys=snap.keys();
        while(keys.hasNext()){String k=keys.next(); try{result.put(k,snap.get(k));}catch(Exception ignored){}}
        call.resolve(result);
    }

    @PluginMethod
    public void clearDiagnostics(PluginCall call) {
        AlarmDiagnostics.clear(getContext()); call.resolve();
    }

    @PluginMethod
    public void cancel(PluginCall call) {
        String id=call.getString("id","");
        AlarmScheduler.cancel(getContext(),id);
        call.resolve();
    }
}
