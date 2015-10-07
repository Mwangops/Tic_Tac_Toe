package com.hackerangolano.tictactoe;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Asus on 06/09/2015.
 */
public class TicTacToe extends Application {
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(R.xml.analytics_global_config);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

    synchronized Tracker getTracker() { return tracker; }

    synchronized GoogleAnalytics getAnalytics() { return analytics; }
}
