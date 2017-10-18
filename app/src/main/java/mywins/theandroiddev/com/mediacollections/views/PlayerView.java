package mywins.theandroiddev.com.mediacollections.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.List;

import mywins.theandroiddev.com.mediacollections.Audio;
import mywins.theandroiddev.com.mediacollections.AudioPlayer;
import mywins.theandroiddev.com.mediacollections.AudioStatus;
import mywins.theandroiddev.com.mediacollections.R;

/**
 * Created by jakub on 17.10.17.
 */

public class PlayerView extends LinearLayout implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int PULSE_ANIMATION_DURATION = 200;
    private static final int TITLE_ANIMATION_DURATION = 600;

    private TextView txtCurrentMusic;
    private ImageButton btnPrev, btnPlay, btnStop, btnNext;
    private ProgressBar progressBarPlayer;
    private AudioPlayer audioPlayer;
    private TextView txtDuration;
    private SeekBar seekBar;
    private TextView txtCurrentDuration;
    JcPlayerViewServiceListener jcPlayerViewServiceListener = new JcPlayerViewServiceListener() {

        @Override
        public void onPreparedAudio(String audioName, int duration) {
            dismissProgressBar();
            resetPlayerInfo();

            long aux = duration / 1000;
            int minute = (int) (aux / 60);
            int second = (int) (aux % 60);

            final String sDuration =
                    (minute < 10 ? "0" + minute : minute + "")
                            + ":" + (second < 10 ? "0" + second : second + "");

            seekBar.setMax(duration);

            txtDuration.post(new Runnable() {
                @Override
                public void run() {
                    txtDuration.setText(sDuration);
                }
            });
        }

        @Override
        public void onCompletedAudio() {
            resetPlayerInfo();

            try {
                audioPlayer.nextAudio();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPaused() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_play_arrow_black_24dp, null));
            } else {
                btnPlay.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_play_arrow_black_24dp, null));
            }
            btnPlay.setTag(R.drawable.ic_play_arrow_black_24dp);
        }

        @Override
        public void onContinueAudio() {
            dismissProgressBar();
        }

        @Override
        public void onPlaying() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnPlay.setBackground(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_pause_black_24dp, null));
            } else {
                btnPlay.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_pause_black_24dp, null));
            }
            btnPlay.setTag(R.drawable.ic_pause_black_24dp);

        }

        @Override
        public void onTimeChanged(long currentPosition) {
            long aux = currentPosition / 1000;
            int minutes = (int) (aux / 60);
            int seconds = (int) (aux % 60);
            final String sMinutes = minutes < 10 ? "0" + minutes : minutes + "";
            final String sSeconds = seconds < 10 ? "0" + seconds : seconds + "";

            seekBar.setProgress((int) currentPosition);
            txtCurrentDuration.post(new Runnable() {
                @Override
                public void run() {
                    txtCurrentDuration.setText(String.valueOf(sMinutes + ":" + sSeconds));
                }
            });
        }

        @Override
        public void updateTitle(final String title) {

            YoYo.with(Techniques.FadeInLeft)
                    .duration(TITLE_ANIMATION_DURATION)
                    .playOn(txtCurrentMusic);

            txtCurrentMusic.post(new Runnable() {
                @Override
                public void run() {
                    txtCurrentMusic.setText(title);
                }
            });
        }
    };
    private boolean isInitialized;

    public PlayerView(Context context) {
        super(context);
        init();
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        inflate(getContext(), R.layout.view_jcplayer, this);

        this.progressBarPlayer = findViewById(R.id.progress_bar_player);
        this.btnNext = findViewById(R.id.btn_next);
        this.btnPrev = findViewById(R.id.btn_prev);
        this.btnPlay = findViewById(R.id.btn_play);
        this.btnStop = findViewById(R.id.btn_stop);
        this.txtDuration = findViewById(R.id.txt_total_duration);
        this.txtCurrentDuration = findViewById(R.id.txt_current_duration);
        this.txtCurrentMusic = findViewById(R.id.txt_current_music);
        this.seekBar = findViewById(R.id.seek_bar);
        this.btnPlay.setTag(R.drawable.ic_play_arrow_black_24dp);

        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
    }

    public void initPlaylist(List<Audio> playlist) {

        if (!isAlreadySorted(playlist)) {
            sortPlaylist(playlist);
        }
        audioPlayer = new AudioPlayer(getContext(), playlist, jcPlayerViewServiceListener);
        isInitialized = true;
    }

    public void playAudio(Audio audio) {
        showProgressBar();
        createJcAudioPlayer();
        if (!audioPlayer.getPlaylist().contains(audio))
            audioPlayer.getPlaylist().add(audio);
        audioPlayer.playAudio(audio);

    }

    public void next() {
        if (audioPlayer.getCurrentAudio() == null) {
            return;
        }
        resetPlayerInfo();
        showProgressBar();

        audioPlayer.nextAudio();

    }

    public void continueAudio() {
        showProgressBar();

        audioPlayer.continueAudio();

    }

    public void pause() {
        audioPlayer.pauseAudio();
    }

    public void stop() {
        audioPlayer.stopAudio();
        txtCurrentDuration.setText(getContext().getString(R.string.play_initial_time));
        seekBar.setProgress(0);
    }

    public void previous() {
        resetPlayerInfo();
        showProgressBar();
        audioPlayer.previousAudio();

    }

    @Override
    public void onClick(View view) {
        if (isInitialized) {
            if (view.getId() == R.id.btn_play) {
                YoYo.with(Techniques.Pulse)
                        .duration(PULSE_ANIMATION_DURATION)
                        .playOn(btnPlay);

                if (btnPlay.getTag().equals(R.drawable.ic_pause_black_24dp)) {
                    pause();
                } else {
                    continueAudio();
                }
            }
            if (view.getId() == R.id.btn_stop) {
                YoYo.with(Techniques.Pulse)
                        .duration(PULSE_ANIMATION_DURATION)
                        .playOn(btnStop);

                stop();
            }
        }
        if (view.getId() == R.id.btn_next) {
            YoYo.with(Techniques.Pulse)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnNext);
            next();
        }

        if (view.getId() == R.id.btn_prev) {
            YoYo.with(Techniques.Pulse)
                    .duration(PULSE_ANIMATION_DURATION)
                    .playOn(btnPrev);
            previous();
        }
    }

    public List<Audio> getMyPlaylist() {
        return audioPlayer.getPlaylist();
    }

    private void createJcAudioPlayer() {
        if (audioPlayer == null) {
            List<Audio> playlist = new ArrayList<>();
            audioPlayer = new AudioPlayer(getContext(), playlist, jcPlayerViewServiceListener);
        }
        isInitialized = true;
    }

    private void sortPlaylist(List<Audio> playlist) {
        for (int i = 0; i < playlist.size(); i++) {
            Audio audio = playlist.get(i);
            audio.setId(i);
            audio.setPosition(i);
        }
    }

    private boolean isAlreadySorted(List<Audio> playlist) {
        if (playlist != null) {
            return playlist.get(0).getPosition() != -1;
        } else {
            return false;
        }
    }

    private void showProgressBar() {
        progressBarPlayer.setVisibility(ProgressBar.VISIBLE);
        btnPlay.setVisibility(Button.GONE);
        btnNext.setClickable(false);
        btnPrev.setClickable(false);
    }

    private void dismissProgressBar() {
        progressBarPlayer.setVisibility(ProgressBar.GONE);
        btnPlay.setVisibility(Button.VISIBLE);
        btnNext.setClickable(true);
        btnPrev.setClickable(true);
    }

    private void resetPlayerInfo() {
        seekBar.setProgress(0);
        txtCurrentMusic.setText("");
        txtCurrentDuration.setText(getContext().getString(R.string.play_initial_time));
        txtDuration.setText(getContext().getString(R.string.play_initial_time));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        if (fromUser && audioPlayer != null) audioPlayer.seekTo(i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        showProgressBar();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        dismissProgressBar();
    }

    public void kill() {
        if (audioPlayer != null) audioPlayer.kill();
    }

    public interface JcPlayerViewStatusListener {
        void onPausedStatus(AudioStatus audioStatus);

        void onContinueAudioStatus(AudioStatus audioStatus);

        void onPlayingStatus(AudioStatus audioStatus);

        void onTimeChangedStatus(AudioStatus audioStatus);

        void onCompletedAudioStatus(AudioStatus audioStatus);

        void onPreparedAudioStatus(AudioStatus audioStatus);
    }

    public interface JcPlayerViewServiceListener {
        void onPreparedAudio(String audioName, int duration);

        void onCompletedAudio();

        void onPaused();

        void onContinueAudio();

        void onPlaying();

        void onTimeChanged(long currentTime);

        void updateTitle(String title);
    }


}
