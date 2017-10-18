package mywins.theandroiddev.com.mediacollections;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import mywins.theandroiddev.com.mediacollections.views.PlayerView;

/**
 * Created by jakub on 17.10.17.
 */

public class AudioPlayer {
    private static final String TAG = "AudioPlayer";
    private AudioPlayer instance = null;
    private SimpleMusicService jcPlayerService;
    private PlayerView.JcPlayerViewServiceListener listener;
    private List<Audio> playlist;
    private Audio currentAudio;
    private int currentPositionList;
    private Context context;
    private boolean mBound = false;
    private int position = 1;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            SimpleMusicService.JcPlayerServiceBinder binder = (SimpleMusicService.JcPlayerServiceBinder) service;
            jcPlayerService = binder.getService();

            if (listener != null) {
                jcPlayerService.registerServicePlayerListener(listener);
            }

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public AudioPlayer(Context context, List<Audio> playlist, PlayerView.JcPlayerViewServiceListener listener) {
        this.context = context;
        this.playlist = playlist;
        this.listener = listener;
        instance = AudioPlayer.this;

        initService();
    }

    public AudioPlayer getInstance() {
        return instance;
    }


    public void playAudio(Audio Audio) {

        currentAudio = Audio;
        jcPlayerService.play(currentAudio);

        updatePositionAudioList();

    }

    public void stopAudio() {

        jcPlayerService.pause(currentAudio);
        seekTo(0);

    }

    private void initService() {
        if (!mBound) {
            startJcPlayerService();
        } else {
            mBound = true;
        }
    }

    public void nextAudio() {

        if (currentAudio != null) {
            try {
                Audio nextAudio = playlist.get(currentPositionList + position);
                this.currentAudio = nextAudio;
                jcPlayerService.stop();
                jcPlayerService.play(nextAudio);

            } catch (IndexOutOfBoundsException e) {
                playAudio(playlist.get(0));
                e.printStackTrace();
            }
        }

        updatePositionAudioList();

    }

    public void previousAudio() {
        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(jcPlayerService, "Error", Toast.LENGTH_SHORT).show();

        } else {
            if (currentAudio != null) {
                try {
                    Audio previousAudio = playlist.get(currentPositionList - position);
                    this.currentAudio = previousAudio;
                    jcPlayerService.stop();
                    jcPlayerService.play(previousAudio);

                } catch (IndexOutOfBoundsException e) {
                    playAudio(playlist.get(0));
                    e.printStackTrace();
                }
            }

            updatePositionAudioList();

        }
    }

    public void pauseAudio() {
        jcPlayerService.pause(currentAudio);

    }

    public void continueAudio() {
        if (playlist == null || playlist.size() == 0) {
            Toast.makeText(jcPlayerService, "Error", Toast.LENGTH_SHORT).show();
        } else {
            if (currentAudio == null) {
                currentAudio = playlist.get(0);
            }
            playAudio(currentAudio);

        }
    }

    public void seekTo(int time) {
        if (jcPlayerService != null) {
            jcPlayerService.seekTo(time);
        }
    }

    private void updatePositionAudioList() {
        for (int i = 0; i < playlist.size(); i++) {
            if (playlist.get(i).getId() == currentAudio.getId()) {
                this.currentPositionList = i;
            }
        }
    }

    private synchronized void startJcPlayerService() {
        if (!mBound) {
            Intent intent = new Intent(context.getApplicationContext(), SimpleMusicService.class);
            context.bindService(intent, mConnection, context.getApplicationContext().BIND_AUTO_CREATE);
        }
    }

    public void kill() {
        if (jcPlayerService != null) {
            jcPlayerService.stop();
            jcPlayerService.destroy();
        }

        if (mBound)
            try {
                context.unbindService(mConnection);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "kill: " + e);
            }

    }

    public List<Audio> getPlaylist() {
        return playlist;
    }

    public Audio getCurrentAudio() {
        return jcPlayerService.getCurrentAudio();
    }
}
