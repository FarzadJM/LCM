package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private CustomListAdapter adapter;
    private ArrayList<HashMap<String, String>> dataList;
    private DatabaseHelper dbHandler;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        dbHandler = new DatabaseHelper(this);
        dataList = dbHandler.getAllCustomers();
        adapter = new CustomListAdapter(this, dataList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id", dataList.get(position).get("id"));
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Add Customer");
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_input, null);
            builder.setView(view);
            final EditText editTextName = view.findViewById(R.id.editTextName);
            final EditText editTextPhone = view.findViewById(R.id.editTextPhone);
            final EditText editTextLoyaltyNumber = view.findViewById(R.id.editTextLoyaltyNumber);
            final DatePicker datePicker = view.findViewById(R.id.datePicker);
            builder.setPositiveButton("Add", null);
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(view1 -> {
                    String name = editTextName.getText().toString();

                    // Validate the name field
                    if (name.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String phone = editTextPhone.getText().toString();

                    // Validate the phone field
                    if (phone.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter a phone", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String loyaltyNumber = editTextLoyaltyNumber.getText().toString();

                    // Validate the phone field
                    if (loyaltyNumber.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter loyalty number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get the birthdate from the DatePicker
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth() + 1; // Month is zero-based, so add 1
                    int day = datePicker.getDayOfMonth();
                    @SuppressLint("DefaultLocale") String birthdate = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                    // Save the data to SQLite

                    dbHandler.addCustomer(name, birthdate, phone, loyaltyNumber);
                    dataList.clear();
                    dataList.addAll(dbHandler.getAllCustomers());
                    adapter.notifyDataSetChanged();

                    String query = searchView.getQuery().toString();
                    adapter.getFilter().filter(query);

                    dialog.dismiss();
                });
            });
            dialog.show();
        });

        Intent serviceIntent = new Intent(this, MessageSenderService.class);
        ContextCompat.startForegroundService(this, serviceIntent);

        AlarmManagerHelper.scheduleAlarm(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataList.clear();
        dataList.addAll(dbHandler.getAllCustomers());
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }
}