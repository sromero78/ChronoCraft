import React, { useEffect, useState } from 'react';
import { AlarmItem, WEEKDAYS, Weekday } from '../alarmTypes';
import { ColorName, SoundPreset } from '../types';
import { COLOR_KEYS } from '../constants/colors';
import { X, Bell, Check } from 'lucide-react';
import { useLanguage } from '../i18n';

interface AlarmCreateModalProps {
  isOpen: boolean;
  editingAlarm: AlarmItem | null;
  onClose: () => void;
  onSave: (alarm: Omit<AlarmItem, 'id' | 'createdAt'>, editingId?: string) => void;
}

export const AlarmCreateModal: React.FC<AlarmCreateModalProps> = ({ isOpen, editingAlarm, onClose, onSave }) => {
  const { t } = useLanguage();
  const [name, setName] = useState('Despertar');
  const [time, setTime] = useState('07:00');
  const [weekdays, setWeekdays] = useState<Weekday[]>([1,2,3,4,5]);
  const [color, setColor] = useState<ColorName>('indigo');
  const [sound, setSound] = useState<SoundPreset>('digital');
  const [vibrate, setVibrate] = useState(true);
  const [gradualVolume, setGradualVolume] = useState(false);
  const [snoozeMinutes, setSnoozeMinutes] = useState(5);

  useEffect(() => {
    if (!isOpen) return;
    if (editingAlarm) {
      setName(editingAlarm.name);
      setTime(`${String(editingAlarm.hour).padStart(2,'0')}:${String(editingAlarm.minute).padStart(2,'0')}`);
      setWeekdays(editingAlarm.weekdays);
      setColor(editingAlarm.color);
      setSound(editingAlarm.sound);
      setVibrate(editingAlarm.vibrate);
      setGradualVolume(editingAlarm.gradualVolume);
      setSnoozeMinutes(editingAlarm.snoozeMinutes);
    } else {
      setName('Despertar'); setTime('07:00'); setWeekdays([1,2,3,4,5]); setColor('indigo');
      setSound('digital'); setVibrate(true); setGradualVolume(false); setSnoozeMinutes(5);
    }
  }, [isOpen, editingAlarm]);

  if (!isOpen) return null;
  const toggleDay = (day: Weekday) => setWeekdays((prev) => prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day].sort());

  const submit = () => {
    const [hour, minute] = time.split(':').map(Number);
    onSave({ name: name.trim() || 'Alarma', hour, minute, weekdays, enabled: editingAlarm?.enabled ?? true, color, sound, vibrate, gradualVolume, snoozeMinutes }, editingAlarm?.id);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-[100] bg-slate-950/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="w-full sm:max-w-lg bg-white dark:bg-slate-900 rounded-t-3xl sm:rounded-3xl border border-slate-200 dark:border-slate-800 shadow-2xl max-h-[92dvh] overflow-y-auto overscroll-contain">
        <div className="sticky top-0 bg-white/95 dark:bg-slate-900/95 backdrop-blur px-4 py-3 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between z-10">
          <div className="flex items-center gap-2"><Bell className="w-5 h-5 text-indigo-500" /><h2 className="font-black">{editingAlarm ? t('editAlarm') : t('newAlarm')}</h2></div>
          <button onClick={onClose} className="p-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800"><X className="w-5 h-5" /></button>
        </div>
        <div className="p-4 space-y-5">
          <div className="text-center">
            <input type="time" value={time} onChange={(e) => setTime(e.target.value)} className="font-mono tabular-nums text-4xl sm:text-5xl font-black bg-transparent text-center w-full outline-none text-slate-900 dark:text-white" />
          </div>
          <label className="block"><span className="text-xs font-bold text-slate-500">{t('alarmName')}</span><input value={name} onChange={(e) => setName(e.target.value)} className="mt-1 w-full px-3 py-2.5 rounded-xl bg-slate-100 dark:bg-slate-800 outline-none focus:ring-2 focus:ring-indigo-500" /></label>
          <div><div className="text-xs font-bold text-slate-500 mb-2">{t('repeat')}</div><div className="grid grid-cols-7 gap-1.5">{WEEKDAYS.map((day) => <button key={day.id} onClick={() => toggleDay(day.id)} className={`aspect-square rounded-xl text-xs font-black ${weekdays.includes(day.id) ? 'bg-indigo-600 text-white shadow-sm' : 'bg-slate-100 dark:bg-slate-800 text-slate-500'}`}>{day.short}</button>)}</div></div>
          <div><div className="text-xs font-bold text-slate-500 mb-2">{t('color')}</div><div className="flex flex-wrap gap-2">{COLOR_KEYS.map((c) => <button key={c} onClick={() => setColor(c)} className={`w-7 h-7 rounded-full border-2 ${color === c ? 'border-slate-900 dark:border-white scale-110' : 'border-transparent'}`} style={{backgroundColor: ({blue:'#3b82f6',emerald:'#10b981',violet:'#8b5cf6',rose:'#f43f5e',amber:'#f59e0b',cyan:'#06b6d4',indigo:'#6366f1',coral:'#f97316'} as any)[c] || '#6366f1'}} />)}</div></div>
          <label className="block"><span className="text-xs font-bold text-slate-500">{t('sound')}</span><select value={sound} onChange={(e) => setSound(e.target.value as SoundPreset)} className="mt-1 w-full px-3 py-2.5 rounded-xl bg-slate-100 dark:bg-slate-800 outline-none"><option value="digital">{t('digitalAlarm')}</option><option value="chime">{t('gentleChime')}</option><option value="bell">{t('classicBell')}</option><option value="marimba">Marimba</option><option value="gentle">{t('softPulse')}</option></select></label>
          <div className="grid grid-cols-2 gap-2">
            <button onClick={() => setVibrate(!vibrate)} className={`p-3 rounded-xl border text-xs font-bold ${vibrate ? 'border-indigo-300 bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600' : 'border-slate-200 dark:border-slate-700 text-slate-500'}`}>{t('vibration')} {vibrate ? 'ON' : 'OFF'}</button>
            <button onClick={() => setGradualVolume(!gradualVolume)} className={`p-3 rounded-xl border text-xs font-bold ${gradualVolume ? 'border-indigo-300 bg-indigo-50 dark:bg-indigo-950/40 text-indigo-600' : 'border-slate-200 dark:border-slate-700 text-slate-500'}`}>{t('gradualVolume')} {gradualVolume ? 'ON' : 'OFF'}</button>
          </div>
          <label className="block"><span className="text-xs font-bold text-slate-500">{t('snooze')}</span><select value={snoozeMinutes} onChange={(e) => setSnoozeMinutes(Number(e.target.value))} className="mt-1 w-full px-3 py-2.5 rounded-xl bg-slate-100 dark:bg-slate-800 outline-none"><option value={0}>{t('disabled')}</option><option value={5}>5 {t('minutes')}</option><option value={10}>10 {t('minutes')}</option><option value={15}>15 {t('minutes')}</option></select></label>
          <button onClick={submit} className="w-full py-3 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-black flex items-center justify-center gap-2"><Check className="w-4 h-4" />{t('saveAlarm')}</button>
        </div>
      </div>
    </div>
  );
};
