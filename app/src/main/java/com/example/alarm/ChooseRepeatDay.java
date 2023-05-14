package com.example.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.alarm.databinding.ActivityChooseRepeatDayBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseRepeatDay extends AppCompatActivity {
    ActivityChooseRepeatDayBinding binding;
    AlarmInformation information;
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
        binding = ActivityChooseRepeatDayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().setStatusBarColor(Color.parseColor("#2a343c"));
        getWindow().setNavigationBarColor(Color.parseColor("#2a343c"));

        if (information == null)
            information = new AlarmInformation(0, null, null, false, false, false, false,
                    false, false, false, false, false);

        if (getIntent().getParcelableExtra("RepeatDaysFromSetAlarmActivity") != null) {
            information = getIntent().getParcelableExtra("RepeatDaysFromSetAlarmActivity");
            writeValuesOfChosenDays();
        } else {
            monday = false;
            tuesday = false;
            wednesday = false;
            thursday = false;
            friday = false;
            saturday = false;
            sunday = false;
        }

        if (getIntent().getParcelableExtra("TemporaryChosenDaysFromSetAlarmActivity") != null) {
            information = getIntent().getParcelableExtra("TemporaryChosenDaysFromSetAlarmActivity");
            writeValuesOfChosenDays();
        }

        binding.cbMonday.setClickable(false);
        binding.cbTuesday.setClickable(false);
        binding.cbWednesday.setClickable(false);
        binding.cbThursday.setClickable(false);
        binding.cbFriday.setClickable(false);
        binding.cbSaturday.setClickable(false);
        binding.cbSunday.setClickable(false);

        binding.tvCancelDayChoose.setOnClickListener(v -> finish());
        @SuppressLint("NonConstantResourceId") View.OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.cl_monday:
                    monday = binding.cbMonday.isChecked();
                    monday = !monday;
                    binding.cbMonday.setChecked(monday);
                    break;
                case R.id.cl_tuesday:
                    tuesday = binding.cbTuesday.isChecked();
                    tuesday = !tuesday;
                    binding.cbTuesday.setChecked(tuesday);
                    break;
                case R.id.cl_wednesday:
                    wednesday = binding.cbWednesday.isChecked();
                    wednesday = !wednesday;
                    binding.cbWednesday.setChecked(wednesday);
                    break;
                case R.id.cl_thursday:
                    thursday = binding.cbThursday.isChecked();
                    thursday = !thursday;
                    binding.cbThursday.setChecked(thursday);
                    break;
                case R.id.cl_friday:
                    friday = binding.cbFriday.isChecked();
                    friday = !friday;
                    binding.cbFriday.setChecked(friday);
                    break;
                case R.id.cl_saturday:
                    saturday = binding.cbSaturday.isChecked();
                    saturday = !saturday;
                    binding.cbSaturday.setChecked(saturday);
                    break;
                case R.id.cl_sunday:
                    sunday = binding.cbSunday.isChecked();
                    sunday = !sunday;
                    binding.cbSunday.setChecked(sunday);
                    break;
                default:
                    break;
            }
        };
        binding.clMonday.setOnClickListener(onClickListener);
        binding.clTuesday.setOnClickListener(onClickListener);
        binding.clWednesday.setOnClickListener(onClickListener);
        binding.clThursday.setOnClickListener(onClickListener);
        binding.clFriday.setOnClickListener(onClickListener);
        binding.clSaturday.setOnClickListener(onClickListener);
        binding.clSunday.setOnClickListener(onClickListener);

        binding.tvSaveDayChoose.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseRepeatDay.this, SetAlarmActivity.class);
            information.monday = monday;
            information.tuesday = tuesday;
            information.wednesday = wednesday;
            information.thursday = thursday;
            information.friday = friday;
            information.saturday = saturday;
            information.sunday = sunday;
            intent.putExtra("RepeatDaysFromRepeatActivity", information);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
    public void writeValuesOfChosenDays(){
        monday = information.monday;
        tuesday = information.tuesday;
        wednesday = information.wednesday;
        thursday = information.thursday;
        friday = information.friday;
        saturday = information.saturday;
        sunday = information.sunday;
        binding.cbMonday.setChecked(monday);
        binding.cbTuesday.setChecked(tuesday);
        binding.cbWednesday.setChecked(wednesday);
        binding.cbThursday.setChecked(thursday);
        binding.cbFriday.setChecked(friday);
        binding.cbSaturday.setChecked(saturday);
        binding.cbSunday.setChecked(sunday);
    }
}
