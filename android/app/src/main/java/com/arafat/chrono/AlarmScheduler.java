package com.arafat.chrono;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;

public final class AlarmScheduler {
    private AlarmScheduler() {}

    public static long nextTrigger(int hour, int minute, JSONArray weekdays) {
        Calendar now = Calendar.getInstance();
        long best = Long.MAX_VALUE;
        if (weekdays == null || weekdays.length() == 0) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour); c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
            if (c.getTimeInMillis() <= now.getTimeInMillis()) c.add(Calendar.DAY_OF_YEAR, 1);
            return c.getTimeInMillis();
        }
        for (int i=0; i<weekdays.length(); i++) {
            int iso = weekdays.optInt(i, 1);
            int androidDay = iso == 7 ? Calendar.SUNDAY : iso + 1;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour); c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
            int delta = (androidDay - c.get(Calendar.DAY_OF_WEEK) + 7) % 7;
            c.add(Calendar.DAY_OF_YEAR, delta);
            if (c.getTimeInMillis() <= now.getTimeInMillis()) c.add(Calendar.DAY_OF_YEAR, 7);
            best = Math.min(best, c.getTimeInMillis());
        }
        return best;
    }

    public static void schedule(Context context, JSONObject alarm) {
        if (!alarm.optBoolean("enabled", true)) { cancel(context, alarm.optString("id")); return; }
        String id = alarm.optString("id");
        if (id.isEmpty()) return;
        long trigger = nextTrigger(alarm.optInt("hour"), alarm.optInt("minute"), alarm.optJSONArray("weekdays"));
        Intent fire = new Intent(context, AlarmReceiver.class).setAction("com.arafat.chrono.FIRE_ALARM");
        fire.putExtra("alarmId", id);
        fire.putExtra("name", alarm.optString("name", "Alarm"));
        fire.putExtra("snoozeMinutes", alarm.optInt("snoozeMinutes", 5));
        fire.putExtra("vibrate", alarm.optBoolean("vibrate", true));
        PendingIntent operation = PendingIntent.getBroadcast(context, requestCode(id), fire,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent show = new Intent(context, MainActivity.class).setAction("com.arafat.chrono.SHOW_ALARM").putExtra("alarmId", id);
        PendingIntent showIntent = PendingIntent.getActivity(context, requestCode(id + "-show"), show,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) manager.setAlarmClock(new AlarmManager.AlarmClockInfo(trigger, showIntent), operation);
    }

    public static void cancel(Context context, String id) {
        if (id == null || id.isEmpty()) return;
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent fire = new Intent(context, AlarmReceiver.class).setAction("com.arafat.chrono.FIRE_ALARM");
        PendingIntent operation = PendingIntent.getBroadcast(context, requestCode(id), fire,
            PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (manager != null && operation != null) { manager.cancel(operation); operation.cancel(); }
    }

    public static int requestCode(String id) { return id == null ? 0 : id.hashCode() & 0x7fffffff; }
}
