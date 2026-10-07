import React, { useEffect, useMemo, useState } from 'react';
import { capacitorBridge } from '../utils/capacitorNativeBridge';
import { AlarmItem, WEEKDAYS } from '../alarmTypes';
import { AlarmCard } from './AlarmCard';
import { Bell, CalendarDays, ListOrdered, Plus } from 'lucide-react';
import { useLanguage } from '../i18n';

interface AlarmsViewProps {
  alarms: AlarmItem[];
  onCreate: () => void;
  onToggle: (id: string) => void;
  onEdit: (alarm: AlarmItem) => void;
  onDuplicate: (alarm: AlarmItem) => void;
  onDelete: (id: string) => void;
  onToggleDay: (weekday: number, enabled: boolean) => void;
}

function nextOccurrence(alarm: AlarmItem, now = new Date()) {
  if (!alarm.enabled) return Number.POSITIVE_INFINITY;
  for (let add = 0; add < 8; add++) {
    const d = new Date(now);
    d.setDate(now.getDate() + add);
    d.setHours(alarm.hour, alarm.minute, 0, 0);
    const isoDay = d.getDay() === 0 ? 7 : d.getDay();
    if ((alarm.weekdays.length === 0 ? add === 0 : alarm.weekdays.includes(isoDay as any)) && d.getTime() > now.getTime()) return d.getTime();
  }
  return Number.POSITIVE_INFINITY;
}

