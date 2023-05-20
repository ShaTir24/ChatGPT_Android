package com.example.chatgptintegration;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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
    private Paint.Align alignType = Paint.Align.LEFT;

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
            alignType = Paint.Align.LEFT;
        });

        centerAlign.setOnClickListener(v -> {
            textArea.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //to update the changes on btn click
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
            alignType = Paint.Align.CENTER;
        });

        rightAlign.setOnClickListener(v -> {
            textArea.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            //to update the changes on btn click
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            textArea.setText(spannableString);
            alignType = Paint.Align.RIGHT;
        });

        clear.setOnClickListener(v -> {
            String stringText = textArea.getText().toString();
            textArea.setText(stringText);
        });

        generatePdf.setOnClickListener(v -> {
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            // Create a paint object to set the text color and size
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(textSize);
            paint.setTextAlign(alignType);
            // Draw some text on the page
            canvas.drawText(spannableString.toString(), 10, 10, paint);
            // Finish the page
            document.finishPage(page);
            // Save the document to a file
            try {
                File file = new File(filePath, "my_document.pdf");
                FileOutputStream outputStream = new FileOutputStream(file);
                document.writeTo(outputStream);
                document.close();
                Toast.makeText(this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "File Save Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}