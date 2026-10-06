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
        AlarmStore.save(getContext(), alarms);
        for(int i=0;i<alarms.length();i++) {
            JSONObject alarm=alarms.optJSONObject(i);
            if(alarm!=null) AlarmScheduler.schedule(getContext(),alarm);
        }
        call.resolve();
    }

    @PluginMethod
    public void cancel(PluginCall call) {
        String id=call.getString("id","");
        AlarmScheduler.cancel(getContext(),id);
        call.resolve();
    }
}
