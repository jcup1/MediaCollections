package mywins.theandroiddev.com.mediacollections;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.main_images_cv)
    CardView mainImagesCv;

    @BindView(R.id.main_music_cv)
    CardView mainMusicCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.main_images_cv)
    public void openImageGallery() {
        startActivity(new Intent(this, ImageGallery.class));

    }

    @OnClick(R.id.main_music_cv)
    public void openMusicGallery() {
        startActivity(new Intent(this, MusicGallery.class));
    }

}
