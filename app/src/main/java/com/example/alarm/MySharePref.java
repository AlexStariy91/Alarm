package com.example.alarm;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class MySharePref {
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    public static <T> void writeList(Context context, String key,String nameStore, List<T> list) {
        pref = context.getSharedPreferences(nameStore, Context.MODE_PRIVATE);
        editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(key, json);
    }

    public static void set(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static List<AlarmInformation> getList(Context context, String nameStore, String key) {
        pref = context.getSharedPreferences(nameStore, Context.MODE_PRIVATE);
        String object = pref.getString(key, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<AlarmInformation>>() {
        }.getType();
        return gson.fromJson(object, type);

    }

    public static List<Integer> getListAlarmId(Context context, String nameStore, String key) {
        pref = context.getSharedPreferences(nameStore, Context.MODE_PRIVATE);
        String object = pref.getString(key, null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(object, type);

    }
}
