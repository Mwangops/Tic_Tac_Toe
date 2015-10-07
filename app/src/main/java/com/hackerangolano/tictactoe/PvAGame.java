package com.hackerangolano.tictactoe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Created by Asus on 07/09/2015.
 */
public class PvAGame extends AppCompatActivity {
    private TextView mTurnInfo, mWinsCount, mTiesCount, mLosesCount;
    private Button mBoardButtons[];
    private GameLogic mGame;
    private InterstitialAd mInterstitialAd;

    public static final String KEY_DIFFICULTY =
            "com.hackerangolano.tictactoe.difficulty",
    statsFilename = "statistics";
    private int mWinsCounter = 0, mTiesCounter = 0, mLosesCounter = 0;
    private int GAMES_COUNT, GAME_DIFFICULTY;
    private boolean mHumanFirst = true;
    private SharedPreferences statsPreference = null;
    private static Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout); // Set the layout
        context = this;

        // Create a new instance of the game logic
        mGame = new GameLogic();

        // Create and initialize the buttons
        mBoardButtons = new Button[mGame.getBoardSize()];
        mBoardButtons[0] = (Button) findViewById(R.id.button_1);
        mBoardButtons[1] = (Button) findViewById(R.id.button_2);
        mBoardButtons[2] = (Button) findViewById(R.id.button_3);
        mBoardButtons[3] = (Button) findViewById(R.id.button_4);
        mBoardButtons[4] = (Button) findViewById(R.id.button_5);
        mBoardButtons[5] = (Button) findViewById(R.id.button_6);
        mBoardButtons[6] = (Button) findViewById(R.id.button_7);
        mBoardButtons[7] = (Button) findViewById(R.id.button_8);
        mBoardButtons[8] = (Button) findViewById(R.id.button_9);

        // Set the onClick Listener on every button
        for (int i = 0; i < mGame.getBoardSize(); i++)
        {
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }

        // Get reference to remaining visual components of the layout
        Button mNewGame = (Button) findViewById(R.id.buttonNewGame);
        mNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mGame.gameEnd())
                {
                    new AlertDialog.Builder(context)
                            .setTitle(R.string.new_game_alert_title)
                            .setMessage(R.string.new_game_alert_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.new_game_alert_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mLosesCounter++;
                                    updateStats(GAME_DIFFICULTY);
                                    startNewGame();
                                }
                            })
                            .setNegativeButton(R.string.new_game_alert_negative, null)
                            .show();
                }
                else
                    startNewGame();
            }
        });
        mTurnInfo = (TextView) findViewById(R.id.turn_info);
        mWinsCount = (TextView) findViewById(R.id.wins_count);
        mTiesCount = (TextView) findViewById(R.id.ties_count);
        mLosesCount = (TextView) findViewById(R.id.loses_count);

        // Initialize the game counter and set the game difficulty
        GAMES_COUNT = 0;
        GAME_DIFFICULTY = getIntent().getIntExtra(KEY_DIFFICULTY, GameLogic.DIFFICULTY_EASY);

        // Read the stats stored before and display it
        readStats(GAME_DIFFICULTY);
        displayStats();

        // Prepare the Ad to be displayed at the bottom
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build(); //Add another test device with .addTestDevice("DeviceId")
        mAdView.loadAd(mAdRequest);

        // Prepare the Interstitial Ad to show before starting a new game
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        loadInterstitial(); // Call the method to load the Interstitial Ad asynchronously

        startNewGame();
    }

    private void readStats(int difficulty) {
        if(statsPreference == null)
        {
            statsPreference = getSharedPreferences(statsFilename, MODE_PRIVATE);
        }
        switch (difficulty)
        {
            case GameLogic.DIFFICULTY_EASY:
                mWinsCounter = statsPreference.getInt("gEasyWins", 0);
                mTiesCounter = statsPreference.getInt("gEasyTies", 0);
                mLosesCounter = statsPreference.getInt("gEasyLoses", 0);
                break;
            case GameLogic.DIFFICULTY_MEDIUM:
                mWinsCounter = statsPreference.getInt("gMediumWins", 0);
                mTiesCounter = statsPreference.getInt("gMediumTies", 0);
                mLosesCounter = statsPreference.getInt("gMediumLoses", 0);
                break;
            case GameLogic.DIFFICULTY_HARD:
                mWinsCounter = statsPreference.getInt("gHardWins", 0);
                mTiesCounter = statsPreference.getInt("gHardTies", 0);
                mLosesCounter = statsPreference.getInt("gHardLoses", 0);
                break;
            case GameLogic.DIFFICULTY_EXTREME:
                mWinsCounter = statsPreference.getInt("gExtremeWins", 0);
                mTiesCounter = statsPreference.getInt("gExtremeTies", 0);
                mLosesCounter = statsPreference.getInt("gExtremeLoses", 0);
                break;
        }
    }

    private void startNewGame() {
        int GAMES_TO_SHOW_AD = 10;

        GAMES_COUNT++;
        if(GAMES_COUNT % GAMES_TO_SHOW_AD == 0)
            showInterstitialAd();

        mGame.clearBoard();
        for(Button b: mBoardButtons)
        {
            b.setText(" ");
            b.setEnabled(true);
        }

        if(mHumanFirst)
        {
            mTurnInfo.setText(R.string.player_turn);
            mHumanFirst = false;
        }
        else
        {
            mTurnInfo.setText(R.string.android_turn);
            int move = mGame.getComputerMove(GAME_DIFFICULTY);
            setMove(GameLogic.PLAYER_2, move);
            mHumanFirst = true;
            mTurnInfo.setText(R.string.player_turn);
        }
    }

    private void showInterstitialAd() {
        // Show the Interstitial Ad if it is ready
        if(mInterstitialAd != null && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
    }

    private void loadInterstitial() {
        AdRequest mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.id_note3))
                .build();
        mInterstitialAd.loadAd(mAdRequest);
    }

    private void displayStats() {
        mWinsCount.setText(Integer.toString(mWinsCounter));
        mTiesCount.setText(Integer.toString(mTiesCounter));
        mLosesCount.setText(Integer.toString(mLosesCounter));
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }

    private class ButtonClickListener implements View.OnClickListener
    {

        int location;

        public ButtonClickListener(int location)
        {
            this.location = location;
        }

        @Override
        public void onClick(View v) {
            if(!mGame.gameEnd())
            {
                setMove(GameLogic.PLAYER_1, location);
                if(mGame.win(mGame.getBoard(), GameLogic.PLAYER_1))
                { // If player win
                    mTurnInfo.setText(R.string.win_info);
                    mWinsCounter++;
                    updateStats(GAME_DIFFICULTY);
                    mHumanFirst = true;
                }
                else // If player didn't win
                {
                    if(!mGame.gameEnd()) {
                        mTurnInfo.setText(R.string.android_turn);
                        int move = mGame.getComputerMove(GAME_DIFFICULTY);
                        setMove(GameLogic.PLAYER_2, move);
                        if(!mGame.gameEnd())
                            mTurnInfo.setText(R.string.player_turn);
                    }
                    if(mGame.win(mGame.getBoard(), GameLogic.PLAYER_2))
                    { // If Android win
                        mTurnInfo.setText(R.string.lose_info);
                        mLosesCounter++;
                        updateStats(GAME_DIFFICULTY);
                        mHumanFirst = false;
                    }
                    if(mGame.possibleMoves(mGame.getBoard()).length == 0 &&
                            !mGame.win(mGame.getBoard(), GameLogic.PLAYER_1) &&
                            !mGame.win(mGame.getBoard(), GameLogic.PLAYER_2))
                    { // If no one win
                        mTurnInfo.setText(R.string.tie_info);
                        mTiesCounter++;
                        updateStats(GAME_DIFFICULTY);
                    }
                }
            }
        }
    }

    private void updateStats(int difficulty) {
        // Get a reference to SharedPreferences if statsPreference is null
        if(statsPreference == null)
            statsPreference = getSharedPreferences(statsFilename, MODE_PRIVATE);

        SharedPreferences.Editor statsEditor = statsPreference.edit(); // Open statsPreference for edit
        switch (difficulty)
        {  // Update stats based on difficulty
            case GameLogic.DIFFICULTY_EASY:
                statsEditor.putInt("gEasyWins", mWinsCounter);
                statsEditor.putInt("gEasyTies", mTiesCounter);
                statsEditor.putInt("gEasyLoses", mLosesCounter);
                break;
            case GameLogic.DIFFICULTY_MEDIUM:
                statsEditor.putInt("gMediumWins", mWinsCounter);
                statsEditor.putInt("gMediumTies", mTiesCounter);
                statsEditor.putInt("gMediumLoses", mLosesCounter);
                break;
            case GameLogic.DIFFICULTY_HARD:
                statsEditor.putInt("gHardWins", mWinsCounter);
                statsEditor.putInt("gHardTies", mTiesCounter);
                statsEditor.putInt("gHardLoses", mLosesCounter);
                break;
            case GameLogic.DIFFICULTY_EXTREME:
                statsEditor.putInt("gExtremeWins", mWinsCounter);
                statsEditor.putInt("gExtremeTies", mTiesCounter);
                statsEditor.putInt("gExtremeLoses", mLosesCounter);
                break;
        }
        statsEditor.apply();
        displayStats();
    }

    private void setMove(char player, int location)
    {
        mGame.move(mGame.getBoard(), player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == GameLogic.PLAYER_1)
            mBoardButtons[location].setTextColor(
                    getResources().getColor(R.color.player1_selected)
            );
        else
            mBoardButtons[location].setTextColor(
                    getResources().getColor(R.color.player2_selected)
            );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pva_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
