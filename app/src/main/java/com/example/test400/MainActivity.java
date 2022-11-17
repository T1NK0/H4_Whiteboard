package com.example.test400;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewResult;

    private com.google.android.material.floatingactionbutton.FloatingActionButton addview;
    private com.google.android.material.floatingactionbutton.FloatingActionButton removeview;
    private RelativeLayout layout;

    /***
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising layout
        addview = findViewById(R.id.btnAddFromApi);
        removeview = findViewById(R.id.btnCleanBoard);
        layout = findViewById(R.id.whiteboardLayout);

//        Response response = client.newCall(request).execute();

        addview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initialising new layout
                ImageView imageView = new ImageView(MainActivity.this);

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.108.137.25:5078/Image")
                        .method("GET", null)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String myResponse = response.body().string();

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] decodedString = Base64.decode(myResponse, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    imageView.setImageBitmap(decodedByte);
                                }
                            });
                        }
                    }
                });

                // calling addNewImageView with width and height
                addNewImageView(imageView, 400, 200);
            }
        });

        //Looks at the removeView button and adds a listener for click.
        removeview.setOnClickListener(new View.OnClickListener() {
            /***
             * Removes all views from the relative layout on click of button.
             *
             * @param view The on click listener.
             */
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
            }
        });
    }

    /***
     * Adds the new imageView to the layout.
     *
     * @param imageView The created imageView we want to add to the layout.
     * @param width The width of the imageView.
     * @param height The height of the imageview.
     */
    private void addNewImageView(ImageView imageView, int width, int height) {
        Random random = new Random();

        //Creates new instance of layout parameters.
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

        // setting the margin in linearlayout
        params.setMargins(random.nextInt(700), random.nextInt(1000), 0, 0);
        imageView.setLayoutParams(params);

        // adding the image in layout
        layout.addView(imageView);
    }
}