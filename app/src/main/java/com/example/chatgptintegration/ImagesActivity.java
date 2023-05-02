package com.example.chatgptintegration;

import static java.lang.System.out;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImagesActivity extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 201;
    private final int CAMERA_REQ_CODE = 101;
    private int count_stored = 0;
    private long id;
    ImageView iv1, iv2;
    MaterialButton submitBtn, viewBtn, clearBtn;
    MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        MaterialButton captureBtn = findViewById(R.id.capture_btn);
        MaterialButton uploadBtn = findViewById(R.id.upload_btn);
        submitBtn = findViewById(R.id.submit_img_btn);
        viewBtn = findViewById(R.id.view_img_btn);
        clearBtn = findViewById(R.id.reset_img_btn);
        iv1 = findViewById(R.id.img_view);
        iv2 = findViewById(R.id.res_img_view);

        dbHelper = new MyDatabaseHelper(this);

        captureBtn.setOnClickListener(v -> {
            Intent iCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(iCam, CAMERA_REQ_CODE);
        });

        uploadBtn.setOnClickListener(v -> {
            Intent iGal = new Intent(Intent.ACTION_PICK);
            iGal.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGal, GALLERY_REQ_CODE);
        });

        iv1.setOnClickListener(v -> {
            Intent iGal = new Intent(Intent.ACTION_PICK);
            iGal.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(iGal, GALLERY_REQ_CODE);
        });

        submitBtn.setOnClickListener(v -> {
            //getting the bitmap of an image from imageView
            Bitmap bitImg = ((BitmapDrawable)iv1.getDrawable()).getBitmap();

            //compressing bitmap image into a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitImg.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imgBytes = outputStream.toByteArray();

            //inserting image byte array into database
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
                ContentValues values = new ContentValues();
                values.put("name", "Tirth");
                values.put("image_data", imgBytes);
                id = db.insertOrThrow("my_table", null, values);
                count_stored++;
                Log.d("TAG", "Image Inserted Into Database");
                Toast.makeText(getApplicationContext(), "Image Inserted Into Database", Toast.LENGTH_SHORT).show();
                viewBtn.setEnabled(true);
                clearBtn.setEnabled(true);
                submitBtn.setEnabled(false);
                Toast.makeText(this, String.valueOf(count_stored), Toast.LENGTH_SHORT).show();
            } catch (SQLiteException e) {
                Log.e("TAG", "Error inserting data into database", e);
                Toast.makeText(getApplicationContext(), "Error Occurred while inserting image into database", Toast.LENGTH_SHORT).show();
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] imageData = getImageDataFromDB();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                iv2.setImageBitmap(bitmap);
                iv2.setVisibility(View.VISIBLE);
                viewBtn.setEnabled(false);
            }

            private byte[] getImageDataFromDB() {
                byte[] byteArray = {0};
                try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
                    String query = "SELECT image_data FROM my_table WHERE _id = ?";
                    Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
                    if(cursor.moveToFirst()) {
                        byteArray = cursor.getBlob(cursor.getColumnIndexOrThrow("image_data"));
                    }
                    cursor.close();
                    return byteArray;

                } catch(SQLiteException e) {
                    Toast.makeText(ImagesActivity.this, "Error in Fetching image from Database", Toast.LENGTH_SHORT).show();
                    Log.e("TAG", "Error Fetching the Image Byte Array from Database");
                }
                return byteArray;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQ_CODE) {
                //for camera image picking
                Bitmap img = (Bitmap) data.getExtras().get("data");
                iv1.setImageBitmap(img);
            } else if (requestCode == GALLERY_REQ_CODE) {
                //for gallery image picking
                iv1.setImageURI(data.getData());
            }
            //imgBool = true;
            submitBtn.setEnabled(true);
        }
    }
}