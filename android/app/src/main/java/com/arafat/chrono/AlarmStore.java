package com.arafat.chrono;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

public final class AlarmStore {
    private static final String PREFS = "chronocraft_alarm_store";
    private static final String KEY = "alarms";
    private AlarmStore() {}

    public static void save(Context context, JSONArray alarms) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, alarms.toString()).apply();
    }

    public static JSONArray load(Context context) {
        String raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]");
        try { return new JSONArray(raw); } catch (Exception e) { return new JSONArray(); }
    }

    public static JSONObject find(Context context, String id) {
        JSONArray items = load(context);
        for (int i=0;i<items.length();i++) {
            JSONObject a=items.optJSONObject(i);
            if (a!=null && id.equals(a.optString("id"))) return a;
        }
        return null;
    }

    public static void setEnabled(Context context, String id, boolean enabled) {
        JSONArray items = load(context);
        for (int i=0;i<items.length();i++) {
            JSONObject a=items.optJSONObject(i);
            if(a!=null && id.equals(a.optString("id"))) {
                try { a.put("enabled", enabled); } catch (Exception ignored) {}
                break;
            }
        }
        save(context, items);
    }

    public static void rescheduleAll(Context context) {
        JSONArray items=load(context);
        for(int i=0;i<items.length();i++) {
            JSONObject a=items.optJSONObject(i);
            if(a!=null && a.optBoolean("enabled",true)) AlarmScheduler.schedule(context,a);
        }
    }
}
