package com.example.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AlarmReceiver extends BroadcastReceiver {
    Uri melody;
    AlarmInformation alarmInformation;
    Bundle bundle;
    private final String CHANNEL_ID = generateChannelId();
    Calendar calendar = Calendar.getInstance();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            Intent fullScreenIntent = new Intent(context, FireAlarm.class);
            Intent iBroad1 = new Intent(context, NotificationCancelListener.class);

            if (intent.getParcelableExtra("IntentFromBootReceiver") != null){
                bundle = intent.getParcelableExtra("IntentFromBootReceiver");
                alarmInformation = bundle.getParcelable("BundleFromBootReceiver");
                notificationCancelIntent(fullScreenIntent, iBroad1);
            }

            if (intent.getParcelableExtra("BundleAlarmInformationFromSetAlarmActivity") != null) {
                bundle = intent.getParcelableExtra("BundleAlarmInformationFromSetAlarmActivity");
                alarmInformation = bundle.getParcelable("AlarmInformationFromSetAlarmActivity");
                notificationCancelIntent(fullScreenIntent, iBroad1);
            }

            if (intent.getParcelableExtra("BundleAlarmInformationFromAdapter") != null) {
                bundle = intent.getParcelableExtra("BundleAlarmInformationFromAdapter");
                alarmInformation = bundle.getParcelable("AlarmInformationFromAdapter");
                notificationCancelIntent(fullScreenIntent, iBroad1);
            }

            if (intent.getParcelableExtra("BundleAlarmInformationFromFireAlarm") != null) {
                bundle = intent.getParcelableExtra("BundleAlarmInformationFromFireAlarm");
                alarmInformation = bundle.getParcelable("AlarmInformationFromFireAlarm");
                notificationCancelIntent(fullScreenIntent, iBroad1);
            }

            if (intent.getParcelableExtra("RepeatAlarmFromReceiver") != null){
                bundle = intent.getParcelableExtra("RepeatAlarmFromReceiver");
                alarmInformation = bundle.getParcelable("BundleRepeatAlarmFromReceiver");
                notificationCancelIntent(fullScreenIntent, iBroad1);
                System.out.println("SUCCEED");
            }

            Intent repeatDaysAlarmInformation = new Intent(context, AlarmReceiver.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String[] time = alarmInformation.alarmTime.split(":");

            if (alarmInformation.monday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +1, alarmInformation.alarmId);
            }

            if (alarmInformation.tuesday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +2, alarmInformation.alarmId);
            }

            if (alarmInformation.wednesday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +3, alarmInformation.alarmId);
            }

            if (alarmInformation.thursday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +4, alarmInformation.alarmId);
            }

            if (alarmInformation.friday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +5, alarmInformation.alarmId);
            }

            if (alarmInformation.saturday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +6, alarmInformation.alarmId);
            }

            if (alarmInformation.sunday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                setRepeatingDays(context, repeatDaysAlarmInformation, alarmManager, alarmInformation.alarmId +7, alarmInformation.alarmId);
            }

            NotificationChannel channel;
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            channel = new NotificationChannel(CHANNEL_ID, "description", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setVibrationPattern(new long[]{500, 500, 500, 1000, 1000});

            AudioAttributes att = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            channel.setSound(melody, att);
            channel.enableVibration(true);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }

            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("ALARM")
                    .setContentText("It's time to get up!!!")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(melody)
                    .setContentIntent(fullScreenPendingIntent)
                    .setDeleteIntent(PendingIntent.getBroadcast(context, 0, iBroad1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE))
                    .setVibrate(new long[]{500, 500, 500, 1000, 1000})
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setFullScreenIntent(fullScreenPendingIntent, true);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_INSISTENT;
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            NotificationManagerCompat.from(context).notify(5, notification);
            assert manager != null;
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (NotificationChannel chan : channels) {
                if (!Objects.equals(chan.getId(), CHANNEL_ID)) {
                    manager.deleteNotificationChannel(chan.getId());
                }
            }
        }
    }

    public String generateChannelId() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "");
    }

    public void notificationCancelIntent(Intent fireAlarmIntent, Intent cancelNotificationIntent) {
        melody = Uri.parse(alarmInformation.alarmSound);
        Bundle bundle = new Bundle();
        bundle.putParcelable("CancelListener", alarmInformation);
        cancelNotificationIntent.putExtra("CancelListenerBundle", bundle);
        fireAlarmIntent.putExtra("CancelListenerBundle", bundle);
    }

    public void setRepeatingDays(Context context,Intent intent, AlarmManager alarmManager,int PreviousAlarmId ,int piIntent){
        PendingIntent piCancelPreviousAlarm = PendingIntent.getBroadcast(context, PreviousAlarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(piCancelPreviousAlarm);
        long time = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY * 7;
        Calendar logCalendar = Calendar.getInstance();
        logCalendar.setTimeInMillis(time);
        Bundle repeatDaysAlarmInformationBundle = new Bundle();
        repeatDaysAlarmInformationBundle.putParcelable("BundleRepeatAlarmFromReceiver", alarmInformation);
        intent.putExtra("RepeatAlarmFromReceiver",repeatDaysAlarmInformationBundle);
        PendingIntent piDays = PendingIntent.getBroadcast(context, piIntent, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(time, null), piDays);
        Log.e("NextDayRepeatLOG", logCalendar.getTime() + " ID of next alarm" + piIntent);
    }
}
