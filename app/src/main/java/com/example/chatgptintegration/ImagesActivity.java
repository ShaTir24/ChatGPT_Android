package com.example.chatgptintegration;

import static java.lang.System.out;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImagesActivity extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 201;
    private final int CAMERA_REQ_CODE = 101;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        MaterialButton captureBtn = findViewById(R.id.capture_btn);
        MaterialButton uploadBtn = findViewById(R.id.upload_btn);
        MaterialButton submitBtn = findViewById(R.id.submit_img_btn);
        iv = findViewById(R.id.img_view);

        captureBtn.setOnClickListener(v -> {
            Intent iCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(iCam, CAMERA_REQ_CODE);
        });

        uploadBtn.setOnClickListener(v -> {
            Intent iGal = new Intent(Intent.ACTION_PICK);
            iGal.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGal, GALLERY_REQ_CODE);
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the bitmap of an image from imageView
                Bitmap bitImg = ((BitmapDrawable)iv.getDrawable()).getBitmap();

                //file path
                String filePath = getApplicationContext().getFilesDir().getPath() +"/image.jpg";

                //creating a file in internal storage directory
                File imgFile = new File(filePath);

                try {
                    //creating output stream to write the bitmap image to the file
                    FileOutputStream outputStream = new FileOutputStream(imgFile);
                    bitImg.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                    if(imgFile.exists()) {
                        Log.d("File saved at", filePath);
                    } else {
                        Log.d("File not Saved at", filePath);
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQ_CODE) {
                //for camera image picking
                Bitmap img = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(img);
            } else if (requestCode == GALLERY_REQ_CODE) {
                //for gallery image picking
                iv.setImageURI(data.getData());
            }
        }
    }
}