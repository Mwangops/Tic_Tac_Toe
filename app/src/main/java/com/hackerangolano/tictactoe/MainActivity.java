package com.hackerangolano.tictactoe;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Anderson Costa on 30/07/2015.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private final int PvPMode = 0, PvAMode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for this file

        // Prepare the Ad to be displayed at the bottom of the screen
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getResources().getString(R.string.id_note3))
                .build(); //Add another test device with .addTestDevice("DeviceId")
        mAdView.loadAd(mAdRequest);

        // Create 4 buttons to hold the actual instances of the buttons
        Button btnPlay, btnHelp, btnStats, btnExit;

        // Initialize the buttons
        btnPlay = (Button) findViewById(R.id.button_play);
        btnHelp = (Button) findViewById(R.id.button_help);
        btnStats = (Button) findViewById(R.id.button_statistics);
        btnExit = (Button) findViewById(R.id.button_exit);

        // Set the onClick listener for all buttons
        btnPlay.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnStats.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        // Send a new event to Google Analytics
        Tracker tracker = ((TicTacToe) getApplication()).getTracker();
        tracker.setScreenName("Main Activity");
        tracker.setAppVersion(getResources().getString(R.string.app_version));
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("General")
                .setAction("start")
                .setLabel("Game Started")
                .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {// TODO: finish menu items
            case R.id.menu_option_about:
                return true;
            case R.id.menu_option_contact:
                return true;
            case R.id.menu_option_more_games:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // TODO: end other button action
        // Get the ID of the button clicked and execute the right actions
        switch (v.getId())
        {
            case R.id.button_play:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.play_dialog_title)
                        .setItems(R.array.play_dialog_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openNewGameDialog(which);
                            }
                        })
                        .show(); // Open a new Dialog box to select type of game
                break;
            case R.id.button_statistics:
                //startActivity(new Intent(this, Statistics.class)); // Open stats
                break;
            case R.id.button_help:
                //startActivity(new Intent(this, Help.class)); // Open help
                break;
            case R.id.button_exit: // Terminates the application
                finish();
                System.exit(0);
                break;
        }
    }

    private void openNewGameDialog(int gameType) {
        switch (gameType)
        {
            case PvAMode:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.difficulty_dialog_title)
                        .setItems(R.array.difficulty_dialog_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGame(PvAMode, which);
                            }
                        })
                        .show();
                break;
            case PvPMode:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.multiplayer_dialog_title)
                        .setItems(R.array.multiplayer_dialog_options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGame(PvPMode, which);
                            }
                        })
                        .show();
                break;
        }
    }

    private void startGame(int gameMode, int extra) {
        switch (gameMode)
        {
            case PvPMode:
                // TODO: end PvP Mode
                Toast.makeText(this, "PvP Mode", Toast.LENGTH_SHORT).show();
                break;
            case PvAMode:
                Intent intent = new Intent(this, PvAGame.class);
                intent.putExtra(PvAGame.KEY_DIFFICULTY, extra);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Notify analytics that activity has started
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        // Notify analytics that activity has stopped
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Play background sound if option enabled
        Music.play(this, R.raw.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop background sound
        Music.stop(this);
    }
}
