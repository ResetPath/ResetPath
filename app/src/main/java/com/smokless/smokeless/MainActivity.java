package com.smokless.smokeless;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        updateFront();
    }

    private long getBestTimeBetweenTwoSmoke() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                long biggestTimeDifference = 0;
                long previousTimestamp = cursor.getLong(0);
                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(0);
                    long timeDifference = currentTimestamp - previousTimestamp;
                    if (timeDifference > biggestTimeDifference) {
                        biggestTimeDifference = timeDifference;
                    }
                    previousTimestamp = currentTimestamp;
                }
                return biggestTimeDifference / (60 * 1000);
            } else {
                return 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long getAverageTimeBetweenTwoSmoke() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                long totalTimeDifference = 0;
                long previousTimestamp = cursor.getLong(0);
                int numberOfTimeDifferences = 0;
                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(0);
                    long timeDifference = currentTimestamp - previousTimestamp;
                    totalTimeDifference += timeDifference;
                    numberOfTimeDifferences++;
                    previousTimestamp = currentTimestamp;
                }
                return totalTimeDifference / (numberOfTimeDifferences * 60 * 1000);
            } else {
                return 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String calculateTimeSinceLastSmoke(long lastTimestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastTimestamp;

        // Calculate the time difference in minutes
        long minutes = timeDifference / 60000;

        // Return the time difference in minutes as a string
        return String.valueOf(minutes) + " Minutes";
    }

    public void onButtonImSmokingClicked(View view) {
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Create a new ContentValues object to store the data to be inserted into the database
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TIMESTAMP, timestamp);

        // Insert the data into the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
        db.close();

        updateFront();
    }

    private void updateFront() {

        printAllSmokeTimes();
        long lastTimestamp = getLastTimestampInDb();
        EditText editTextCurrentTimeSinceLastSmoke = findViewById(R.id.editTextCurrentTimeSinceLastSmoke);
        editTextCurrentTimeSinceLastSmoke.setText(calculateTimeSinceLastSmoke(lastTimestamp));

        // Get the maximum duration between two entries of the database
        long bestTimeBetweenTwoSmoke = getBestTimeBetweenTwoSmoke();
        EditText editTextBestTimeSinceLastSmoke = findViewById(R.id.editTextBestTimeSinceLastSmoke);
        editTextBestTimeSinceLastSmoke.setText(String.valueOf(bestTimeBetweenTwoSmoke) + " Minutes");

        long averageTimeBetweenTwoSmoke = getAverageTimeBetweenTwoSmoke();
        EditText editTextAverageTimeBetweenTwoSmoke = findViewById(R.id.editTextAverageTimeBetweenTwoSmoke);
        editTextAverageTimeBetweenTwoSmoke.setText(String.valueOf(averageTimeBetweenTwoSmoke) + " Minutes");
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "smokeless.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_NAME = "smoking_sessions";
        private static final String COLUMN_TIMESTAMP = "timestamp";

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO: Implement database upgrade logic
        }
    }

    public long getLastTimestampInDb() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT MAX(" + DatabaseHelper.COLUMN_TIMESTAMP + ") FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        } else {
            return 0;
        }
    }

    private void printAllSmokeTimes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Log.d("SmokeTime", "Timestamp: " + cursor.getLong(0));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
