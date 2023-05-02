package com.example.chatgptintegration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private EditText mEditText;
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    private final String accessToken = "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\n";
    private List< Message > mMessages;
    private LinearLayout mProgressBar;
    private TextToSpeech tts;
    private FloatingActionButton mButton, mSpeech, mClear;

    public void scrollToBottom() {
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1);
    }

    public void startProgressWheel() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void stopProgressWheel() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mTextView = findViewById(R.id.hintText);
        mTextView.setText(R.string.hint_text);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.button_return);
        mClear = findViewById(R.id.btn_clear);
        mProgressBar = findViewById(R.id.progressBar);
        ImageButton mImageButton = findViewById(R.id.send_btn);
        mSpeech = findViewById(R.id.btn_speak);
        ImageButton mMic = findViewById(R.id.mic_btn);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not Supported!");
                    }
                } else {
                    Log.e("TTS", "Initialization Failed");
                }
            }
        });

        mMessages = new ArrayList< >();
        mAdapter = new MessageAdapter(mMessages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager mManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mManager);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mRecyclerView, null, mAdapter.getItemCount());
                mClear.setVisibility(View.VISIBLE);
                mSpeech.setVisibility(View.VISIBLE);
                mTextView.setVisibility(View.GONE);
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                if(!rv.canScrollVertically(1) && rv.canScrollVertically(-1)) {
                    mButton.setVisibility(View.VISIBLE);
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToBottom();
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessages.clear();
                mAdapter.notifyDataSetChanged();
                mTextView.setVisibility(View.VISIBLE);
                mButton.setVisibility(View.GONE);
                mClear.setVisibility(View.GONE);
                mSpeech.setVisibility(View.GONE);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAPI();
                hideKeyboard(view);
            }
        });

        mSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tts.isSpeaking()) {
                    tts.shutdown();
                    mSpeech.setImageResource(R.drawable.speech);
                    return;
                }
                String text = obtainLastText();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "test");

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        mSpeech.setImageResource(R.drawable.mute_btn);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        mSpeech.setImageResource(R.drawable.speech);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Toast.makeText(getApplicationContext(), "An Error Ocurred while recognizing!", Toast.LENGTH_SHORT).show();
                        mSpeech.setImageResource(R.drawable.speech);
                    }
                });
            }
        });

        mMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                try {
                    startActivityIfNeeded(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Your device does not support speech to text", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if(resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mEditText.setText(result.get(0));
                callAPI();
            }
        }
    }
    private void callAPI() {
        String text = mEditText.getText().toString().trim();
        if(text.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Enter some text to send", Toast.LENGTH_SHORT).show();
            return;
        }
        mMessages.add(new Message(text, true));
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        mEditText.getText().clear();
        JSONObject requestBody = new JSONObject();
        startProgressWheel();
        mSpeech.setVisibility(View.GONE);
        try {
            requestBody.put("model", "gpt-3.5-turbo");
            JSONArray msgArray = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role", "user");
            obj.put("content", text);
            msgArray.put(obj);

            requestBody.put("messages", msgArray);

        } catch (JSONException e) {
            stopProgressWheel();
            mSpeech.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody, new Response.Listener < JSONObject > () {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray choicesArray = response.getJSONArray("choices");
                    String text = choicesArray.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    Log.e("API Response", response.toString());
                    //Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                    stopProgressWheel();
                    mSpeech.setVisibility(View.VISIBLE);
                    mMessages.add(new Message(text.replaceFirst("\n", "").replaceFirst("\n", ""), false));
                    mAdapter.notifyItemInserted(mMessages.size() - 1);
                } catch (JSONException e) {
                    stopProgressWheel();
                    mSpeech.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API Error", error.toString());
                stopProgressWheel();
                mSpeech.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map< String, String > getHeaders() {
                Map < String, String > headers = new HashMap< >();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
            @Override
            protected Response < JSONObject > parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        int timeoutMs = 25000; // 25 seconds timeout
        RetryPolicy policy = new DefaultRetryPolicy(timeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        // Add the request to the RequestQueue
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void hideKeyboard(@NonNull View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
    }

    private String obtainLastText() {
        LinearLayout layout = (LinearLayout) mRecyclerView.getChildAt(mAdapter.getItemCount()-1);
        TextView tv = layout.findViewById(R.id.text_message_bot);
        return tv.getText().toString();
    }
}