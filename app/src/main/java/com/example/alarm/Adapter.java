package com.example.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarm.databinding.RecyclerviewRowBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Adapter extends RecyclerView.Adapter<Adapter.DataHolder> {
    public interface OnItemClickListener {
        void onItemClick(AlarmInformation item);
    }

    private List<AlarmInformation> arrayList;
    private OnItemClickListener listener;

    public enum DaysOfTheWeek {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    }


    public Adapter(ArrayList<AlarmInformation> arrayList, OnItemClickListener listener) {
        this.arrayList = arrayList;
        this.listener = listener;

    }

    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DataHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        holder.bind(arrayList.get(position), listener);
        setChosenDaysToTextView(holder, position);
        holder.binding.tvChosenTime.setText(arrayList.get(position).alarmTime);
        holder.binding.scAlarmOnOff.setChecked(arrayList.get(position).switchEnableDisableAlarm);
        if (!holder.binding.scAlarmOnOff.isChecked()) {
            cancelAlarm(holder.binding.scAlarmOnOff.getContext(), position);
            holder.binding.tvChosenTime.setTextColor(Color.parseColor("#8C8C8F"));
            holder.binding.tvChosenDaysAdapter.setTextColor(Color.parseColor("#8C8C8F"));
        }
        holder.binding.scAlarmOnOff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                arrayList.get(position).setSwitchEnableDisableAlarm(true);
                holder.binding.tvChosenTime.setTextColor(Color.parseColor("#FFFFFF"));
                holder.binding.tvChosenDaysAdapter.setTextColor(Color.parseColor("#FFFFFF"));
                MySharePref.writeList(compoundButton.getContext(), "INFORMATION_ALARM", "INFORMATION_ALARM", arrayList);
                startAlarm(compoundButton.getContext(), position);
            } else {
                cancelAlarm(compoundButton.getContext(), position);
                arrayList.get(position).setSwitchEnableDisableAlarm(false);
                holder.binding.tvChosenTime.setTextColor(Color.parseColor("#8C8C8F"));
                holder.binding.tvChosenDaysAdapter.setTextColor(Color.parseColor("#8C8C8F"));
                Log.e("ID From Adapter", arrayList.get(position).alarmId + "");
                MySharePref.writeList(compoundButton.getContext(), "INFORMATION_ALARM", "INFORMATION_ALARM", arrayList);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DataHolder extends RecyclerView.ViewHolder {
        private RecyclerviewRowBinding binding;

        public DataHolder(RecyclerviewRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final AlarmInformation item, final OnItemClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }
    }

    public void cancelAlarm(Context context, int position) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 1);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 2);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 3);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 4);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 5);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 6);
        cancelRepeatingAlarm(context, intent,alarmManager, arrayList.get(position).alarmId + 7);
    }


    public void startAlarm(Context context, int position) {
        String[] splitTime = arrayList.get(position).alarmTime.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        Log.e("Switch", "Calendar " + calendar.getTime());

        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmInformation alarmInformation = new AlarmInformation(arrayList.get(position).alarmId, arrayList.get(position).alarmTime
                , arrayList.get(position).alarmSound, true, arrayList.get(position).switchRepeatSignal, arrayList.get(position).monday,
                arrayList.get(position).tuesday, arrayList.get(position).wednesday, arrayList.get(position).thursday,
                arrayList.get(position).friday, arrayList.get(position).saturday, arrayList.get(position).sunday);
        Bundle bundle = new Bundle();
        bundle.putParcelable("AlarmInformationFromAdapter", alarmInformation);
        intent.putExtra("BundleAlarmInformationFromAdapter", bundle);
        PendingIntent pi = PendingIntent.getBroadcast(context, arrayList.get(position).alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), pi);

        if (arrayList.get(position).monday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 1,2, splitTime, pi);
        }

        if (arrayList.get(position).tuesday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 2,3, splitTime, pi);
        }

        if (arrayList.get(position).wednesday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 3,4, splitTime, pi);
        }

        if (arrayList.get(position).thursday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 4,5, splitTime, pi);
        }

        if (arrayList.get(position).friday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 5,6, splitTime, pi);
        }

        if (arrayList.get(position).saturday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 6,7, splitTime, pi);
        }

        if (arrayList.get(position).sunday) {
            setStartRepeatingAlarm(context, alarmManager, intent, arrayList.get(position).alarmId + 7,1, splitTime, pi);
        }
    }

    public void setChosenDaysToTextView(DataHolder holder, int position) {
        Set<DaysOfTheWeek> headerOfDays = new HashSet<>();
        if (arrayList.get(position).monday) headerOfDays.add(DaysOfTheWeek.Monday);
        else headerOfDays.remove(DaysOfTheWeek.Monday);
        if (arrayList.get(position).tuesday) headerOfDays.add(DaysOfTheWeek.Tuesday);
        else headerOfDays.remove(DaysOfTheWeek.Tuesday);
        if (arrayList.get(position).wednesday) headerOfDays.add(DaysOfTheWeek.Wednesday);
        else headerOfDays.remove(DaysOfTheWeek.Wednesday);
        if (arrayList.get(position).thursday) headerOfDays.add(DaysOfTheWeek.Thursday);
        else headerOfDays.remove(DaysOfTheWeek.Thursday);
        if (arrayList.get(position).friday) headerOfDays.add(DaysOfTheWeek.Friday);
        else headerOfDays.remove(DaysOfTheWeek.Friday);
        if (arrayList.get(position).saturday) headerOfDays.add(DaysOfTheWeek.Saturday);
        else headerOfDays.remove(DaysOfTheWeek.Saturday);
        if (arrayList.get(position).sunday) headerOfDays.add(DaysOfTheWeek.Sunday);
        else headerOfDays.remove(DaysOfTheWeek.Sunday);
        List<DaysOfTheWeek> sortedDays = new ArrayList<>(headerOfDays);
        Collections.sort(sortedDays);
        StringBuilder result = new StringBuilder();
        if (sortedDays.size() > 1) {
            for (DaysOfTheWeek value : sortedDays) {
                result.append(value.toString().substring(0, 3)).append(" ");
            }
            holder.binding.tvChosenDaysAdapter.setText(result);
        } else
            holder.binding.tvChosenDaysAdapter.setText("Every " + sortedDays.toString().toLowerCase().replaceAll("[\\[\\]]", ""));
        if (sortedDays.size() == 0) holder.binding.tvChosenDaysAdapter.setText("");
        if (arrayList.get(position).monday && arrayList.get(position).tuesday && arrayList.get(position).wednesday
                && arrayList.get(position).thursday && arrayList.get(position).friday && arrayList.get(position).saturday
                && arrayList.get(position).sunday)
            holder.binding.tvChosenDaysAdapter.setText("Every day");

    }

    public void setStartRepeatingAlarm(Context context, AlarmManager alarmManager, Intent intent, int code, int day, String[] time, PendingIntent cancelDisposableAlarm) {
        alarmManager.cancel(cancelDisposableAlarm);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, day);
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        calendar.set(Calendar.SECOND, 0);
        if (calendar.before(Calendar.getInstance())) calendar.add(Calendar.WEEK_OF_YEAR, 1);
        PendingIntent piStartRepeatAlarm = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), null), piStartRepeatAlarm);
        Log.e("AlarmAdapter", calendar.getTime().toString());
    }

    public void cancelRepeatingAlarm(Context context,Intent intent, AlarmManager alarmManager, int code){
        PendingIntent cancelAllAlarms = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(cancelAllAlarms);
    }
}


