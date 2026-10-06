import { AlarmItem, Weekday } from '../alarmTypes';

const ALARMS_KEY = 'chronocraft_alarms_v1';

function validWeekday(value: unknown): value is Weekday {
  return Number.isInteger(value) && Number(value) >= 1 && Number(value) <= 7;
}

export function loadAlarms(): AlarmItem[] {
  if (typeof window === 'undefined') return [];
  try {
    const raw = localStorage.getItem(ALARMS_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return [];
    return parsed
      .filter((item) => item && typeof item === 'object')
      .map((item: any) => ({
        id: String(item.id || `alarm-${Date.now()}-${Math.random().toString(36).slice(2, 7)}`),
        name: String(item.name || 'Alarma'),
        hour: Math.min(23, Math.max(0, Number(item.hour) || 0)),
        minute: Math.min(59, Math.max(0, Number(item.minute) || 0)),
        weekdays: Array.isArray(item.weekdays) ? item.weekdays.filter(validWeekday) : [],
        enabled: item.enabled !== false,
        color: item.color || 'indigo',
        sound: item.sound || 'chime',
        vibrate: item.vibrate !== false,
        gradualVolume: item.gradualVolume === true,
        snoozeMinutes: Number.isFinite(Number(item.snoozeMinutes)) ? Math.max(0, Number(item.snoozeMinutes)) : 5,
        createdAt: Number(item.createdAt) || Date.now(),
      }));
  } catch {
    return [];
  }
}

export function saveAlarms(items: AlarmItem[]) {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(ALARMS_KEY, JSON.stringify(items));
  } catch (e) {
    console.error('Failed to save alarms', e);
  }
}
