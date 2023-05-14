package com.example.alarm;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import com.example.alarm.databinding.ActivityMainBinding;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements Adapter.OnItemClickListener {
    ActivityMainBinding binding;
    ActivityResultLauncher<Intent> chosenAlarm;
    List<AlarmInformation> arrayList;
    Adapter adapter;
    AlarmInformation data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().setStatusBarColor(Color.parseColor("#000000"));
        getWindow().setNavigationBarColor(Color.parseColor("#000000"));


        arrayList = MySharePref.getList(this, "INFORMATION_ALARM", "INFORMATION_ALARM");

        if (arrayList == null) arrayList = new ArrayList<>();

        if (arrayList.size() == 0) binding.tvEmptyAlarm.setVisibility(View.VISIBLE);

        binding.rvAlarms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter((ArrayList<AlarmInformation>) arrayList, this);
        binding.rvAlarms.setAdapter(adapter);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.rvAlarms);
        chosenAlarm = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                assert intent != null;
                data = intent.getParcelableExtra("Time");
                boolean replace = false;
                if (arrayList.size() == 0) arrayList.add(data);
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).alarmId == (data.alarmId)) {
                        arrayList.set(i, data);
                        replace = true;
                    }
                }
                if (!replace) arrayList.add(data);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                if (arrayList.size() > 0) {
                    arrayList.sort((object1, object2) -> {
                        try {
                            return Objects.requireNonNull(dateFormat.parse(object1.alarmTime)).compareTo(dateFormat.parse(object2.alarmTime));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                MySharePref.writeList(this, "INFORMATION_ALARM", "INFORMATION_ALARM", arrayList);
                binding.tvEmptyAlarm.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });

        binding.ibAddButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, SetAlarmActivity.class);
            chosenAlarm.launch(intent);
        });
    }

    AlarmInformation deleteItem;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            deleteItem = arrayList.get(position);
            List<Integer> removeAlarmIDFromSharedPref = MySharePref.getListAlarmId(MainActivity.this, "ALARM_IDs", "ALARM_IDs");
            removeAlarmIDFromSharedPref.remove(Integer.valueOf(deleteItem.alarmId));
            MySharePref.writeList(MainActivity.this, "ALARM_IDs", "ALARM_IDs", removeAlarmIDFromSharedPref);
            Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, deleteItem.alarmId, intent, PendingIntent.FLAG_MUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(sender);

            if (deleteItem.monday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 1, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.tuesday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 2, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.wednesday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 3, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.thursday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 4, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.friday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 5, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.saturday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 6, removeAlarmIDFromSharedPref);
            }

            if (deleteItem.sunday) {
                deleteAlarm(intent, alarmManager, deleteItem.alarmId + 7, removeAlarmIDFromSharedPref);
            }

            arrayList.remove(position);
            if (position == 0 && arrayList.size() == 0)
                binding.tvEmptyAlarm.setVisibility(View.VISIBLE);
            MySharePref.writeList(MainActivity.this, "INFORMATION_ALARM", "INFORMATION_ALARM", arrayList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(MainActivity.this, R.color.white))
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 17)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onItemClick(AlarmInformation item) {
        Intent intent = new Intent(this, SetAlarmActivity.class);
        intent.putExtra("ExistingAlarmInformation", item);
        intent.putExtra("ChangeTitle", "Edit");
        chosenAlarm.launch(intent);

    }

    public void deleteAlarm(Intent intent, AlarmManager alarmManager, int code, List<Integer> list) {
        list.remove(Integer.valueOf(code));
        MySharePref.writeList(MainActivity.this, "ALARM_IDs", "ALARM_IDs", list);
        PendingIntent piDeleteAlarm = PendingIntent.getBroadcast(MainActivity.this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        alarmManager.cancel(piDeleteAlarm);
    }
}