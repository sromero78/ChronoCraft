import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

export type AppLanguage = 'es' | 'en';

const STORAGE_KEY = 'chronocraft_language';

const messages = {
  es: {
    all: 'Todo', stopwatches: 'Cronómetros', timers: 'Temporizadores', alarms: 'Alarmas',
    running: 'activos', presets: 'Preajustes', add: 'Añadir', weekPlanner: 'Planificador semanal',
    week: 'Semana', upcoming: 'Próximas', newAlarm: 'Alarma', noAlarms: 'Todavía no hay alarmas',
    createFirst: 'Crear la primera', alarm: 'Alarma', editAlarm: 'Editar alarma',
    alarmName: 'Nombre', repeat: 'Repetir', color: 'Color', sound: 'Sonido',
    vibration: 'Vibración', gradualVolume: 'Volumen gradual', snooze: 'Posponer',
    disabled: 'Desactivado', minutes: 'minutos', saveAlarm: 'Guardar alarma',
    deactivateDay: 'DESACTIVAR DÍA', activateDay: 'ACTIVAR DÍA', oneAlarm: 'alarma', manyAlarms: 'alarmas',
    oneTime: 'UNA VEZ', edit: 'Editar', duplicate: 'Duplicar', delete: 'Eliminar',
    activateAlarm: 'Activar alarma', deactivateAlarm: 'Desactivar alarma', language: 'Idioma',
    spanish: 'Español', english: 'English', tools: 'Herramientas', preferences: 'Preferencias y asistencia', history: 'Historial', view: 'Ver', settings: 'Sonido y ajustes', configure: 'Configurar', wakeLock: 'Mantener pantalla activa', voiceCoach: 'Asistente de voz', batchControls: 'Controles en grupo', startAll: 'Iniciar todos', pauseAll: 'Pausar todos', resetAll: 'Reiniciar todos', soundActive: 'Sonido activo', soundMuted: 'Sonido silenciado', addClock: 'Añadir reloj', digitalAlarm: 'Alarma digital', gentleChime: 'Campanillas suaves', classicBell: 'Campana clásica', softPulse: 'Pulso suave'
  },
  en: {
    all: 'All', stopwatches: 'Stopwatches', timers: 'Timers', alarms: 'Alarms',
    running: 'running', presets: 'Presets', add: 'Add', weekPlanner: 'Weekly planner',
    week: 'Week', upcoming: 'Upcoming', newAlarm: 'Alarm', noAlarms: 'No alarms yet',
    createFirst: 'Create the first one', alarm: 'Alarm', editAlarm: 'Edit alarm',
    alarmName: 'Name', repeat: 'Repeat', color: 'Color', sound: 'Sound',
    vibration: 'Vibration', gradualVolume: 'Gradual volume', snooze: 'Snooze',
    disabled: 'Disabled', minutes: 'minutes', saveAlarm: 'Save alarm',
    deactivateDay: 'DISABLE DAY', activateDay: 'ENABLE DAY', oneAlarm: 'alarm', manyAlarms: 'alarms',
    oneTime: 'ONCE', edit: 'Edit', duplicate: 'Duplicate', delete: 'Delete',
    activateAlarm: 'Enable alarm', deactivateAlarm: 'Disable alarm', language: 'Language',
    spanish: 'Español', english: 'English', tools: 'Tools', preferences: 'Preferences & assist', history: 'Run history', view: 'View', settings: 'Sound & settings', configure: 'Configure', wakeLock: 'Screen wake lock', voiceCoach: 'Voice coach', batchControls: 'Batch controls', startAll: 'Start all', pauseAll: 'Pause all', resetAll: 'Reset all', soundActive: 'Sound active', soundMuted: 'Sound muted', addClock: 'Add clock', digitalAlarm: 'Digital alarm', gentleChime: 'Gentle chime', classicBell: 'Classic bell', softPulse: 'Soft pulse'
  }
} as const;

type MessageKey = keyof typeof messages.es;
interface LanguageContextValue {
  language: AppLanguage;
  setLanguage: (language: AppLanguage) => void;
  t: (key: MessageKey) => string;
}

const LanguageContext = createContext<LanguageContextValue | null>(null);

export const LanguageProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
  const [language, setLanguage] = useState<AppLanguage>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      return saved === 'en' ? 'en' : 'es';
    } catch {
      return 'es';
    }
  });

  useEffect(() => {
    try { localStorage.setItem(STORAGE_KEY, language); } catch {}
    document.documentElement.lang = language;
  }, [language]);

  const value = useMemo(() => ({
    language,
    setLanguage,
    t: (key: MessageKey) => messages[language][key],
  }), [language]);

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>;
};

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (!context) throw new Error('useLanguage must be used inside LanguageProvider');
  return context;
}
