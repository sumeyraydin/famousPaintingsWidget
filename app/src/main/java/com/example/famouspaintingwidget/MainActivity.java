package com.example.famouspaintingwidget;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int paintingId = getIntent().getIntExtra("painting_id", -1);
        Painting painting = PaintingWidget.getPaintingById(this, paintingId);

        ImageView iv = findViewById(R.id.painting_image);
        TextView tvTitle = findViewById(R.id.painting_title);
        TextView tvStory = findViewById(R.id.painting_story);

        if (painting != null) {
            Glide.with(this).load(painting.image).into(iv);
            tvTitle.setText(painting.title);
            tvStory.setText(painting.story);
        }
    }
}
