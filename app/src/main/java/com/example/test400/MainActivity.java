package com.example.test400;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private int xDelta = 0;
    private int yDelta = 0;

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
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });
    }

    /**
     * Creates a new image to the layout and adds it to the internal storage of the device.
     *
     * @param requestCode the activity request code we send along on our take image click, to start the activity.
     * @param resultcode
     * @param data the picture to save.
     */
    protected void onActivityResult(int requestCode, int resultcode, Intent data) {
        super.onActivityResult(requestCode, resultcode, data);
        if (requestCode == pic_id){
            ImageView imageView = new ImageView(MainActivity.this);

            Bitmap photo = (Bitmap) data.getExtras().get("data");

            imageView.setImageBitmap(photo);

            SaveToInternalStorage(photo);

            AddNewImageView(imageView, 400, 400);
        }
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
                .url("http://10.108.137.16:5078/Image") //Your machines local IPV4
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

        imageView.setOnTouchListener(onTouchListener());

        // adding the image in layout
        layout.addView(imageView);
    }

    /**
     * Saves the image to the local directory on the phone.
     *
     * @param bitmapImage the image we want to save.
     * @return the absolute path of the directory.
     */
    private String SaveToInternalStorage(Bitmap bitmapImage){
        Random random = new Random();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/test400/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir with a random generated number for name.
        File mypath = new File(directory,random.nextInt(999999999) + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /**
     * When image is touched, calculate position, and get the raw x and y cordinate of where we end up when we let go of the touch agian.
     *
     * @return true value, to say that we are touhing an image.
     */
    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }

                layout.invalidate();
                return true;
            }
        };
    }
}