package com.cibotechnology.audio;

import android.media.MediaPlayer;
import android.os.Binder;

public class MediaBinder extends Binder {
    MediaPlayer player;

    public MediaBinder(MediaPlayer player) {
        this.player = player;
    }

    public MediaPlayer getMediaPlayer() {
        return this.player;
    }

    public void setMediaPlayer(MediaPlayer player) {
        this.player = player;
        notifyListenerMediaPlayerUpdated();
    }

    AudioStreamListener listener;

    public AudioStreamListener getListener() {
        return listener;
    }

    public void setListener(AudioStreamListener listener) {
        this.listener = listener;
    }

    public void notifyListenerMediaPlayerUpdated() {
        if (null != this.listener) {
            listener.OnMediaPlayerChange(this.player);
        }
    }

}
