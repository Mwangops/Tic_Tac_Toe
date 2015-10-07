package com.hackerangolano.tictactoe;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by Asus on 06/09/2015.
 */
public class Music {
    private static MediaPlayer mediaPlayer = null;

    // Play a song on the background
    public static void play(Context context, int resource) {
        // Stop old song
        stop(context);

        // and start new one
        // Start music only if not disabled in preferences
        if (Prefs.getMusic(context)) {
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    // Stop background song
    public static void stop(Context context) {
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
