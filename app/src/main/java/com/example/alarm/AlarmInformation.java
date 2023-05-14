package com.example.alarm;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Objects;

public  class AlarmInformation implements Parcelable{
    public int alarmId;
    public String alarmTime;
    public String alarmSound;
    public boolean switchEnableDisableAlarm;
    public boolean switchRepeatSignal;
    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    boolean sunday;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmInformation that = (AlarmInformation) o;
        return alarmId == that.alarmId && switchEnableDisableAlarm == that.switchEnableDisableAlarm && switchRepeatSignal == that.switchRepeatSignal && monday == that.monday && tuesday == that.tuesday && wednesday == that.wednesday && thursday == that.thursday && friday == that.friday && saturday == that.saturday && sunday == that.sunday && Objects.equals(alarmTime, that.alarmTime) && Objects.equals(alarmSound, that.alarmSound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alarmId, alarmTime, alarmSound, switchEnableDisableAlarm, switchRepeatSignal, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
    }

    public void setSwitchEnableDisableAlarm(boolean switchEnableDisableAlarm) {
        this.switchEnableDisableAlarm = switchEnableDisableAlarm;
    }

    public void setSwitchRepeatSignal(boolean switchRepeatSignal){
        this.switchRepeatSignal = switchRepeatSignal;
    }

    public AlarmInformation(int alarmId, String alarmTime, String alarmSound, boolean switchEnableDisableAlarm, boolean switchRepeatSignal, boolean monday,
                            boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday){
        this.alarmId = alarmId;
        this.alarmTime = alarmTime;
        this.alarmSound = alarmSound;
        this.switchEnableDisableAlarm = switchEnableDisableAlarm;
        this.switchRepeatSignal = switchRepeatSignal;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;

    }

    protected AlarmInformation(Parcel in) {
        alarmId = in.readInt();
        alarmTime = in.readString();
        alarmSound = in.readString();
        switchEnableDisableAlarm = in.readBoolean();
        switchRepeatSignal = in.readBoolean();
        monday = in.readBoolean();
        tuesday = in.readBoolean();
        wednesday = in.readBoolean();
        thursday = in.readBoolean();
        friday = in.readBoolean();
        saturday = in.readBoolean();
        sunday = in.readBoolean();
    }

    public static final Creator<AlarmInformation> CREATOR = new Creator<AlarmInformation>() {
        @Override
        public AlarmInformation createFromParcel(Parcel in) {
            return new AlarmInformation(in);
        }

        @Override
        public AlarmInformation[] newArray(int size) {
            return new AlarmInformation[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        String result = "";
        result = alarmId + " " + alarmTime + " " + alarmSound + " " + switchEnableDisableAlarm + " " + switchRepeatSignal;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(alarmId);
        parcel.writeString(alarmTime);
        parcel.writeString(alarmSound);
        parcel.writeBoolean(switchEnableDisableAlarm);
        parcel.writeBoolean(switchRepeatSignal);
        parcel.writeBoolean(monday);
        parcel.writeBoolean(tuesday);
        parcel.writeBoolean(wednesday);
        parcel.writeBoolean(thursday);
        parcel.writeBoolean(friday);
        parcel.writeBoolean(saturday);
        parcel.writeBoolean(sunday);
    }
}
