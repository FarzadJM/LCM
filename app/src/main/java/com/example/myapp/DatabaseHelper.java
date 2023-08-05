package com.example.myapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "myapp.db";
    private static final String TABLE_CUSTOMER_NAME = "customers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_BIRTHDATE = "birthdate";
    private static final String COLUMN_LOYALTY_NUMBER = "loyalty_number";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_HAIRCUT_COUNT = "haircut_count";
    private static final String TABLE_MESSAGE_NAME = "messages";
    private static final String COLUMN_DATE_SENT = "date_sent";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CUSTOMER_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_BIRTHDATE + " TEXT, " +
                COLUMN_LOYALTY_NUMBER + " TEXT, " +
                COLUMN_HAIRCUT_COUNT + " INTEGER)";
        db.execSQL(query);

        String createTable = "CREATE TABLE " + TABLE_MESSAGE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_DATE_SENT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE_NAME);
        onCreate(db);
    }

    public void addCustomer(String name, String birthdate, String phone, String loyaltyNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_BIRTHDATE, birthdate);
        values.put(COLUMN_LOYALTY_NUMBER, loyaltyNumber);
        values.put(COLUMN_HAIRCUT_COUNT, 0);
        db.insert(TABLE_CUSTOMER_NAME, null, values);
        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getAllCustomers() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CUSTOMER_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            HashMap<String, String> data = new HashMap<>();
            data.put("id", cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            data.put("name", cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            data.put("birthdate", cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDATE)));
            data.put("phone", cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            data.put("loyalty_number", cursor.getString(cursor.getColumnIndex(COLUMN_LOYALTY_NUMBER)));
            data.put("haircut_count", cursor.getString(cursor.getColumnIndex(COLUMN_HAIRCUT_COUNT)));
            dataList.add(data);
        }
        cursor.close();
        db.close();
        return dataList;
    }

    @SuppressLint("Range")
    public HashMap<String, String> getCustomer(String id) {
        HashMap<String, String> data = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CUSTOMER_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        if (cursor.moveToFirst()) {
            data.put("id", cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            data.put("name", cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            data.put("birthdate", cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDATE)));
            data.put("phone", cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            data.put("loyalty_number", cursor.getString(cursor.getColumnIndex(COLUMN_LOYALTY_NUMBER)));
            data.put("haircut_count", cursor.getString(cursor.getColumnIndex(COLUMN_HAIRCUT_COUNT)));
        }
        cursor.close();
        db.close();
        return data;
    }

    public void deleteCustomer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CUSTOMER_NAME, COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }

    public void increaseHaircutCounterById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Update the age of the customer by one
        String query = "UPDATE " + TABLE_CUSTOMER_NAME + " SET " + COLUMN_HAIRCUT_COUNT + " = " + COLUMN_HAIRCUT_COUNT + " + 1 WHERE " + COLUMN_ID + " = " + id;
        db.execSQL(query);

        db.close();
    }

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getTodayBirthdays() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CUSTOMER_NAME + " WHERE strftime('%Y', " + COLUMN_BIRTHDATE + ") <> ? AND  strftime('%m', " + COLUMN_BIRTHDATE + ") = ? AND strftime('%d', " + COLUMN_BIRTHDATE + ") = ?";
        @SuppressLint("DefaultLocale") Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(year), String.format("%02d", month), String.format("%02d", day)});
        while (cursor.moveToNext()) {
            HashMap<String, String> data = new HashMap<>();
            data.put("id", cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            data.put("name", cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            data.put("birthdate", cursor.getString(cursor.getColumnIndex(COLUMN_BIRTHDATE)));
            data.put("phone", cursor.getString(cursor.getColumnIndex(COLUMN_PHONE)));
            data.put("loyalty_number", cursor.getString(cursor.getColumnIndex(COLUMN_LOYALTY_NUMBER)));
            data.put("haircut_count", cursor.getString(cursor.getColumnIndex(COLUMN_HAIRCUT_COUNT)));
            dataList.add(data);
        }
        cursor.close();
        db.close();
        return dataList;
    }

    public boolean isMessageSentToday(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        // Check if a message was sent today
        String query = "SELECT * FROM " + TABLE_MESSAGE_NAME + " WHERE " + COLUMN_PHONE + " = ? AND " + COLUMN_DATE_SENT + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phoneNumber, currentDate});
        boolean isSent = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isSent;
    }

    public void saveMessageSentToday(String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        String currentDate = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        // Save the current date as a message sent today
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE_SENT, currentDate);
        values.put(COLUMN_PHONE, phone);
        db.insert(TABLE_MESSAGE_NAME, null, values);

        db.close();
    }

}