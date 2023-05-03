package com.example.chatgptintegration;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity {

    EditText textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        textArea = findViewById(R.id.editTextTextMultiLine);
        Button underline = findViewById(R.id.button_underline);
        underline.setPaintFlags(underline.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    protected void boldButton(View view) {
            Spannable spannableString = new SpannableStringBuilder(textArea.getText());
            spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd(),
                    0);
            textArea.setText(spannableString);
    }

    protected void italicsButton(View view) {
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC),
                textArea.getSelectionStart(),
                textArea.getSelectionEnd(),
                0);
        textArea.setText(spannableString);
    }

    protected void underlineButton(View view) {
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        spannableString.setSpan(new UnderlineSpan(),
                textArea.getSelectionStart(),
                textArea.getSelectionEnd(),
                0);
        textArea.setText(spannableString);
    }

    protected void clearButton(View view) {
        String stringText = textArea.getText().toString();
        textArea.setText(stringText);
    }

    protected void incButton(View view) {
        textArea.setTextSize(textArea.getTextSize() + 2);
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        textArea.setText(spannableString);
    }

    protected void decButton(View view) {
        textArea.setTextSize(textArea.getTextSize() - 2);
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        textArea.setText(spannableString);
    }

    protected void leftAlignButton(View view) {
        textArea.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        //to update the changes on btn click
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        textArea.setText(spannableString);
    }

    protected void centerAlignButton(View view) {
        textArea.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //to update the changes on btn click
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        textArea.setText(spannableString);
    }

    protected void rightAlignButton(View view) {
        textArea.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        //to update the changes on btn click
        Spannable spannableString = new SpannableStringBuilder(textArea.getText());
        textArea.setText(spannableString);
    }

}