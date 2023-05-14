package com.example.alarm;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.alarm.databinding.ActivitySetAlarmMenuBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SetAlarmActivity extends AppCompatActivity {
    int ALARM_REQ_CODE = new Random().nextInt();
    ActivitySetAlarmMenuBinding binding;
    Uri savedMelody;
    ActivityResultLauncher<Intent> melodyTitle;
    ActivityResultLauncher<Intent> repeatDays;
    AlarmManager alarmManager;
    Calendar calendar;
    List<Integer> listAlarmIDs;
    AlarmInformation previouslySettedAlarmInformation;
    AlarmInformation temporaryChosenDays;
    Uri defaultMelody = Uri.parse("android.resource://com.example.alarm/raw/silk");
    AlarmInformation information;

    public enum DaysOfTheWeek {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    }

    Set<DaysOfTheWeek> headerOfDays = new HashSet<>();
    boolean alarmEnabled;
    boolean repeatSignal = false;
    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    boolean sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetAlarmMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        System.out.println(ALARM_REQ_CODE);
        System.out.println(listAlarmIDs);
        getWindow().setStatusBarColor(Color.parseColor("#2a343c"));
        getWindow().setNavigationBarColor(Color.parseColor("#2a343c"));
        if (getIntent().getStringExtra("ChangeTitle") != null) {
            binding.tvAdding.setText(getIntent().getStringExtra("ChangeTitle"));
        }

        if (getIntent().getParcelableExtra("ExistingAlarmInformation") != null) {
            previouslySettedAlarmInformation = getIntent().getParcelableExtra("ExistingAlarmInformation");
            ALARM_REQ_CODE = previouslySettedAlarmInformation.alarmId;
            System.out.println(ALARM_REQ_CODE);
            savedMelody = Uri.parse(previouslySettedAlarmInformation.alarmSound);
            repeatSignal = previouslySettedAlarmInformation.switchRepeatSignal;
            Ringtone ringtone = RingtoneManager.getRingtone(this, savedMelody);
            binding.tvMelody.setText(ringtone.getTitle(this));
            String[] splitTime = previouslySettedAlarmInformation.alarmTime.split(":");
            int hour = Integer.parseInt(splitTime[0]);
            int minute = Integer.parseInt(splitTime[1]);
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            binding.tpTimeChoice.setHour(hour);
            binding.tpTimeChoice.setMinute(minute);
            monday = previouslySettedAlarmInformation.monday;
            tuesday = previouslySettedAlarmInformation.tuesday;
            wednesday = previouslySettedAlarmInformation.wednesday;
            thursday = previouslySettedAlarmInformation.thursday;
            friday = previouslySettedAlarmInformation.friday;
            saturday = previouslySettedAlarmInformation.saturday;
            sunday = previouslySettedAlarmInformation.sunday;
            setChosenDaysToTextView();
        }

        if (savedMelody == null) {
            savedMelody = defaultMelody;
            binding.tvMelody.setText("Silk");
        }

        binding.tpTimeChoice.setOnTimeChangedListener((timePicker, selectedHour, selectedMinute) -> {
            calendar = Calendar.getInstance();
            timePicker.setIs24HourView(true);
            selectedHour = binding.tpTimeChoice.getHour();
            selectedMinute = binding.tpTimeChoice.getMinute();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
        });
        binding.tpTimeChoice.setIs24HourView(true);
        binding.tvCancel.setOnClickListener(view1 -> finish());
        melodyTitle = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                assert intent != null;
                final Uri currentTone = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                savedMelody = currentTone;
                Ringtone ringtone = RingtoneManager.getRingtone(SetAlarmActivity.this, currentTone);
                binding.tvMelody.setText(ringtone.getTitle(SetAlarmActivity.this));
                binding.scRepeatMelody.setClickable(true);
            } else if (savedMelody == null) {
                savedMelody = Uri.parse("android.resource://com.example.alarm/raw/silk");
                binding.tvMelody.setText("Silk");
            }
        });


        binding.clMelody.setOnClickListener(view12 -> {
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            //intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select tone");
            melodyTitle.launch(intent);
        });


        binding.tvSave.setOnClickListener(view1 -> {
            if (calendar == null) {
                calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
            }
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            alarmEnabled = true;
            listAlarmIDs = MySharePref.getListAlarmId(this, "ALARM_IDs", "ALARM_IDs");

            if (listAlarmIDs == null) listAlarmIDs = new ArrayList<>();

            if (!listAlarmIDs.contains(ALARM_REQ_CODE)) {
                //ALARM_REQ_CODE = new Random().nextInt();
                listAlarmIDs.add(ALARM_REQ_CODE);
                System.out.println(ALARM_REQ_CODE);
                MySharePref.writeList(this, "ALARM_IDs", "ALARM_IDs", listAlarmIDs);
            }

            Intent iBroad = new Intent(SetAlarmActivity.this, AlarmReceiver.class);
            Intent intent = new Intent(SetAlarmActivity.this, MainActivity.class);

            String chosenTime;
            if (calendar.get(Calendar.MINUTE) <= 9) {
                chosenTime = calendar.get(Calendar.HOUR_OF_DAY) + ":0" + calendar.get(Calendar.MINUTE);
            } else {
                chosenTime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            }

            information = new AlarmInformation(ALARM_REQ_CODE, chosenTime, savedMelody.toString(), alarmEnabled, repeatSignal, monday,
                    tuesday, wednesday, thursday, friday, saturday, sunday);
            intent.putExtra("Time", information);
            Bundle bundle = new Bundle();
            bundle.putParcelable("AlarmInformationFromSetAlarmActivity", information);
            iBroad.putExtra("BundleAlarmInformationFromSetAlarmActivity", bundle);

            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), ALARM_REQ_CODE, iBroad, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (monday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 1, 2, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 1, iBroad, alarmManager);


            if (tuesday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 2, 3, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 2, iBroad, alarmManager);

            if (wednesday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 3, 4, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 3, iBroad, alarmManager);

            if (thursday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 4, 5, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 4, iBroad, alarmManager);

            if (friday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 5, 6, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 5, iBroad, alarmManager);

            if (saturday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 6, 7, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 6, iBroad, alarmManager);

            if (sunday) {
                setRepeatingAlarm(alarmManager, iBroad, ALARM_REQ_CODE + 7, 1, listAlarmIDs, pi);
            } else cancelRepeatingAlarm(listAlarmIDs, ALARM_REQ_CODE + 7, iBroad, alarmManager);

            if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), pi);
                Log.e("Point", "Calendar " + calendar.getTime());
            }

            Toast.makeText(this, "Alarm saved", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, intent);
            finish();
        });

        binding.scRepeatMelody.setChecked(repeatSignal);
        binding.scRepeatMelody.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                repeatSignal = true;
            } else {
                repeatSignal = false;
            }
        });

        repeatDays = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                assert intent != null;
                information = intent.getParcelableExtra("RepeatDaysFromRepeatActivity");
                monday = information.monday;
                tuesday = information.tuesday;
                wednesday = information.wednesday;
                thursday = information.thursday;
                friday = information.friday;
                saturday = information.saturday;
                sunday = information.sunday;
                temporaryChosenDays = new AlarmInformation(0, null, null, false, false, monday, tuesday,
                        wednesday, thursday, friday, saturday, sunday);
                setChosenDaysToTextView();
            }
        });

        binding.clRepeat.setOnClickListener(v -> {
            Intent intent = new Intent(SetAlarmActivity.this, ChooseRepeatDay.class);
            intent.putExtra("RepeatDaysFromSetAlarmActivity", previouslySettedAlarmInformation);
            intent.putExtra("TemporaryChosenDaysFromSetAlarmActivity", temporaryChosenDays);
            repeatDays.launch(intent);
        });

    }

    public void setChosenDaysToTextView() {
        if (monday) headerOfDays.add(DaysOfTheWeek.Monday);
        else headerOfDays.remove(DaysOfTheWeek.Monday);
        if (tuesday) headerOfDays.add(DaysOfTheWeek.Tuesday);
        else headerOfDays.remove(DaysOfTheWeek.Tuesday);
        if (wednesday) headerOfDays.add(DaysOfTheWeek.Wednesday);
        else headerOfDays.remove(DaysOfTheWeek.Wednesday);
        if (thursday) headerOfDays.add(DaysOfTheWeek.Thursday);
        else headerOfDays.remove(DaysOfTheWeek.Thursday);
        if (friday) headerOfDays.add(DaysOfTheWeek.Friday);
        else headerOfDays.remove(DaysOfTheWeek.Friday);
        if (saturday) headerOfDays.add(DaysOfTheWeek.Saturday);
        else headerOfDays.remove(DaysOfTheWeek.Saturday);
        if (sunday) headerOfDays.add(DaysOfTheWeek.Sunday);
        else headerOfDays.remove(DaysOfTheWeek.Sunday);
        List<DaysOfTheWeek> sortedDaysOfWeek = new ArrayList<>(headerOfDays);
        Collections.sort(sortedDaysOfWeek);
        StringBuilder result = new StringBuilder();
        if (sortedDaysOfWeek.size() > 1) {
            for (DaysOfTheWeek value : sortedDaysOfWeek) {
                result.append(value.toString().substring(0, 3)).append(" ");
            }
            binding.tvChosenDays.setText(result);
        } else
            binding.tvChosenDays.setText("Every " + sortedDaysOfWeek.toString().toLowerCase().replaceAll("[\\[\\]]", ""));
        if (monday && tuesday && wednesday && thursday && friday && saturday && sunday)
            binding.tvChosenDays.setText("Every day");
        if (!monday && !tuesday && !wednesday && !thursday && !friday && !saturday && !sunday) {
            binding.tvChosenDays.setText("Never >");
        }
    }

    public void setRepeatingAlarm(AlarmManager alarmManager, Intent intent, int code, int day, List<Integer> list, PendingIntent cancelDisposableAlarm) {
        alarmManager.cancel(cancelDisposableAlarm);
        if (!list.contains(code)) list.add(code);
        MySharePref.writeList(this, "ALARM_IDs", "ALARM_IDs", listAlarmIDs);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_WEEK, day);
        calendar1.set(Calendar.HOUR_OF_DAY, binding.tpTimeChoice.getHour());
        calendar1.set(Calendar.MINUTE, binding.tpTimeChoice.getMinute());
        calendar1.set(Calendar.SECOND, 0);
        if (calendar1.before(Calendar.getInstance())) calendar1.add(Calendar.WEEK_OF_YEAR, 1);
        PendingIntent piDays = PendingIntent.getBroadcast(getApplicationContext(), code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar1.getTimeInMillis(), null), piDays);
        Log.e("AlarmSetAlarmActivity", calendar1.getTime() + String.valueOf(day));
    }

    public void cancelRepeatingAlarm(List<Integer> list, int code, Intent intent, AlarmManager alarmManager) {
        PendingIntent piCancel = PendingIntent.getBroadcast(getApplicationContext(), code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(piCancel);
        list.remove(Integer.valueOf(code));
        MySharePref.writeList(this, "ALARM_IDs", "ALARM_IDs", listAlarmIDs);
    }
}