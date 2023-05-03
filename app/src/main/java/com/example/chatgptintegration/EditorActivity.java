package com.example.chatgptintegration;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EditorActivity extends AppCompatActivity {

    EditText textArea;
    private final String filePath = Environment.DIRECTORY_DOCUMENTS;
    private float textSize;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        textArea = findViewById(R.id.editTextTextMultiLine);
        FloatingActionButton bold = findViewById(R.id.button_bold);
        FloatingActionButton italics = findViewById(R.id.button_italics);
        FloatingActionButton underline = findViewById(R.id.button_underline);
        FloatingActionButton incFont = findViewById(R.id.font_inc_btn);
        FloatingActionButton decFont = findViewById(R.id.font_dec_btn);
        FloatingActionButton leftAlign = findViewById(R.id.left_aln_btn);
        FloatingActionButton centerAlign = findViewById(R.id.center_aln_btn);
        FloatingActionButton rightAlign = findViewById(R.id.right_aln_btn);
        Button clear = findViewById(R.id.clearButton);
        Button generatePdf = findViewById(R.id.pdfButton);

        bold.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd(),
                    0);
            textArea.setText(spannableString);
        });

        italics.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            spannableString.setSpan(new StyleSpan(Typeface.ITALIC),
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd(),
                    0);
            textArea.setText(spannableString);
        });

        underline.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            spannableString.setSpan(new UnderlineSpan(),
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd(),
                    0);
            textArea.setText(spannableString);
        });

        incFont.setOnClickListener(v -> {
            textSize = textArea.getTextSize();
            float newSize = textSize + 2;
            textArea.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
        });

        decFont.setOnClickListener(v -> {
            textSize = textArea.getTextSize();
            float newSize = textSize - 2;
            textArea.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
        });

        leftAlign.setOnClickListener(v -> {
            textArea.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            //to update the changes on btn click
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
        });

        centerAlign.setOnClickListener(v -> {
            textArea.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //to update the changes on btn click
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
        });

        rightAlign.setOnClickListener(v -> {
            textArea.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            //to update the changes on btn click
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
        });

        clear.setOnClickListener(v -> {
            String stringText = textArea.getText().toString();
            textArea.setText(stringText);
        });

        generatePdf.setOnClickListener(v -> {
            try {
                Spannable spannableString = new SpannableStringBuilder(textArea.getText());
                String text = spannableString.toString();
                byte[] byteArray = text.getBytes();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "document1.pdf");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath);

                ContentResolver contentResolver = getContentResolver();
                Uri uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), values);
                OutputStream outputStream = contentResolver.openOutputStream(uri);
                outputStream.write(byteArray);
                outputStream.close();
                Toast.makeText(this, "PDF file saved at path: " + filePath, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.d("TAG", String.valueOf(e));
                Toast.makeText(this, "Error ocurred saving the file", Toast.LENGTH_SHORT).show();
            }
        });
    }
}