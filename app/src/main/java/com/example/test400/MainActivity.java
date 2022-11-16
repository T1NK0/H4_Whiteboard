package com.example.test400;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    com.google.android.material.floatingactionbutton.FloatingActionButton addview;
    com.google.android.material.floatingactionbutton.FloatingActionButton removeview;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising layout
        addview = findViewById(R.id.btnAddFromApi);
        removeview = findViewById(R.id.btnCleanBoard);
        layout = findViewById(R.id.whiteboardLayout);

        // we will click on the add view button
        addview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialising new layout
                ImageView imageView = new ImageView(MainActivity.this);

                // setting the image in the layout
                imageView.setImageResource(R.mipmap.ic_launcher);

                // calling addNewImageView with width and height
                addNewImageView(imageView, 200, 200);

                // adding the background color
                generateRandomColor(imageView);
            }
        });

        removeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
            }
        });
    }

    public void generateRandomColor(ImageView imageView) {

        // Initialising the Random();
        Random random = new Random();

        // adding the random background color
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

        // setting the background color
        imageView.setBackgroundColor(color);
    }

    private void addNewImageView(ImageView imageView, int width, int height) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);

        // setting the margin in linearlayout
        params.setMargins(0, 10, 0, 10);
        imageView.setLayoutParams(params);

        // adding the image in layout
        layout.addView(imageView);
    }


}