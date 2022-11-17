package com.example.test400;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    private RelativeLayout layout;
    private static final int pic_id = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Action buttons at the bottom of page.
        com.google.android.material.floatingactionbutton.FloatingActionButton addImageview = findViewById(R.id.btnAddFromApi);
        com.google.android.material.floatingactionbutton.FloatingActionButton removeImageview = findViewById(R.id.btnCleanBoard);
        com.google.android.material.floatingactionbutton.FloatingActionButton takeImageView = findViewById(R.id.btnCaptureImage);

        //Get RelativeLayout from activity.
        layout = findViewById(R.id.whiteboardLayout);

        // Add click listener to addview button.
        addImageview.setOnClickListener(new View.OnClickListener() {
            /***
             * On click of button, call the "GetRandomImageFromAPI click
             *
             * @param view The button clicked.
             */
            @Override
            public void onClick(View view) {
                GetRandomImageFromAPI(new ImageView(MainActivity.this));
            }
        });

        //Looks at the removeView button and adds a listener for click.
        removeImageview.setOnClickListener(new View.OnClickListener() {
            /***
             * Removes all views from the relative layout on click of button.
             *
             * @param view The button clicked.
             */
            @Override
            public void onClick(View view) {
                layout.removeAllViews();
            }
        });

        takeImageView.setOnClickListener(new View.OnClickListener() {
            /**
             * Listens to click on the camera button, and opens the camera on the phone.
             *
             * @param view The button clicked.
             */
            @Override
            public void onClick(View view) {
                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(camera);
            }
        });

    }

    /***
     * Gets a random image, and sets it to our imageviews setImageBitmap
     *
     * @param imageView Our imageView we want to add the image to.
     */
    private void GetRandomImageFromAPI(ImageView imageView) {
        // Initialising http client
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // Initialising request, and setting our url to local ip, with get method.
        Request request = new Request.Builder()
                .url("http://10.108.137.25:5078/Image") //Your machines local IPV4
                .method("GET", null)
                .build();

        //Calls our http client, with a new call, which is the request.
        client.newCall(request).enqueue(new Callback() {
            /***
             * Adds a exception to the stack.
             *
             * @param call a request that can be canceled.
             * @param e our exception.
             */
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            /***
             * If we get the data correctly, make the Apiresponse into string,
             * and create a new thread to communicate with the UI thread so the UI don't crash.
             *
             * @param call Our data request that can be canceled.
             * @param response the http response.
             * @throws IOException the exception to throw if error.
             */
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String apiImage = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        /***
                         * Runs the runnable ui thread and sets the imageView's value on setImageBitmap to the decoded string.
                         */
                        @Override
                        public void run() {
                            byte[] decodedString = Base64.decode(apiImage, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageView.setImageBitmap(decodedByte);
                        }
                    });
                }
            }
        });

        // calling AddNewImageView with width and height
        AddNewImageView(imageView, 400, 400);
    }

    /***
     * Adds the new imageView to the layout.
     *
     * @param imageView The created imageView we want to add to the layout.
     * @param width The width of the imageView.
     * @param height The height of the imageview.
     */
    private void AddNewImageView(ImageView imageView, int width, int height) {
        Random random = new Random();

        //Creates new instance of layout parameters.
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

        // setting the margin in linearlayout
        params.setMargins(random.nextInt(700), random.nextInt(1000), 0, 0);
        imageView.setLayoutParams(params);

        /** image dragable function */
//        imageView.setOnTouchListener();

        // adding the image in layout
        layout.addView(imageView);
    }
}