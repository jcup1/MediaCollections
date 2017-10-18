package mywins.theandroiddev.com.mediacollections;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mywins.theandroiddev.com.mediacollections.views.PlayerView;

public class SimpleMusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder mBinder = new SimpleMusicService.JcPlayerServiceBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private int duration;
    private int currentTime;
    private Audio currentAudio;
    private AudioStatus audioStatus = new AudioStatus();
    private List<PlayerView.JcPlayerViewServiceListener> jcPlayerServiceListeners;
    private List<PlayerView.JcPlayerViewStatusListener> jcPlayerStatusListeners;
    private AssetFileDescriptor assetFileDescriptor = null;

    public SimpleMusicService() {
    }

    public void registerServicePlayerListener(PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener) {
        if (jcPlayerServiceListeners == null) {
            jcPlayerServiceListeners = new ArrayList<>();
        }

        if (!jcPlayerServiceListeners.contains(jcPlayerServiceListener)) {
            jcPlayerServiceListeners.add(jcPlayerServiceListener);
        }
    }

    public void registerStatusListener(PlayerView.JcPlayerViewStatusListener statusListener) {
        if (jcPlayerStatusListeners == null) {
            jcPlayerStatusListeners = new ArrayList<>();
        }

        if (!jcPlayerStatusListeners.contains(statusListener)) {
            jcPlayerStatusListeners.add(statusListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void pause(Audio audio) {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            duration = mediaPlayer.getDuration();
            currentTime = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }

        for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
            jcPlayerServiceListener.onPaused();
        }

        if (jcPlayerStatusListeners != null) {
            for (PlayerView.JcPlayerViewStatusListener jcPlayerStatusListener : jcPlayerStatusListeners) {
                audioStatus.setAudio(audio);
                audioStatus.setDuration(duration);
                audioStatus.setCurrentPosition(currentTime);
                audioStatus.setPlayState(AudioStatus.PlayState.PAUSE);
                jcPlayerStatusListener.onPausedStatus(audioStatus);
            }
        }
    }

    public void destroy() {
        stop();
        stopSelf();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        isPlaying = false;
    }

    public void play(Audio audio) {
        Audio tempAudio = this.currentAudio;
        this.currentAudio = audio;

        if (isAudioFileValid(audio.getPath())) {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();

                    assetFileDescriptor = getApplicationContext().getAssets().openFd(audio.getPath());
                    mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                            assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
                    assetFileDescriptor.close();
                    assetFileDescriptor = null;

                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.setOnCompletionListener(this);

                } else {
                    if (isPlaying) {
                        stop();
                        play(audio);
                    } else {
                        if (tempAudio != audio) {
                            stop();
                            play(audio);
                        } else {
                            mediaPlayer.start();
                            isPlaying = true;

                            if (jcPlayerServiceListeners != null) {
                                for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                                    jcPlayerServiceListener.onContinueAudio();
                                }
                            }

                            if (jcPlayerStatusListeners != null) {
                                for (PlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                                    audioStatus.setAudio(audio);
                                    audioStatus.setPlayState(AudioStatus.PlayState.PLAY);
                                    audioStatus.setDuration(mediaPlayer.getDuration());
                                    audioStatus.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                    jcPlayerViewStatusListener.onContinueAudioStatus(audioStatus);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            updateTimeAudio();

            for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.onPlaying();
            }

            if (jcPlayerStatusListeners != null) {
                for (PlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                    audioStatus.setAudio(audio);
                    audioStatus.setPlayState(AudioStatus.PlayState.PLAY);
                    audioStatus.setCurrentPosition(0);
                    jcPlayerViewStatusListener.onPlayingStatus(audioStatus);
                }
            }

        }
    }

    public void seekTo(int time) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }

    private void updateTimeAudio() {
        new Thread() {
            public void run() {
                while (isPlaying) {
                    try {

                        if (jcPlayerServiceListeners != null) {
                            for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                                jcPlayerServiceListener.onTimeChanged(mediaPlayer.getCurrentPosition());
                            }
                        }

                        if (jcPlayerStatusListeners != null) {
                            for (PlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                                audioStatus.setPlayState(AudioStatus.PlayState.PLAY);
                                audioStatus.setDuration(mediaPlayer.getDuration());
                                audioStatus.setCurrentPosition(mediaPlayer.getCurrentPosition());
                                jcPlayerViewStatusListener.onTimeChangedStatus(audioStatus);
                            }
                        }
                        Thread.sleep(200);
                    } catch (IllegalStateException | InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (jcPlayerServiceListeners != null) {
            for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.onCompletedAudio();
            }
        }

        if (jcPlayerStatusListeners != null) {
            for (PlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                jcPlayerViewStatusListener.onCompletedAudioStatus(audioStatus);
            }
        }
    }

    private boolean isAudioFileValid(String path) {

        try {
            assetFileDescriptor = null;
            assetFileDescriptor = getApplicationContext().getAssets().openFd(path);
            return assetFileDescriptor != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        isPlaying = true;
        this.duration = mediaPlayer.getDuration();
        this.currentTime = mediaPlayer.getCurrentPosition();
        updateTimeAudio();

        if (jcPlayerServiceListeners != null) {
            for (PlayerView.JcPlayerViewServiceListener jcPlayerServiceListener : jcPlayerServiceListeners) {
                jcPlayerServiceListener.updateTitle(currentAudio.getTitle());
                jcPlayerServiceListener.onPreparedAudio(currentAudio.getTitle(), mediaPlayer.getDuration());
            }
        }

        if (jcPlayerStatusListeners != null) {
            for (PlayerView.JcPlayerViewStatusListener jcPlayerViewStatusListener : jcPlayerStatusListeners) {
                audioStatus.setAudio(currentAudio);
                audioStatus.setPlayState(AudioStatus.PlayState.PLAY);
                audioStatus.setDuration(duration);
                audioStatus.setCurrentPosition(currentTime);
                jcPlayerViewStatusListener.onPreparedAudioStatus(audioStatus);
            }
        }
    }

    public Audio getCurrentAudio() {
        return currentAudio;
    }

    public class JcPlayerServiceBinder extends Binder {
        public SimpleMusicService getService() {
            return SimpleMusicService.this;
        }
    }
}
