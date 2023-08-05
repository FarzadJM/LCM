package com.example.myapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private String id;
    private TextView textViewName;
    private TextView textViewPhone;
    private TextView textViewBirthdate;
    private TextView textViewHaircutCount;
    private TextView textViewLoyaltyNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.myapp.R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("id");

        textViewName = findViewById(R.id.textViewName);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewBirthdate = findViewById(R.id.textViewBirthdate);;
        textViewLoyaltyNumber = findViewById(R.id.textViewLoyaltyNumber);
        textViewHaircutCount = findViewById(R.id.textViewHaircutCount);

        reloadData();

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this item?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Delete the item from SQLite
                DatabaseHelper dbHelper = new DatabaseHelper(DetailActivity.this);
                dbHelper.deleteCustomer(id);
                dbHelper.close();

                // Finish the activity and go back to MainActivity
                finish();
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        Button buttonNewHaircut = findViewById(R.id.buttonNewHaircut);
        buttonNewHaircut.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setTitle("Confirm Increase");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Delete the item from SQLite
                DatabaseHelper dbHelper = new DatabaseHelper(DetailActivity.this);
                dbHelper.increaseHaircutCounterById(id);
                dbHelper.close();

                reloadData();
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void reloadData() {
        HashMap<String, String> data;
        try (DatabaseHelper dbHandler = new DatabaseHelper(this)) {
            data = dbHandler.getCustomer(id);
        }
        textViewName.setText(data.get("name"));
        textViewPhone.setText(data.get("phone"));
        textViewLoyaltyNumber.setText(data.get("loyalty_number"));
        textViewBirthdate.setText(data.get("birthdate"));
        textViewHaircutCount.setText(data.get("haircut_count"));

        Objects.requireNonNull(getSupportActionBar()).setTitle(Objects.requireNonNull(data.get("name")).toUpperCase());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}