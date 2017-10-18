package mywins.theandroiddev.com.mediacollections;

/**
 * Created by jakub on 17.10.17.
 */

public class AudioStatus {
    private Audio audio;
    private long duration;
    private long currentPosition;
    private PlayState playState;

    public AudioStatus() {
        this(null, 0, 0, PlayState.UNINTIALIZED);
    }

    public AudioStatus(Audio audio, long duration, long currentPosition, PlayState playState) {
        this.audio = audio;
        this.duration = duration;
        this.currentPosition = currentPosition;
        this.playState = playState;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PlayState getPlayState() {
        return playState;
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }

    public enum PlayState {
        PLAY, PAUSE, STOP, UNINTIALIZED
    }
}
