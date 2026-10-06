import { ColorName, SoundPreset } from './types';

export type Weekday = 1 | 2 | 3 | 4 | 5 | 6 | 7;

export interface AlarmItem {
  id: string;
  name: string;
  hour: number;
  minute: number;
  weekdays: Weekday[];
  enabled: boolean;
  color: ColorName;
  sound: SoundPreset;
  vibrate: boolean;
  gradualVolume: boolean;
  snoozeMinutes: number;
  createdAt: number;
}

export const WEEKDAYS: { id: Weekday; short: string; label: string }[] = [
  { id: 1, short: 'L', label: 'Lunes' },
  { id: 2, short: 'M', label: 'Martes' },
  { id: 3, short: 'X', label: 'Miércoles' },
  { id: 4, short: 'J', label: 'Jueves' },
  { id: 5, short: 'V', label: 'Viernes' },
  { id: 6, short: 'S', label: 'Sábado' },
  { id: 7, short: 'D', label: 'Domingo' },
];
