package com.example.chatgptintegration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImagesActivity extends AppCompatActivity {
    private final int GALLERY_REQ_CODE = 201;
    private final int CAMERA_REQ_CODE = 101;
    ImageView iv1;
    TextView outp;
    MaterialButton submitBtn, generateBtn, clearBtn;
    MyDatabaseHelper dbHelper;
    Bitmap bitImg;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MaterialButton captureBtn = findViewById(R.id.capture_btn);
        MaterialButton uploadBtn = findViewById(R.id.upload_btn);
        submitBtn = findViewById(R.id.submit_img_btn);
        generateBtn = findViewById(R.id.generate_txt_btn);
        clearBtn = findViewById(R.id.reset_img_btn);
        iv1 = findViewById(R.id.img_view);
        outp = findViewById(R.id.output_gen_txt);

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "ddlpaws9m");
        config.put("api_key", "494571888937294");
        config.put("api_secret", "rAyqgtHXGVHGSAHYli0BhxCRCms");
        Cloudinary cloudinary = new Cloudinary(config);

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
            bitImg = ((BitmapDrawable)iv1.getDrawable()).getBitmap();

            //inserting image byte array into database
            try {
                // Upload image to Cloudinary
                cloudinary.uploader().upload(bitmapToBytes(bitImg), ObjectUtils.emptyMap());
                generateBtn.setEnabled(true);
            } catch (IOException e) {
                Toast.makeText(this, "Error Occurred while storing the image into database.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Map<?, ?> uploadResult;
            try {
                uploadResult = cloudinary.uploader().upload(bitmapToBytes(bitImg), ObjectUtils.emptyMap());
                imageUrl = (String) uploadResult.get("secure_url");
                generateBtn.setEnabled(true);
                clearBtn.setEnabled(true);
            } catch (IOException e) {
                Toast.makeText(this, "Unable to get the URL of the image at this time. Please try again later.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        generateBtn.setOnClickListener(v -> {
            String url = "https://tirth24.pythonanywhere.com/";
            JSONObject reqObj = new JSONObject();
            try {
                reqObj.put("url", imageUrl);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String response = HttpUtils.sendPostRequest(url, reqObj);
            if(response != null) {
                outp.setText(response);
                outp.setMovementMethod(new ScrollingMovementMethod());
            }
        });

        clearBtn.setOnClickListener(v -> {
            try {
                cloudinary.uploader().destroy(getPublicId(imageUrl), ObjectUtils.emptyMap());
                Toast.makeText(this, "The image has been successfully removed from database.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "An error occurred while removing the image from database.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    private String getPublicId(String imageUrl) {
        //Extract the public ID from the image URL
        Uri uri = Uri.parse(imageUrl);
        List<String> segments = uri.getPathSegments();
        String publicIdWithExtension = segments.get(segments.size() - 1);
        return publicIdWithExtension.substring(0, publicIdWithExtension.lastIndexOf("."));
    }

    private byte[] bitmapToBytes(Bitmap bitImg) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
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