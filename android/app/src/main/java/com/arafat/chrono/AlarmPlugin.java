package com.arafat.chrono;

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
    public void cancel(PluginCall call) {
        String id=call.getString("id","");
        AlarmScheduler.cancel(getContext(),id);
        call.resolve();
    }
}