export const AlarmsView: React.FC<AlarmsViewProps> = (props) => {
  const [mode, setMode] = useState<'week' | 'next'>('week');
  const { t } = useLanguage();
  const [diag, setDiag] = useState<any>(null);
  const [diagLoading, setDiagLoading] = useState(false);
  const refreshDiag = async () => { setDiagLoading(true); try { setDiag(await capacitorBridge.getAlarmDiagnostics()); } finally { setDiagLoading(false); } };
  useEffect(() => {
    if (!capacitorBridge.isAndroid()) return;
    refreshDiag();
    const refreshWhenVisible=()=>{ if(document.visibilityState==='visible') refreshDiag(); };
    window.addEventListener('focus',refreshDiag);
    document.addEventListener('visibilitychange',refreshWhenVisible);
    return ()=>{ window.removeEventListener('focus',refreshDiag); document.removeEventListener('visibilitychange',refreshWhenVisible); };
  }, []);
  const requestNotifications = async () => { await capacitorBridge.requestAlarmNotificationPermission(); await refreshDiag(); };
  const fmt = (v: number) => v ? new Date(v).toLocaleString() : '—';
  const upcoming = useMemo(() => [...props.alarms].filter(a => a.enabled).sort((a,b) => nextOccurrence(a)-nextOccurrence(b)), [props.alarms]);

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between gap-2">
        <div><div className="flex items-center gap-2"><Bell className="w-5 h-5 text-indigo-500" /><h2 className="text-lg sm:text-xl font-black">{t('alarms')}</h2></div><p className="text-xs text-slate-400 mt-0.5">{t('weekPlanner')}</p></div>
        <div className="flex items-center gap-1.5">
          <div className="flex p-1 rounded-xl bg-slate-100 dark:bg-slate-800 border border-slate-200 dark:border-slate-700">
            <button onClick={() => setMode('week')} className={`p-1.5 rounded-lg ${mode==='week'?'bg-white dark:bg-slate-900 text-indigo-500 shadow-sm':'text-slate-400'}`} title="Semana"><CalendarDays className="w-4 h-4"/></button>
            <button onClick={() => setMode('next')} className={`p-1.5 rounded-lg ${mode==='next'?'bg-white dark:bg-slate-900 text-indigo-500 shadow-sm':'text-slate-400'}`} title="Próximas"><ListOrdered className="w-4 h-4"/></button>
          </div>
          <button onClick={props.onCreate} className="px-3 py-2 rounded-xl bg-indigo-600 text-white text-xs font-bold flex items-center gap-1"><Plus className="w-4 h-4"/>{t('newAlarm')}</button>
        </div>
      </div>

      {capacitorBridge.isAndroid() && (
        <div className="rounded-2xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 p-3 text-xs">
          <div className="flex items-center justify-between gap-2 mb-2">
            <span className="font-black">Diagnóstico de alarmas</span>
            <button onClick={refreshDiag} className="px-2.5 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 font-bold text-indigo-500">{diagLoading ? 'Leyendo…' : 'Actualizar'}</button>
          </div>
          {diag && (!diag.notifications || !diag.exactAlarm || !diag.fullScreenIntent) && <div className="mb-3 p-2.5 rounded-xl bg-amber-50 dark:bg-amber-950/30 border border-amber-200 dark:border-amber-900">
            <div className="font-bold text-amber-800 dark:text-amber-300 mb-2">ChronoCraft necesita permisos para que las alarmas funcionen correctamente.</div>
            <div className="flex flex-wrap gap-2">
              {!diag.notifications && <button onClick={requestNotifications} className="px-2.5 py-1.5 rounded-lg bg-indigo-600 text-white font-bold">Permitir notificaciones</button>}
              {!diag.exactAlarm && <button onClick={async()=>{await capacitorBridge.openExactAlarmSettings();}} className="px-2.5 py-1.5 rounded-lg bg-indigo-600 text-white font-bold">Permitir alarmas exactas</button>}
              {!diag.fullScreenIntent && <button onClick={async()=>{await capacitorBridge.openFullScreenIntentSettings();}} className="px-2.5 py-1.5 rounded-lg bg-indigo-600 text-white font-bold">Permitir pantalla completa</button>}
            </div>
          </div>}
          {diag ? <div className="grid grid-cols-1 sm:grid-cols-2 gap-x-4 gap-y-1 text-slate-600 dark:text-slate-300">
            <div>Alarma exacta: <b className={diag.exactAlarm?'text-emerald-500':'text-rose-500'}>{diag.exactAlarm?'PERMITIDA':'BLOQUEADA'}</b></div>
            <div>Pantalla completa: <b className={diag.fullScreenIntent?'text-emerald-500':'text-rose-500'}>{diag.fullScreenIntent?'PERMITIDA':'BLOQUEADA'}</b></div>
            <div>Notificaciones: <b className={diag.notifications?'text-emerald-500':'text-rose-500'}>{diag.notifications?'PERMITIDAS':'BLOQUEADAS'}</b></div>
            <div>Programada para: <b>{fmt(diag.scheduledFor)}</b></div>
            <div>Receiver ejecutado: <b>{fmt(diag.receiverAt)}</b></div>
            <div>Notificación creada: <b>{fmt(diag.notificationAt)}</b></div>
            <div>Activity abierta: <b>{fmt(diag.activityAt)}</b></div>
          </div> : <div className="text-slate-400">Pulsa Actualizar para leer el estado nativo de Android.</div>}
        </div>
      )}

      {props.alarms.length === 0 ? (
        <div className="py-14 text-center rounded-2xl border border-dashed border-slate-300 dark:border-slate-700"><Bell className="w-8 h-8 mx-auto text-slate-300 mb-2"/><div className="font-bold">{t('noAlarms')}</div><button onClick={props.onCreate} className="mt-3 text-sm font-bold text-indigo-500">{t('createFirst')}</button></div>
      ) : mode === 'next' ? (
        <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-2.5 sm:gap-4">{upcoming.map(a => <AlarmCard key={a.id} alarm={a} onToggle={props.onToggle} onEdit={props.onEdit} onDuplicate={props.onDuplicate} onDelete={props.onDelete}/>)}</div>
      ) : (
        <div className="space-y-5">
          {WEEKDAYS.map(day => {
            const items = props.alarms.filter(a => a.weekdays.includes(day.id)).sort((a,b) => a.hour*60+a.minute-(b.hour*60+b.minute));
            const dayEnabled = items.some(a => a.enabled);
            return (
              <div key={day.id}>
                <div className="flex items-center justify-between mb-2 px-0.5">
                  <div className="flex items-baseline gap-2"><h3 className="font-black text-sm uppercase tracking-wide">{day.label}</h3><span className="text-[10px] text-slate-400 font-bold">{items.length} {items.length===1?t('oneAlarm'):t('manyAlarms')}</span></div>
                  {items.length > 0 && <button onClick={() => props.onToggleDay(day.id, !dayEnabled)} className="text-[10px] font-bold text-slate-400 hover:text-indigo-500">{dayEnabled?t('deactivateDay'):t('activateDay')}</button>}
                </div>
                {items.length ? <div className="grid grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-2.5 sm:gap-4">{items.map(a => <AlarmCard key={`${day.id}-${a.id}`} alarm={a} onToggle={props.onToggle} onEdit={props.onEdit} onDuplicate={props.onDuplicate} onDelete={props.onDelete}/>)}</div> : <button onClick={props.onCreate} className="w-full py-3 rounded-xl border border-dashed border-slate-200 dark:border-slate-800 text-xs text-slate-400 hover:text-indigo-500">+ Añadir alarma</button>}
              </div>
            );
          })}
        </div>
      )}
    </section>
  );
};
