package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

public class BootBroadcastReceiver extends BroadcastReceiver {
    List<AlarmInformation> listOfAlarms;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            listOfAlarms = MySharePref.getList(context, "INFORMATION_ALARM", "INFORMATION_ALARM");
            if (listOfAlarms != null) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                for (AlarmInformation value : listOfAlarms) {
                    if (!value.monday && !value.tuesday && !value.wednesday && !value.thursday && !value.friday && !value.saturday && !value.sunday && value.switchEnableDisableAlarm) {
                        restartOneTimeAlarm(value, context, alarmManager);
                    }
                    if (value.monday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 1, 2);
                    if (value.tuesday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 2, 3);
                    if (value.wednesday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 3, 4);
                    if (value.thursday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 4, 5);
                    if (value.friday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 5, 6);
                    if (value.saturday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 6, 7);
                    if (value.sunday) restartRepeatingAlarms(value, context, alarmManager, value.alarmId + 7, 1);
                }
            }
        }
    }

    public void restartOneTimeAlarm(AlarmInformation value, Context context, AlarmManager alarmManager) {
        String[] time = value.alarmTime.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            value.setSwitchEnableDisableAlarm(false);
            MySharePref.writeList(context,"INFORMATION_ALARM", "INFORMATION_ALARM", listOfAlarms);
            return;
        }
        Log.e("BootReceiver", calendar.getTime().toString());
        System.out.println(value);
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("BundleFromBootReceiver", value);
        intent.putExtra("IntentFromBootReceiver", bundle);
        PendingIntent piOneTimeAlarm = PendingIntent.getBroadcast(context, value.alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), piOneTimeAlarm);
    }

    public void restartRepeatingAlarms(AlarmInformation value, Context context, AlarmManager alarmManager, int code, int day){
        String[] time = value.alarmTime.split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND,0);
        if (calendar.before(Calendar.getInstance())) calendar.add(Calendar.WEEK_OF_YEAR,1);
        System.out.println(value);
        Log.e("BootReceiver", calendar.getTime().toString());
        Intent intent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("BundleFromBootReceiver", value);
        intent.putExtra("IntentFromBootReceiver", bundle);
        PendingIntent piRepeatingAlarm = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), piRepeatingAlarm);
    }
}
