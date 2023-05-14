package com.example.alarm;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import com.example.alarm.databinding.ActivityFireAlarmBinding;
import java.util.Calendar;
import java.util.List;

public class FireAlarm extends AppCompatActivity {
    ActivityFireAlarmBinding binding;
    AlarmInformation alarmInformation;
    List<AlarmInformation> list;
    Bundle bundle;
    boolean repeatSignal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFireAlarmBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        turnScreenOnAndKeyguardOff();

        setShowWhenLocked(true);
        setContentView(view);
        getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        list = MySharePref.getList(this, "INFORMATION_ALARM", "INFORMATION_ALARM");
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getParcelableExtra("CancelListenerBundle") != null) {
                bundle = intent.getParcelableExtra("CancelListenerBundle");
                alarmInformation = bundle.getParcelable("CancelListener");
                repeatSignal = alarmInformation.switchRepeatSignal;
            }
        }

        if (repeatSignal) binding.bRepeatSound.setVisibility(View.VISIBLE);
        else binding.bRepeatSound.setVisibility(View.GONE);

        binding.bStopAlarm.setOnClickListener(view1 -> {
            for (AlarmInformation value : list) {
                if (value.monday || value.tuesday || value.wednesday || value.thursday || value.friday ||
                        value.saturday || value.sunday){value.setSwitchEnableDisableAlarm(true);}
                else if (value.equals(alarmInformation)) {
                    value.setSwitchEnableDisableAlarm(false);
                }
            }
            MySharePref.writeList(this, "INFORMATION_ALARM", "INFORMATION_ALARM", list);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(5);
            finish();
        });

        binding.bRepeatSound.setOnClickListener(view2 -> {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(5);
            Calendar calendar = Calendar.getInstance();
            Intent intent1 = new Intent(FireAlarm.this, AlarmReceiver.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("AlarmInformationFromFireAlarm", alarmInformation);
            intent1.putExtra("BundleAlarmInformationFromFireAlarm", bundle);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(FireAlarm.this, alarmInformation.alarmId
                    , intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            AlarmManager alarmManager = (AlarmManager) FireAlarm.this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis() + 5 * 60 * 1000, null), pendingIntent);
            finish();
        });

    }

    private void turnScreenOnAndKeyguardOff() {
        setTurnScreenOn(true);
        setShowWhenLocked(true);
        ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).requestDismissKeyguard(this, null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
