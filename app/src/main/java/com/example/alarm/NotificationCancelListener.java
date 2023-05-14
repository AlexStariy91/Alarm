package com.example.alarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.util.List;
public class NotificationCancelListener extends BroadcastReceiver {
    AlarmInformation alarmInformation;
    List<AlarmInformation> list;
    Bundle bundle;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            if (intent.getParcelableExtra("CancelListenerBundle") != null) {
                list = MySharePref.getList(context, "INFORMATION_ALARM", "INFORMATION_ALARM");
                bundle = intent.getParcelableExtra("CancelListenerBundle");
                alarmInformation = bundle.getParcelable("CancelListener");
                for (AlarmInformation value : list) {
                    if (value.monday || value.tuesday || value.wednesday || value.thursday || value.friday ||
                    value.saturday || value.sunday){value.setSwitchEnableDisableAlarm(true);}
                    else if (value.equals(alarmInformation)) {
                        value.setSwitchEnableDisableAlarm(false);
                    }
                }
                MySharePref.writeList(context,"INFORMATION_ALARM","INFORMATION_ALARM",list);
            }
        }

    }
}
