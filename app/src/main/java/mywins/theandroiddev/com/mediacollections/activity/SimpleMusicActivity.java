package mywins.theandroiddev.com.mediacollections.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import java.util.ArrayList;

import mywins.theandroiddev.com.mediacollections.Audio;
import mywins.theandroiddev.com.mediacollections.AudioAdapter;
import mywins.theandroiddev.com.mediacollections.R;
import mywins.theandroiddev.com.mediacollections.views.PlayerView;

/**
 * Created by jakub on 17.10.17.
 */

public class SimpleMusicActivity extends AppCompatActivity {

    private PlayerView player;
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_music);

        recyclerView = findViewById(R.id.recyclerView);
        player = findViewById(R.id.gallery_jcplayer);

        ArrayList<Audio> audios = new ArrayList<>();
        audios.add(Audio.createFromAssets("Asset audio 1", "49.v4.mid"));
        audios.add(Audio.createFromAssets("Asset audio 2", "56.mid"));
        audios.add(Audio.createFromAssets("Asset audio 3", "a_34.mp3"));

        player.initPlaylist(audios);

        adapterSetup();
    }

    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(player.getMyPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                player.playAudio(player.getMyPlaylist().get(position));
            }

        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.kill();
    }

}