package com.example.chatgptintegration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardView imgCard = findViewById(R.id.img_card);
        CardView chatCard = findViewById(R.id.chat_card);

        imgCard.setOnClickListener(v -> {
            Intent iScan = new Intent(getApplicationContext(), ImagesActivity.class);
            startActivity(iScan);
        });

        chatCard.setOnClickListener(v -> {
            Intent iChat = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(iChat);
        });
    }
}