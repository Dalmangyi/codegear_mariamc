package com.codegear.mariamc_rfid.rfidreader.common;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codegear.mariamc_rfid.application.Application;
import com.codegear.mariamc_rfid.R;
import com.codegear.mariamc_rfid.rfidreader.rfid.RFIDController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by qvfr34 on 2/16/2015.
 */
public class InventoryTimer {
    private static final int INV_UPDATE_INTERVAL = 500;
    private static InventoryTimer inventorytimer;
    private static long startedTime;
    private AppCompatActivity activity;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> rrTimer;

    public static InventoryTimer getInstance() {
        if (inventorytimer == null)
            inventorytimer = new InventoryTimer();
        return inventorytimer;
    }

    public void startTimer() {
        if (isTimerRunning())
            stopTimer();
        startedTime = System.currentTimeMillis();
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1);
            final Runnable task = new Runnable() {
                public void run() {
                    //ReadRate = (No Of Tags Read / Inventory Duration)
                    RFIDController.mRRStartedTime += INV_UPDATE_INTERVAL;
                    if (RFIDController.mRRStartedTime == 0)
                        Application.TAG_READ_RATE = 0;
                    else
                        Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (RFIDController.mRRStartedTime / (float) 1000));
                    startedTime = System.currentTimeMillis();
                    updateUI();
                }
            };
            rrTimer = scheduler.scheduleAtFixedRate(task, INV_UPDATE_INTERVAL, INV_UPDATE_INTERVAL, MILLISECONDS);
        }
    }

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void stopTimer() {
        if (rrTimer != null) {
            //Stop the timer
            rrTimer.cancel(true);
            scheduler.shutdown();
            //ReadRate = (No Of Tags Read / Inventory Duration)
            RFIDController.mRRStartedTime += (System.currentTimeMillis() - startedTime);
            if (RFIDController.mRRStartedTime == 0)
                Application.TAG_READ_RATE = 0;
            else
                Application.TAG_READ_RATE = (int) (Application.TOTAL_TAGS / (RFIDController.mRRStartedTime / (float) 1000));
        }
        rrTimer = null;
        scheduler = null;
        updateUI();
    }

    public boolean isTimerRunning() {
        if (rrTimer != null)
            return true;
        return false;
    }

    void updateUI() {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                StringBuilder min;
                StringBuilder sec;

                @Override
                public void run() {
                    TextView readRate = (TextView) activity.findViewById(R.id.readRateContent);
                    TextView timeText = (TextView) activity.findViewById(R.id.readTimeContent);
                    TextView uniqueTags = (TextView) activity.findViewById(R.id.uniqueTagContent);
                    TextView totalTags = (TextView) activity.findViewById(R.id.totalTagContent);

                    if (readRate != null) {
                        readRate.setText(Application.TAG_READ_RATE + Constants.TAGS_SEC);
                    }
                    if (timeText != null) {
                        long displayTime = RFIDController.mRRStartedTime;
                        min = new StringBuilder(String.format("%d", TimeUnit.MILLISECONDS.toMinutes(displayTime)));
                        sec = new StringBuilder(String.format("%d", TimeUnit.MILLISECONDS.toSeconds(displayTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(displayTime))));
                        if (min.length() == 1) {
                            min = min.insert(0, "0");
                        }
                        if (sec.length() == 1) {
                            sec = sec.insert(0, "0");
                        }
                        timeText.setText(min + ":" + sec);
                    }
                    min = null;
                    sec = null;
                }
            });
        }
    }


}
