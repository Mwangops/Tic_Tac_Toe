package com.hackerangolano.tictactoe;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Asus on 06/09/2015.
 */
public class Prefs extends AppCompatActivity {
    private static final String OPT_MUSIC = "music";
    private static final boolean OPT_MUSIC_DEF = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static boolean getMusic(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
    }
}
