package com.example.assignment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.assignment.ChatMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ChatMessage>> getChatMessages() {
        return chatMessages;
    }

    public void sendMessage(String message) {
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages != null) {
            currentMessages.add(new ChatMessage(Participant.USER, message,false));
            chatMessages.setValue(currentMessages);
        }
    }

    public void sendMessageToGermini(String message) {
        String apiKey = BuildConfig.WAIFU_KEY;

        // 使用 API 密钥调用 Germini API
        // 示例代码：假设您有一个 HttpClient 来发送请求
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String jsonBody = "{\"message\":\"" + message + "\"}";

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url("https://api.germini.com/chat")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // handle the response
                    List<ChatMessage> currentMessages = chatMessages.getValue();
                    if (currentMessages != null) {
                        currentMessages.add(new ChatMessage(Participant.MODEL, responseBody, false));
                        chatMessages.postValue(currentMessages);
                    }
                }
            }
        });    }
}