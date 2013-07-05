package com.rogerethomas.com.sweetbrown;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SweetBrown extends Activity {

    private final Context context = this;
    private static final String TAG = "SweetBrown";
    private File sound;
    private final File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweet_brown);
        onLaunch();
        ImageView sweetBrown = (ImageView) findViewById(R.id.sweetBrownImage);
        sweetBrown.setClickable(true);
        sweetBrown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ask();
            }
        });
    }

    /**
     * On click of the Sweet Brown photo, the user will get this popup.
     */
    private void ask() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sweet_brown_ringtone);
        mediaPlayer.start();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Sweet Brown");
        alert.setMessage("You want this sound as your ringtone?");
        alert.setCancelable(false);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                confirmed();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /**
     * When the user confirms the popup, this will run, saving the file and
     * setting the ringtone utilising the RingtoneManager class.
     */
    private void confirmed() {
        Boolean success = false;
        sound = new File(folder, "sweet_brown_ringtone.mp3");
        if (!sound.exists()) {
            Log.i(TAG, "Writing Sweet Brown to " + folder.toString());
            try {
                InputStream in = getResources().openRawResource(R.raw.sweet_brown_ringtone);
                FileOutputStream out = new FileOutputStream(sound.getPath());
                byte[] buff = new byte[1024];
                int read = 0;

                try {
                    while ((read = in.read(buff)) > 0) {
                        out.write(buff, 0, read);
                    }
                } finally {
                    in.close();

                    out.close();
                }
            } catch (Exception e) {
                success = false;
                Log.i(TAG, "Sweet Brown failed to write.");
            }
        } else {
            success = true;
            Log.i(TAG, "Sweet Brown ringtone already there.");
        }

        if (!success) {
            onSetRingtoneError("We couldn't give you a Sweet Brown ringtone.\n\nThere's an issue writing the file.");
        } else {
            setRingtone();
        }
    }

    /**
     * Physically sets the RingtoneManager preferences.
     */
    private void setRingtone() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, sound.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, "Sweet Brown - Ain't nobody got time for that");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
            values.put(MediaStore.Audio.Media.ARTIST, "Sweet Brown");
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(sound.getAbsolutePath());
            getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + sound.getAbsolutePath() + "\"",
                    null);
            Uri newUri = getContentResolver().insert(uri, values);

            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
            onSetRingtoneSuccess();
        } catch (Exception e) {
            onSetRingtoneError("We couldn't give you a Sweet Brown ringtone.\n\nThere's an issue setting the file.");
        }
    }

    /**
     * Generic helper to show error messages (if ever there are any) :p
     * 
     * @param message
     */
    private void onSetRingtoneError(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Sweet Brown");
        alert.setMessage(message);
        alert.setCancelable(false);
        alert.setPositiveButton("OK. That's too bad.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /**
     * Welcome message
     */
    private void onLaunch() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Sweet Brown");
        alert.setMessage("Click Sweet Brown to set your ringtone!");
        alert.setCancelable(false);
        alert.setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /**
     * After the ringtone is set, we show a sweet message :)
     */
    private void onSetRingtoneSuccess() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Sweet Brown");
        alert.setMessage("You've got the ringtone :)\n\nHope you enjoy it!");
        alert.setCancelable(false);
        alert.setPositiveButton("Sweet!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onPause();
    }

}
