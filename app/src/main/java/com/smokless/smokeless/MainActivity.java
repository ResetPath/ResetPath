package com.smokless.smokeless;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_STRICT_MODE = "strictMode";
    private static final String PREF_NAME = "SmokeLessPrefs";
    private SQLiteOpenHelper dbHelper;
    private Timer timer = new Timer();
    private long lastTimestamp;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");


    Button button;
    TextView textViewStatusBestScoreAll;
    TextView textViewStatusAverageScoreAll;
    TextView textViewStatusMedianScoreAll;
    TextView textViewStatusBestScoreYear;
    TextView textViewStatusAverageScoreYear;
    TextView textViewStatusMedianScoreYear;
    TextView textViewStatusBestScoreMonth;
    TextView textViewStatusAverageScoreMonth;
    TextView textViewStatusMedianScoreMonth;
    TextView textViewStatusBestScoreWeek;
    TextView textViewStatusAverageScoreWeek;
    TextView textViewStatusMedianScoreWeek;
    TextView textViewStatusLatestScore;

    TextView textViewStatusCurrentGoal;
    TextView textViewStatusCurrentScore;


    TextView textViewLabelBestScoreAll;
    TextView textViewLabelAverageScoreAll;
    TextView textViewLabelMedianScoreAll;
    TextView textViewLabelBestScoreYear;
    TextView textViewLabelAverageScoreYear;
    TextView textViewLabelMedianScoreYear;
    TextView textViewLabelBestScoreMonth;
    TextView textViewLabelAverageScoreMonth;
    TextView textViewLabelMedianScoreMonth;
    TextView textViewLabelBestScoreWeek;
    TextView textViewLabelAverageScoreWeek;
    TextView textViewLabelMedianScoreWeek;
    TextView textViewLabelLatestScore;


    TextView textViewValueBestScoreAll;
    TextView textViewValueAverageScoreAll;
    TextView textViewValueMedianScoreAll;
    TextView textViewValueBestScoreYear;
    TextView textViewValueAverageScoreYear;
    TextView textViewValueMedianScoreYear;
    TextView textViewValueBestScoreMonth;
    TextView textViewValueAverageScoreMonth;
    TextView textViewValueMedianScoreMonth;
    TextView textViewValueBestScoreWeek;
    TextView textViewValueAverageScoreWeek;
    TextView textViewValueMedianScoreWeek;

    TextView textViewValueLatestScore;

    TextView textViewValueCurrentGoal;
    TextView textViewValueCurrentScore;


    TextView textViewPercentBestScoreAll;
    TextView textViewPercentAverageScoreAll;
    TextView textViewPercentMedianScoreAll;
    TextView textViewPercentBestScoreYear;
    TextView textViewPercentAverageScoreYear;
    TextView textViewPercentMedianScoreYear;
    TextView textViewPercentBestScoreMonth;
    TextView textViewPercentAverageScoreMonth;
    TextView textViewPercentMedianScoreMonth;
    TextView textViewPercentBestScoreWeek;
    TextView textViewPercentAverageScoreWeek;
    TextView textViewPercentMedianScoreWeek;

    TextView textViewPercentLatestScore;

    TextView textViewPercentCurrentScore;
    TextView textViewPercentCurrentGoal;

    TextView textViewLabelCurrentGoal;
    TextView textViewLabelCurrentScore;


    double percentBestScoreAll;
    double percentAverageScoreAll;
    double percentMedianScoreAll;
    double percentBestScoreYear;
    double percentAverageScoreYear;
    double percentMedianScoreYear;
    double percentBestScoreMonth;
    double percentAverageScoreMonth;
    double percentMedianScoreMonth;
    double percentBestScoreWeek;
    double percentAverageScoreWeek;
    double percentMedianScoreWeek;
    double percentLatestScore;
    double percentCurrentGoal;
    double percentCurrentScore;

    double latestScore;



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
        initUIBinders();
        lastTimestamp = getLastTimestampInDb();
        latestScore = calculateLatestScore();
    }

    private void initUIBinders() {
        textViewStatusBestScoreAll = findViewById(R.id.statusBestScoreAll);
        textViewStatusAverageScoreAll = findViewById(R.id.statusAverageScoreAll);
        textViewStatusMedianScoreAll = findViewById(R.id.statusMedianScoreAll);
        textViewStatusBestScoreYear = findViewById(R.id.statusBestScoreYear);
        textViewStatusAverageScoreYear = findViewById(R.id.statusAverageScoreYear);
        textViewStatusMedianScoreYear = findViewById(R.id.statusMedianScoreYear);
        textViewStatusBestScoreMonth = findViewById(R.id.statusBestScoreMonth);
        textViewStatusAverageScoreMonth = findViewById(R.id.statusAverageScoreMonth);
        textViewStatusMedianScoreMonth = findViewById(R.id.statusMedianScoreMonth);
        textViewStatusBestScoreWeek = findViewById(R.id.statusBestScoreWeek);
        textViewStatusAverageScoreWeek = findViewById(R.id.statusAverageScoreWeek);
        textViewStatusMedianScoreWeek = findViewById(R.id.statusMedianScoreWeek);
        textViewStatusLatestScore = findViewById(R.id.statusLatestScore);
        textViewStatusCurrentGoal = findViewById(R.id.statusCurrentGoal);
        textViewStatusCurrentScore = findViewById(R.id.statusCurrentScore);

        textViewLabelBestScoreAll = findViewById(R.id.labelBestScoreAll);
        textViewLabelAverageScoreAll = findViewById(R.id.labelAverageScoreAll);
        textViewLabelMedianScoreAll = findViewById(R.id.labelMedianScoreAll);
        textViewLabelBestScoreYear = findViewById(R.id.labelBestScoreYear);
        textViewLabelAverageScoreYear = findViewById(R.id.labelAverageScoreYear);
        textViewLabelMedianScoreYear = findViewById(R.id.labelMedianScoreYear);
        textViewLabelBestScoreMonth = findViewById(R.id.labelBestScoreMonth);
        textViewLabelAverageScoreMonth = findViewById(R.id.labelAverageScoreMonth);
        textViewLabelMedianScoreMonth = findViewById(R.id.labelMedianScoreMonth);
        textViewLabelBestScoreWeek = findViewById(R.id.labelBestScoreWeek);
        textViewLabelAverageScoreWeek = findViewById(R.id.labelAverageScoreWeek);
        textViewLabelMedianScoreWeek = findViewById(R.id.labelMedianScoreWeek);
        textViewLabelLatestScore = findViewById(R.id.labelLatestScore);
        textViewLabelCurrentGoal = findViewById(R.id.labelCurrentGoal);
        textViewLabelCurrentScore = findViewById(R.id.labelCurrentScore);

        textViewValueBestScoreAll = findViewById(R.id.valueBestScoreAll);
        textViewValueAverageScoreAll = findViewById(R.id.valueAverageScoreAll);
        textViewValueMedianScoreAll = findViewById(R.id.valueMedianScoreAll);
        textViewValueBestScoreYear = findViewById(R.id.valueBestScoreYear);
        textViewValueAverageScoreYear = findViewById(R.id.valueAverageScoreYear);
        textViewValueMedianScoreYear = findViewById(R.id.valueMedianScoreYear);
        textViewValueBestScoreMonth = findViewById(R.id.valueBestScoreMonth);
        textViewValueAverageScoreMonth = findViewById(R.id.valueAverageScoreMonth);
        textViewValueMedianScoreMonth = findViewById(R.id.valueMedianScoreMonth);
        textViewValueBestScoreWeek = findViewById(R.id.valueBestScoreWeek);
        textViewValueAverageScoreWeek = findViewById(R.id.valueAverageScoreWeek);
        textViewValueMedianScoreWeek = findViewById(R.id.valueMedianScoreWeek);
        textViewValueLatestScore = findViewById(R.id.valueLatestScore);
        textViewValueCurrentGoal = findViewById(R.id.valueCurrentGoal);
        textViewValueCurrentScore = findViewById(R.id.valueCurrentScore);

        textViewPercentBestScoreAll = findViewById(R.id.percentBestScoreAll);
        textViewPercentAverageScoreAll = findViewById(R.id.percentAverageScoreAll);
        textViewPercentMedianScoreAll = findViewById(R.id.percentMedianScoreAll);
        textViewPercentBestScoreYear = findViewById(R.id.percentBestScoreYear);
        textViewPercentAverageScoreYear = findViewById(R.id.percentAverageScoreYear);
        textViewPercentMedianScoreYear = findViewById(R.id.percentMedianScoreYear);
        textViewPercentBestScoreMonth = findViewById(R.id.percentBestScoreMonth);
        textViewPercentAverageScoreMonth = findViewById(R.id.percentAverageScoreMonth);
        textViewPercentMedianScoreMonth = findViewById(R.id.percentMedianScoreMonth);
        textViewPercentBestScoreWeek = findViewById(R.id.percentBestScoreWeek);
        textViewPercentAverageScoreWeek = findViewById(R.id.percentAverageScoreWeek);
        textViewPercentMedianScoreWeek = findViewById(R.id.percentMedianScoreWeek);
        textViewPercentLatestScore = findViewById(R.id.percentLatestScore);
        textViewPercentCurrentGoal = findViewById(R.id.percentCurrentGoal);
        textViewPercentCurrentScore = findViewById(R.id.percentCurrentScore);
        button = findViewById(R.id.buttonImSmoking);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the timer task
        timerTask.cancel();
    }

    private long getBestTime(String scope) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;
        Cursor cursor = null;

        long timeUnitMillis = 0;

        // Determine the time unit based on the scope parameter
        if ("year".equals(scope)) {
            timeUnitMillis = TimeUnit.DAYS.toMillis(365); // Approximate days in a year
        } else if ("month".equals(scope)) {
            timeUnitMillis = TimeUnit.DAYS.toMillis(30); // Approximate days in a month
        } else if ("week".equals(scope)) {
            timeUnitMillis = TimeUnit.DAYS.toMillis(7); // 7 days in a week
        }

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

                // Calculate the time difference between the last entry and the current time
                timeDifference = System.currentTimeMillis() - previousTimestamp;

                // If the time difference is greater than the biggestTimeDifference, update it
                if (timeDifference >
                        biggestTimeDifference) {
                    biggestTimeDifference = timeDifference;
                }

                // Ensure that the biggestTimeDifference is within the specified scope
                if (timeUnitMillis > 0 && biggestTimeDifference > timeUnitMillis) {
                    biggestTimeDifference = timeUnitMillis;
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

    private long calculateLatestScore() {
        if (lastTimestamp == 0) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT " + DatabaseHelper.COLUMN_TIMESTAMP +
                " FROM " + DatabaseHelper.TABLE_NAME +
                " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP + " DESC LIMIT 2";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                long currentTimestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));

                if (cursor.moveToNext()) {
                    long previousTimestamp = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP));
                    return currentTimestamp - previousTimestamp;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0; // Return 0
    }

    private long getAverageTime(String scope) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query;

        long installationTimestamp = getInstallationTimestamp();
        long currentTime = System.currentTimeMillis();
        Cursor cursor = null;

        try {
            switch (scope) {
                case "year":
                    // Subtract one year from the current time
                    currentTime -= TimeUnit.DAYS.toMillis(365);
                    break;
                case "month":
                    // Subtract one month from the current time
                    currentTime -= TimeUnit.DAYS.toMillis(30);
                    break;
                case "week":
                    // Subtract one week from the current time
                    currentTime -= TimeUnit.DAYS.toMillis(7);
                    break;
                default:
                    currentTime = 0;
                    // Handle invalid scope values here, if necessary
                    break;
            }

            // Modify the query based on the selected scope and time range
            query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " +
                    DatabaseHelper.COLUMN_TIMESTAMP + " >= " + currentTime +
                    " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;

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
                    totalTimeDifference += System.currentTimeMillis();
                    totalTimeDifference -= previousTimestamp;
                    numberOfTimeDifferences++;
                    return totalTimeDifference / numberOfTimeDifferences;
                } else {
                    return System.currentTimeMillis() - installationTimestamp;
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

    private double getStandardDeviation(String scope) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query;

        long installationTimestamp = getInstallationTimestamp();
        long currentTime = System.currentTimeMillis();
        Cursor cursor = null;

        try {
            switch (scope) {
                // ... (switch cases for year, month, week)

                default:
                    currentTime = 0;
                    // Handle invalid scope values here, if necessary
                    break;
            }

            // Modify the query based on the selected scope and time range
            query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " +
                    DatabaseHelper.COLUMN_TIMESTAMP + " >= " + currentTime +
                    " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP;

            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                long previousTimestamp = cursor.getLong(0);
                int numberOfTimeDifferences = 0;
                List<Long> timeDifferences = new ArrayList<>();

                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(0);
                    long timeDifference = currentTimestamp - previousTimestamp;
                    timeDifferences.add(timeDifference);
                    numberOfTimeDifferences++;
                    previousTimestamp = currentTimestamp;
                }

                if (numberOfTimeDifferences > 0) {
                    // Remove anomalies (e.g., values beyond a certain threshold)
                    double mean = calculateMean(timeDifferences);
                    double stdDev = calculateStandardDeviation(timeDifferences, mean);

                    // Define a threshold, for example, anomalies beyond 3 standard deviations
                    double threshold = 3 * stdDev;

                    // Remove anomalies by filtering based on the threshold
                    List<Long> filteredTimeDifferences = timeDifferences.stream()
                            .filter(diff -> Math.abs(diff - mean) <= threshold)
                            .collect(Collectors.toList());

                    // Recalculate standard deviation using filtered data
                    double newMean = calculateMean(filteredTimeDifferences);
                    double newStdDev = calculateStandardDeviation(filteredTimeDifferences, newMean);

                    return newStdDev;
                } else {
                    return 0; // or handle as per your requirement
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

    // Helper functions for calculating mean and standard deviation
    private double calculateMean(List<Long> data) {
        return data.stream().mapToLong(Long::valueOf).average().orElse(0);
    }

    private double calculateStandardDeviation(List<Long> data, double mean) {
        double variance = data.stream()
                .mapToDouble(diff -> Math.pow(diff - mean, 2))
                .average()
                .orElse(0);
        return Math.sqrt(variance);
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

        lastTimestamp = getLastTimestampInDb();
        latestScore = calculateLatestScore();
        updateFront();
    }

    private void updateFront() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printAllSmokeTimes();
                updateScores("All");
                // updateScores("Year")
                // updateScores("Month")
                // updateScores("Week")

                // updateCurrentScore()
                double currentScore = calculateTimeSinceLastSmoke(lastTimestamp);
                percentCurrentScore = (((
                                percentBestScoreAll +
                                percentAverageScoreAll +
                                percentMedianScoreAll +
                                        percentBestScoreYear +
                                        percentAverageScoreYear +
                                        percentMedianScoreYear +
                                        percentBestScoreMonth +
                                        percentAverageScoreMonth +
                                        percentMedianScoreMonth +
                                        percentBestScoreWeek +
                                        percentAverageScoreWeek +
                                        percentMedianScoreWeek
                ) / 12) + percentLatestScore + percentCurrentGoal)/3;
                formatRow(currentScore, textViewStatusCurrentScore, textViewLabelCurrentScore, textViewValueCurrentScore, textViewPercentCurrentScore, currentScore, percentCurrentScore);
                formatRow(currentScore, textViewStatusLatestScore, textViewLabelLatestScore, textViewValueLatestScore, textViewPercentLatestScore, latestScore, percentLatestScore);
                double percent = percentCurrentScore;
                if (percent < 20) {
                    button.setBackgroundColor(getResources().getColor(R.color.red));
                } else if (percent >= 20 && percent < 40) {
                    button.setBackgroundColor(getResources().getColor(R.color.orange));
                } else if (percent >= 40 && percent < 60) {
                    button.setBackgroundColor(getResources().getColor(R.color.yellow));
                } else if (percent >= 60 && percent < 80) {
                    button.setBackgroundColor(getResources().getColor(R.color.white));
                } else if (percent >= 80 && percent < 100) {
                    button.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (percent >= 100.0) {
                    button.setBackgroundColor(getResources().getColor(R.color.green));
                    button.setText("Why Would you try again ?");
                }
            }
        });
    }

    private void updateScores(String scope) {
        double currentScore = calculateTimeSinceLastSmoke(lastTimestamp);
        double averageScoreAll = getAverageTime("all");
        double medianScoreAll = getMedianTime("all");
        double averageScoreYear = getAverageTime("year");
        double medianScoreYear = getMedianTime("year");
        double averageScoreMonth = getAverageTime("month");
        double medianScoreMonth = getMedianTime("month");
        double averageScoreWeek = getAverageTime("week");
        double medianScoreWeek = getMedianTime("week");
        double bestScoreAll = getBestTime("all");
        double bestScoreYear = getBestTime("year");
        double bestScoreMonth = getBestTime("month");
        double bestScoreWeek = getBestTime("week");
        double standardDeviation = getStandardDeviation("all");
        SharedPreferences sharedPreferences = getSharedPreferences("SmokelessPrefs", MODE_PRIVATE);
        int difficultyLevel = sharedPreferences.getInt("difficultyLevel", 0);
        double currentGoal = averageScoreAll + standardDeviation * difficultyLevel;

        percentBestScoreAll = (currentScore / bestScoreAll) * 100;
        percentAverageScoreAll = (currentScore / averageScoreAll) * 100;
        percentMedianScoreAll = (currentScore / medianScoreAll) * 100;
        percentBestScoreYear = (currentScore / bestScoreYear) * 100;
        percentAverageScoreYear = (currentScore / averageScoreYear) * 100;
        percentMedianScoreYear = (currentScore / medianScoreYear) * 100;
        percentBestScoreMonth = (currentScore / bestScoreMonth) * 100;
        percentAverageScoreMonth = (currentScore / averageScoreMonth) * 100;
        percentMedianScoreMonth = (currentScore / medianScoreMonth) * 100;
        percentBestScoreWeek = (currentScore / bestScoreWeek) * 100;
        percentAverageScoreWeek = (currentScore / averageScoreWeek) * 100;
        percentMedianScoreWeek = (currentScore / medianScoreWeek) * 100;
        percentLatestScore = (currentScore / latestScore) * 100;
        percentCurrentGoal = (currentScore / currentGoal) * 100;

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isStrictModeEnabled = sharedPreferences.getBoolean(KEY_STRICT_MODE, false);
        if (isStrictModeEnabled) {
            if (percentBestScoreAll > 100) {
                percentBestScoreAll = 100;
            }
            if (percentAverageScoreAll > 100) {
                percentAverageScoreAll = 100;
            }
            if (percentMedianScoreAll > 100) {
                percentMedianScoreAll = 100;
            } 
            if (percentBestScoreYear > 100) {
                percentBestScoreYear = 100;
            }
            if (percentAverageScoreYear > 100) {
                percentAverageScoreYear = 100;
            }
            if (percentMedianScoreYear > 100) {
                percentMedianScoreYear = 100;
            } 
            if (percentBestScoreMonth > 100) {
                percentBestScoreMonth = 100;
            }
            if (percentAverageScoreMonth > 100) {
                percentAverageScoreMonth = 100;
            }
            if (percentMedianScoreMonth > 100) {
                percentMedianScoreMonth = 100;
            } 
            if (percentBestScoreWeek > 100) {
                percentBestScoreWeek = 100;
            }
            if (percentAverageScoreWeek > 100) {
                percentAverageScoreWeek = 100;
            }
            if (percentMedianScoreWeek > 100) {
                percentMedianScoreWeek = 100;
            }
            if (percentLatestScore > 100) {
                percentLatestScore = 100;
            }

            if (percentCurrentGoal > 100) {
                percentCurrentGoal = 100;
            }
        }

        formatRow(currentScore, textViewStatusBestScoreAll, textViewLabelBestScoreAll, textViewValueBestScoreAll, textViewPercentBestScoreAll, bestScoreAll, percentBestScoreAll);
        formatRow(currentScore, textViewStatusAverageScoreAll, textViewLabelAverageScoreAll, textViewValueAverageScoreAll, textViewPercentAverageScoreAll, averageScoreAll, percentAverageScoreAll);
        formatRow(currentScore, textViewStatusMedianScoreAll, textViewLabelMedianScoreAll, textViewValueMedianScoreAll, textViewPercentMedianScoreAll, medianScoreAll, percentMedianScoreAll);

        formatRow(currentScore, textViewStatusBestScoreYear, textViewLabelBestScoreYear, textViewValueBestScoreYear, textViewPercentBestScoreYear, bestScoreYear, percentBestScoreYear);
        formatRow(currentScore, textViewStatusAverageScoreYear, textViewLabelAverageScoreYear, textViewValueAverageScoreYear, textViewPercentAverageScoreYear, averageScoreYear, percentAverageScoreYear);
        formatRow(currentScore, textViewStatusMedianScoreYear, textViewLabelMedianScoreYear, textViewValueMedianScoreYear, textViewPercentMedianScoreYear, medianScoreYear, percentMedianScoreYear);

        formatRow(currentScore, textViewStatusBestScoreMonth, textViewLabelBestScoreMonth, textViewValueBestScoreMonth, textViewPercentBestScoreMonth, bestScoreMonth, percentBestScoreMonth);
        formatRow(currentScore, textViewStatusAverageScoreMonth, textViewLabelAverageScoreMonth, textViewValueAverageScoreMonth, textViewPercentAverageScoreMonth, averageScoreMonth, percentAverageScoreMonth);
        formatRow(currentScore, textViewStatusMedianScoreMonth, textViewLabelMedianScoreMonth, textViewValueMedianScoreMonth, textViewPercentMedianScoreMonth, medianScoreMonth, percentMedianScoreMonth);

        formatRow(currentScore, textViewStatusBestScoreWeek, textViewLabelBestScoreWeek, textViewValueBestScoreWeek, textViewPercentBestScoreWeek, bestScoreWeek, percentBestScoreWeek);
        formatRow(currentScore, textViewStatusAverageScoreWeek, textViewLabelAverageScoreWeek, textViewValueAverageScoreWeek, textViewPercentAverageScoreWeek, averageScoreWeek, percentAverageScoreWeek);
        formatRow(currentScore, textViewStatusMedianScoreWeek, textViewLabelMedianScoreWeek, textViewValueMedianScoreWeek, textViewPercentMedianScoreWeek, medianScoreWeek, percentMedianScoreWeek);

        formatRow(currentScore, textViewStatusCurrentGoal, textViewLabelCurrentGoal, textViewValueCurrentGoal, textViewPercentCurrentGoal, currentGoal, percentCurrentGoal);

    }

    private void formatRow(double currentScore, TextView tVStatus, TextView tVLabel, TextView tVValue,
                           TextView tVPercent, double score, double percent) {
            if (percent < 20) {
                tVStatus.setText("🔴");
                tVLabel.setTextColor(getResources().getColor(R.color.red));
                tVValue.setTextColor(getResources().getColor(R.color.red));
                tVPercent.setTextColor(getResources().getColor(R.color.red));
            } else if (percent >= 20 && percent < 40) {
                tVStatus.setText("🟠");
                tVLabel.setTextColor(getResources().getColor(R.color.orange));
                tVValue.setTextColor(getResources().getColor(R.color.orange));
                tVPercent.setTextColor(getResources().getColor(R.color.orange));
            } else if (percent >= 40 && percent < 60) {
                tVStatus.setText("🟡");
                tVLabel.setTextColor(getResources().getColor(R.color.yellow));
                tVValue.setTextColor(getResources().getColor(R.color.yellow));
                tVPercent.setTextColor(getResources().getColor(R.color.yellow));
            } else if (percent >= 60 && percent < 80) {
                tVStatus.setText("⚪️");
                tVLabel.setTextColor(getResources().getColor(R.color.white));
                tVValue.setTextColor(getResources().getColor(R.color.white));
                tVPercent.setTextColor(getResources().getColor(R.color.white));
            } else if (percent >= 80 && percent < 100) {
                tVStatus.setText("🟢");
                tVLabel.setTextColor(getResources().getColor(R.color.green));
                tVValue.setTextColor(getResources().getColor(R.color.green));
                tVPercent.setTextColor(getResources().getColor(R.color.green));
            } else {
                tVStatus.setText("🏆");
                tVLabel.setTextColor(getResources().getColor(R.color.green));
                tVValue.setTextColor(getResources().getColor(R.color.green));
                tVPercent.setTextColor(getResources().getColor(R.color.green));
            }
        String formattedPercent = decimalFormat.format(percent);
        tVValue.setText(formatTimeStamp(score));
        tVPercent.setText(formattedPercent + "%");
    }


    private long getMedianTime(String scope) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE " + DatabaseHelper.COLUMN_TIMESTAMP + " >= ?";
        long currentTime = System.currentTimeMillis();
        long startTime;

        switch (scope) {
            case "year":
                startTime = currentTime - TimeUnit.DAYS.toMillis(365); // Subtract one year
                break;
            case "month":
                startTime = currentTime - TimeUnit.DAYS.toMillis(30); // Subtract one month (approximately)
                break;
            case "week":
                startTime = currentTime - TimeUnit.DAYS.toMillis(7); // Subtract one week
                break;
            default:
                startTime = 0; // Default: no filter
        }

        long installationTimestamp = getInstallationTimestamp(); // You need to implement this method
        String[] selectionArgs = {String.valueOf(startTime)};
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, selectionArgs);
            if (cursor.moveToFirst()) {
                long[] timeDifferences = new long[cursor.getCount() + 1 - 1];
                long previousTimestamp = cursor.getLong(0);
                int numberOfTimeDifferences = 0;

                while (cursor.moveToNext()) {
                    long currentTimestamp = cursor.getLong(0);
                    long timeDifference = currentTimestamp - previousTimestamp;
                    timeDifferences[numberOfTimeDifferences] = timeDifference;
                    numberOfTimeDifferences++;
                    previousTimestamp = currentTimestamp;
                }
                long timeDifference = System.currentTimeMillis() - previousTimestamp;
                timeDifferences[numberOfTimeDifferences] = timeDifference;
                numberOfTimeDifferences++;


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
                    return System.currentTimeMillis() - installationTimestamp;
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



    private String formatTimeStamp(double timestamp) {
        Instant instant = Instant.ofEpochMilli((long) timestamp);
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
            return String.format("%dY %dM %dD %d:%d:%d", years, months, days, hours, minutes, seconds);
        } else if (months > 0) {
            return String.format("%dM %dD %02d:%02d:%02d", months, days, hours, minutes, seconds);
        } else if (days > 0) {
            return String.format("%dD %02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("%02d", seconds);
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

    public void onMenuIconClicked(View view) {
        // Determine the current context (main or settings) based on your logic.
        boolean isInSettings = false; // Set this to true if you're in the settings context, or false if in the main context.

        if (!isInSettings) {
            // Open the settings activity.
            Intent settingsIntent = new Intent(this, SettingsActivity.class); // Replace SettingsActivity with your actual settings activity class.
            startActivity(settingsIntent);
        } else {
            // Switch back to the main activity.
            Intent mainIntent = new Intent(this, MainActivity.class); // Replace MainActivity with your actual main activity class.
            startActivity(mainIntent);
        }
    }
}
