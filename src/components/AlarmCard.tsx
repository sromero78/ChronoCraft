import React from 'react';
import { AlarmItem, WEEKDAYS } from '../alarmTypes';
import { getColorTheme } from '../constants/colors';
import { Bell, Copy, Pencil, Trash2 } from 'lucide-react';
import { useLanguage } from '../i18n';

interface AlarmCardProps {
  alarm: AlarmItem;
  onToggle: (id: string) => void;
  onEdit: (alarm: AlarmItem) => void;
  onDuplicate: (alarm: AlarmItem) => void;
  onDelete: (id: string) => void;
}

export const AlarmCard: React.FC<AlarmCardProps> = ({ alarm, onToggle, onEdit, onDuplicate, onDelete }) => {
  const { t } = useLanguage();
  const theme = getColorTheme(alarm.color);
  const time = `${String(alarm.hour).padStart(2, '0')}:${String(alarm.minute).padStart(2, '0')}`;
  const days = WEEKDAYS.filter((day) => alarm.weekdays.includes(day.id)).map((day) => day.short).join(' ');

  return (
    <article
      className={`relative overflow-hidden rounded-xl sm:rounded-2xl border bg-white dark:bg-slate-900/90 shadow-sm transition-all ${theme.border} ${alarm.enabled ? '' : 'opacity-60'}`}
      style={{ borderTopColor: theme.accentHex, borderTopWidth: 3 }}
    >
      <div className="px-3 py-2 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between gap-2">
        <div className="min-w-0 flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: theme.accentHex }} />
          <h3 className="font-bold text-xs sm:text-sm truncate text-slate-800 dark:text-slate-100">{alarm.name}</h3>
        </div>
        <button
          type="button"
          role="switch"
          aria-checked={alarm.enabled}
          onClick={() => onToggle(alarm.id)}
          className={`relative w-9 h-5 rounded-full transition-colors shrink-0 ${alarm.enabled ? 'bg-emerald-500' : 'bg-slate-300 dark:bg-slate-700'}`}
          title={alarm.enabled ? t('deactivateAlarm') : t('activateAlarm')}
        >
          <span className={`absolute top-0.5 w-4 h-4 bg-white rounded-full shadow transition-transform ${alarm.enabled ? 'translate-x-0.5' : '-translate-x-4.5'}`} style={{ right: alarm.enabled ? 2 : undefined, left: alarm.enabled ? undefined : 2 }} />
        </button>
      </div>

      <button type="button" onClick={() => onEdit(alarm)} className="w-full px-3 pt-3 pb-2 text-left cursor-pointer">
        <div className="flex items-center justify-between gap-3">
          <div className="font-mono tabular-nums text-3xl sm:text-4xl font-black tracking-tight text-slate-900 dark:text-white">{time}</div>
          <Bell className="w-5 h-5 shrink-0" style={{ color: theme.accentHex }} />
        </div>
        <div className="mt-1 text-[10px] sm:text-xs font-bold tracking-wider text-slate-400">{days || t('oneTime')}</div>
      </button>

      <div className="px-2.5 py-2 flex items-center justify-end gap-1 border-t border-slate-100 dark:border-slate-800">
        <button onClick={() => onEdit(alarm)} className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-500 hover:bg-slate-100 dark:hover:bg-slate-800" title={t('edit')}><Pencil className="w-3.5 h-3.5" /></button>
        <button onClick={() => onDuplicate(alarm)} className="p-1.5 rounded-lg text-slate-400 hover:text-indigo-500 hover:bg-slate-100 dark:hover:bg-slate-800" title={t('duplicate')}><Copy className="w-3.5 h-3.5" /></button>
        <button onClick={() => onDelete(alarm.id)} className="p-1.5 rounded-lg text-slate-400 hover:text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/30" title={t('delete')}><Trash2 className="w-3.5 h-3.5" /></button>
      </div>
    </article>
  );
};
