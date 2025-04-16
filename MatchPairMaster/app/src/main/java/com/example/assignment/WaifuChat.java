package com.example.assignment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment.R;
import com.example.assignment.ChatAdapter;
import com.example.assignment.ChatMessage;
import com.example.assignment.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class WaifuChat extends AppCompatActivity {

    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waifu_chat);

        // Initialize ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize Views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        // Setup RecyclerView
        chatAdapter = new ChatAdapter(new ArrayList<>());
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        // Observe Chat Messages
        chatViewModel.getChatMessages().observe(this, chatMessages -> {
            chatAdapter.setChatMessages(chatMessages);
            chatRecyclerView.scrollToPosition(0);
        });

        // Send Button Listener
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                chatViewModel.sendMessage(message);
                messageEditText.setText("");
            }
        });
    }
}