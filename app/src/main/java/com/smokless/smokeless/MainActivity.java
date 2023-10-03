package com.smokless.smokeless;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper dbHelper;
    private Timer timer = new Timer();

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // Update the front of your app here
            updateFront();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        timer.schedule(timerTask, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the timer task
        timerTask.cancel();
    }

    private long getBestTimeBetweenTwoSmoke() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                long biggestTimeDifference = 0;
                long previousTimestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));
                long timeDifference;
                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));
                    timeDifference = currentTimestamp - previousTimestamp;
                    if (timeDifference > biggestTimeDifference) {
                        biggestTimeDifference = timeDifference;
                    }
                    previousTimestamp = currentTimestamp;
                }
                // Get the current time
                long currentTime = System.currentTimeMillis();

                // Get the timestamp of the last entry
                cursor.moveToLast();
                long lastTimestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));

                // Calculate the time difference between the last entry and the current time
                timeDifference = currentTime - lastTimestamp;

                // If the time difference is greater than the biggestTimeDifference, update the biggestTimeDifference
                if (timeDifference > biggestTimeDifference) {
                    biggestTimeDifference = timeDifference;
                }
                return biggestTimeDifference;
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
        long installationTimestamp = getInstallationTimestamp(); // You need to implement this method
        long currentTime = System.currentTimeMillis();
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
                if (numberOfTimeDifferences > 0) {
                    return totalTimeDifference / (numberOfTimeDifferences);
                } else {
                    return (currentTime - installationTimestamp);
                }
            } else {
                return 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private long getInstallationTimestamp() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return pi.firstInstallTime;
    }

    private long calculateTimeSinceLastSmoke(long lastTimestamp) {
        if (lastTimestamp == 0) {
            return 0;
        }
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - lastTimestamp;

        long timestamp = timeDifference;

        return timestamp;
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printAllSmokeTimes();

                long lastTimestamp = getLastTimestampInDb();
                long currentTimeSinceLastSmoke = calculateTimeSinceLastSmoke(lastTimestamp);
                long bestTimeBetweenTwoSmoke = getBestTimeBetweenTwoSmoke();
                long averageTimeBetweenTwoSmoke = getAverageTimeBetweenTwoSmoke();
                long medianTimeBetweenTwoSmoke = getMedianTimeBetweenTwoSmoke();

                EditText editTextBestTimeSinceLastSmoke = findViewById(R.id.editTextBestTimeSinceLastSmoke);
                EditText editTextAverageTimeBetweenTwoSmoke = findViewById(R.id.editTextAverageTimeBetweenTwoSmoke);
                EditText editTextMedianTimeBetweenTwoSmoke = findViewById(R.id.editTextMedianTimeBetweenTwoSmoke);
                EditText editTextCurrentTimeSinceLastSmoke = findViewById(R.id.editTextCurrentTimeSinceLastSmoke);

                if (currentTimeSinceLastSmoke >= bestTimeBetweenTwoSmoke) {
                    editTextBestTimeSinceLastSmoke.setTextColor(getResources().getColor(R.color.green));
                    editTextBestTimeSinceLastSmoke.setText("✅ Best:    " + formatTimeStamp(bestTimeBetweenTwoSmoke));
                } else {
                    editTextBestTimeSinceLastSmoke.setTextColor(getResources().getColor(R.color.orange));
                    editTextBestTimeSinceLastSmoke.setText("❌ Best:    " + formatTimeStamp(bestTimeBetweenTwoSmoke));
                }

                if (currentTimeSinceLastSmoke >= averageTimeBetweenTwoSmoke) {
                    editTextAverageTimeBetweenTwoSmoke.setTextColor(getResources().getColor(R.color.green));
                    editTextAverageTimeBetweenTwoSmoke.setText("✅ Average:    " + formatTimeStamp(averageTimeBetweenTwoSmoke));
                } else {
                    editTextAverageTimeBetweenTwoSmoke.setTextColor(getResources().getColor(R.color.orange));
                    editTextAverageTimeBetweenTwoSmoke.setText("❌ Average:    " + formatTimeStamp(averageTimeBetweenTwoSmoke));
                }

                if (currentTimeSinceLastSmoke >= medianTimeBetweenTwoSmoke) {
                    editTextMedianTimeBetweenTwoSmoke.setTextColor(getResources().getColor(R.color.green));
                    editTextMedianTimeBetweenTwoSmoke.setText("✅ Median:    " + formatTimeStamp(medianTimeBetweenTwoSmoke));
                } else {
                    editTextMedianTimeBetweenTwoSmoke.setTextColor(getResources().getColor(R.color.orange));
                    editTextMedianTimeBetweenTwoSmoke.setText("❌ Median:    " + formatTimeStamp(medianTimeBetweenTwoSmoke));
                }

                if (currentTimeSinceLastSmoke >= averageTimeBetweenTwoSmoke &&
                        currentTimeSinceLastSmoke >= bestTimeBetweenTwoSmoke &&
                        currentTimeSinceLastSmoke >= medianTimeBetweenTwoSmoke) {
                    editTextCurrentTimeSinceLastSmoke.setTextColor(getResources().getColor(R.color.green));
                    editTextCurrentTimeSinceLastSmoke.setText("✅ Score:    " + formatTimeStamp(currentTimeSinceLastSmoke));
                } else if (currentTimeSinceLastSmoke >= averageTimeBetweenTwoSmoke ||
                        currentTimeSinceLastSmoke >= medianTimeBetweenTwoSmoke ||
                        currentTimeSinceLastSmoke >= bestTimeBetweenTwoSmoke) {
                    editTextCurrentTimeSinceLastSmoke.setTextColor(getResources().getColor(R.color.orange));
                    editTextCurrentTimeSinceLastSmoke.setText("⌛️ Score:    " + formatTimeStamp(currentTimeSinceLastSmoke));
                } else {
                    editTextCurrentTimeSinceLastSmoke.setTextColor(getResources().getColor(R.color.red));
                    editTextCurrentTimeSinceLastSmoke.setText("⏳ Score:    " + formatTimeStamp(currentTimeSinceLastSmoke));
                }
            }
        });
    }

    private long getMedianTimeBetweenTwoSmoke() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
        long installationTimestamp = getInstallationTimestamp(); // You need to implement this method
        long currentTime = System.currentTimeMillis();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                long[] timeDifferences = new long[cursor.getCount() - 1];
                long previousTimestamp = cursor.getLong(0);
                int numberOfTimeDifferences = 0;
                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(0);
                    long timeDifference = currentTimestamp - previousTimestamp;
                    timeDifferences[numberOfTimeDifferences] = timeDifference;
                    numberOfTimeDifferences++;
                    previousTimestamp = currentTimestamp;
                }

                if (numberOfTimeDifferences > 0) {
                    // Sort the array of time differences
                    Arrays.sort(timeDifferences);

                    // Calculate the median
                    if (numberOfTimeDifferences % 2 == 0) {
                        // If there is an even number of elements, take the average of the middle two values
                        int middleIndex1 = numberOfTimeDifferences / 2 - 1;
                        int middleIndex2 = numberOfTimeDifferences / 2;
                        long median = (timeDifferences[middleIndex1] + timeDifferences[middleIndex2]) / 2;
                        return median;
                    } else {
                        // If there is an odd number of elements, take the middle value
                        int middleIndex = numberOfTimeDifferences / 2;
                        return timeDifferences[middleIndex];
                    }
                } else {
                    return (currentTime - installationTimestamp);
                }
            } else {
                return 0;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private String formatTimeStamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime dateTime = instant.atZone(ZoneOffset.UTC); // Use the desired time zone if not UTC

        Duration duration = Duration.between(Instant.EPOCH, instant);

        long years = duration.toDays() / 365;
        long days = duration.toDays() % 365;
        long months = (int) (days / 30.44); // Approximate number of days in a month
        days = days % 30;

        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;


        if (years > 0) {
            return String.format("%dY %dM %dD %dh %dm %ds", years, months, days, hours, minutes, seconds);
        } else if (months > 0) {
            return String.format("%dM %dD %dh %dm %ds", months, days, hours, minutes, seconds);
        } else if (days > 0) {
            return String.format("%dD %dh %dm %ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
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

            // Insert init timestamp into the database
            ContentValues values = new ContentValues();
            values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
            db.insert(TABLE_NAME, null, values);

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
