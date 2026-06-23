package com.example.satori_by_aristo.AI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.satori_by_aristo.Principal;
import com.example.satori_by_aristo.R;

public class AI extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_PERM = 1;

    private EditText editTextTranscription;
    private Button btnMic, btnSend;
    private ImageButton btnBack;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isListening = false;
    private GeminiService geminiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        editTextTranscription = findViewById(R.id.editTextTranscription);
        btnMic = findViewById(R.id.btnMic);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewChat);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        geminiService = new GeminiService("");

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            setupSpeechIntent();
            setupSpeechListener();
        }

        btnMic.setOnClickListener(v -> checkPermissionAndExecute());

        btnSend.setOnClickListener(v -> {
            String text = editTextTranscription.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text, ChatMessage.TYPE_USER);
                editTextTranscription.setText("");
                int posicionPensando = messageList.size();
                sendMessage("Pensando...", ChatMessage.TYPE_BOT);
                geminiService.sendPrompt(text, new GeminiService.GeminiResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        messageList.remove(posicionPensando);
                        chatAdapter.notifyItemRemoved(posicionPensando);
                        sendMessage(response, ChatMessage.TYPE_BOT);
                    }
                    @Override
                    public void onError(String error) {
                        messageList.remove(posicionPensando);
                        chatAdapter.notifyItemRemoved(posicionPensando);
                        sendMessage("Lo siento, hubo un problema: " + error, ChatMessage.TYPE_BOT);
                    }
                });
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(AI.this, Principal.class);
            intent.putExtra("open_fragment", "perfil");
            startActivity(intent);
            finish();
        });
    }

    private void sendMessage(String text, int type) {
        messageList.add(new ChatMessage(text, type));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void setupSpeechIntent() {
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
    }

    private void setupSpeechListener() {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { btnMic.setText("🔴"); }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() {
                btnMic.setText("Hablar");
                isListening = false;
            }
            @Override
            public void onError(int error) {
                btnMic.setText("Hablar");
                isListening = false;
            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    editTextTranscription.setText(data.get(0));
                }
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void checkPermissionAndExecute() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_SPEECH_PERM);
        } else {
            toggleListening();
        }
    }

    private void toggleListening() {
        if (!isListening) {
            speechRecognizer.startListening(speechRecognizerIntent);
            isListening = true;
        } else {
            speechRecognizer.stopListening();
            isListening = false;
            btnMic.setText("Hablar");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) speechRecognizer.destroy();
    }
}
