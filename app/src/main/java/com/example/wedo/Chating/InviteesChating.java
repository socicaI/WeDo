package com.example.wedo.Chating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.wedo.Chating.model.MessageData;
import com.example.wedo.Chating.model.RoomData;
import com.example.wedo.R;
import com.example.wedo.Schedule.ResultActivity;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;

public class InviteesChating extends AppCompatActivity {

    /**
     * 채팅
     */
    private Socket mSocket;
//    private RetrofitClient retrofitClient;

    private ChatAdapter adapter;

    private RecyclerView recyclerView;
    private EditText content_edit;

    private Gson gson = new Gson();

    private int SELECT_IMAGE = 100;

    /**
     * Schedule 관련 필요한 데이터
     */
    public String roomNumber, username, profilePath, userEmail, userID, userPass, orderNick;    //roomNumber=주제, nick=사용자 이름, orderNick=주제주인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitees_chating);

        Bundle extras = getIntent().getExtras();
        roomNumber = extras.getString("id");    //주제
        username = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        orderNick = extras.getString("orderNick");
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("id", roomNumber);
        intent.putExtra("nick", username);
        intent.putExtra("orderNick", orderNick);
        intent.putExtra("profilePath", profilePath);
        intent.putExtra("userID", userID);
        intent.putExtra("userPass", userPass);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
        finish();
    }

    /**
     * 채팅
     */
    private void init() {
        try {
            mSocket = IO.socket("http://54.180.0.255:3333");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        retrofitClient = RetrofitClient.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        adapter = new ChatAdapter(getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //메시지 전송 버튼
        ImageButton send_btn = (ImageButton) findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //이미지 전송 버튼

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            mSocket.emit("enter", gson.toJson(new RoomData(username, roomNumber+"of"+orderNick, orderNick)));
        });
        mSocket.on("update", args -> {
            MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
            addChat(data);
        });
    }

    private void sendMessage() {
        content_edit = (EditText) findViewById(R.id.content_edit);
        Bundle extras = getIntent().getExtras();
        roomNumber = extras.getString("id");    //주제
        username = extras.getString("nick");

        mSocket.emit("newMessage", gson.toJson(new MessageData("MESSAGE",
                username,
                roomNumber+"of"+orderNick,
                content_edit.getText().toString(),
                System.currentTimeMillis())));

        adapter.addItem(new ChatItem(username, content_edit.getText().toString(), toDate(System.currentTimeMillis()), ChatType.RIGHT_MESSAGE));
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        content_edit.setText("");
    }

    private void addChat(MessageData data) {
        runOnUiThread(() -> {
            if (data.getType().equals("ENTER") || data.getType().equals("LEFT")) {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.CENTER_MESSAGE));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else if (data.getType().equals("IMAGE")) {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_IMAGE));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else {
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_MESSAGE));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private String toDate(long currentMillis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMillis));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
//            uploadImage(selectedImageUri, getApplicationContext());
            adapter.addItem(new ChatItem(username, String.valueOf(selectedImageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("left", gson.toJson(new RoomData(username, roomNumber+"of"+orderNick,orderNick)));
        mSocket.disconnect();
    }
}