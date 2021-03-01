package com.example.wedo.Chating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Chating.model.MessageData;
import com.example.wedo.Chating.model.RoomData;
import com.example.wedo.Chating.retrofit.RetrofitClient;
import com.example.wedo.Login.LoginActivity;
import com.example.wedo.R;
import com.example.wedo.Register.CameraActivity;
import com.example.wedo.Register.RegisterActivity;
import com.example.wedo.RegisterHttp.RegisterRequest;
import com.example.wedo.Schedule.ResultActivity;
import com.example.wedo.SearchFilter.ItemAdapter;
import com.example.wedo.SearchFilter.ItemModel;
import com.example.wedo.SearchFilter.SearchRequest;
import com.example.wedo.SearchFilter.UserSearchActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    String picture;

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

        chatServerLoad();
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
        ImageButton image_btn = (ImageButton) findViewById(R.id.image_btn);
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent imageIntent = new Intent(Intent.ACTION_PICK);
//                imageIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(imageIntent, SELECT_IMAGE);
                myStartActivity(ChatCameraActivity.class);
            }
        });

        mSocket.connect();

        mSocket.on(Socket.EVENT_CONNECT, args -> {
            mSocket.emit("enter", gson.toJson(new RoomData(username, roomNumber + "of" + orderNick, orderNick)));
        });
        mSocket.on("update", args -> {
            MessageData data = gson.fromJson(args[0].toString(), MessageData.class);
            addChat(data);
        });
    }

    /**
     * 서버에서 채팅 데이터 불러옴
     */
    private void chatServerLoad() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray("chatData");
                    for (int i = 0; i < list.length(); i++) {
                        Long time = Long.valueOf(list.getJSONObject(i).getString("date"));
                        if (list.getJSONObject(i).getString("user_name").equals(username) && list.getJSONObject(i).getString("user_profile").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber + "of" + orderNick)) {
                            adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_content"), toDate(time), ChatType.RIGHT_MESSAGE));
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        } else if (list.getJSONObject(i).getString("user_name") != username && list.getJSONObject(i).getString("user_profile").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber + "of" + orderNick)) {
                            adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_content"), toDate(time), ChatType.LEFT_MESSAGE));
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        } else if (list.getJSONObject(i).getString("user_name").equals(username) && list.getJSONObject(i).getString("user_content").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber + "of" + orderNick)) {
                            adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_profile"), toDate(time), ChatType.RIGHT_IMAGE));
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }else if (list.getJSONObject(i).getString("user_name")!=(username) && list.getJSONObject(i).getString("user_content").equals("null") && list.getJSONObject(i).getString("user_group").equals(roomNumber + "of" + orderNick)) {
                            adapter.addItem(new ChatItem(username, list.getJSONObject(i).getString("user_profile"), toDate(time), ChatType.LEFT_IMAGE));
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ChatDataRequest ChatDataRequest = new ChatDataRequest(roomNumber + "of" + orderNick, responseListener);
        RequestQueue queue = Volley.newRequestQueue(InviteesChating.this);
        queue.add(ChatDataRequest);
    }

    private void addChat(MessageData data) {
        runOnUiThread(() -> {
            System.out.println("값값값: "+data.getType());

//            if (data.getType().equals("ENTER") || data.getType().equals("LEFT")) {
//                System.out.println("값값값값값값: "+data.getType());
//                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.CENTER_MESSAGE));
//                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//            }
            if (data.getType().equals("IMAGE")) {
                System.out.println("값값: "+data.getType());
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_IMAGE));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            } else {
                System.out.println("값: "+data.getType());
                adapter.addItem(new ChatItem(data.getFrom(), data.getContent(), toDate(data.getSendTime()), ChatType.LEFT_MESSAGE));
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    private void sendMessage() {
        content_edit = (EditText) findViewById(R.id.content_edit);
        Bundle extras = getIntent().getExtras();
        roomNumber = extras.getString("id");    //주제
        username = extras.getString("nick");

        mSocket.emit("newMessage", gson.toJson(new MessageData("MESSAGE",
                username,
                roomNumber + "of" + orderNick,
                content_edit.getText().toString(),
                System.currentTimeMillis())));

        adapter.addItem(new ChatItem(username, content_edit.getText().toString(), toDate(System.currentTimeMillis()), ChatType.RIGHT_MESSAGE));
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        content_edit.setText("");
    }

//    private void sendImage(String imageUri) {
//        mSocket.emit("newImage", gson.toJson(new MessageData("IMAGE",
//                username,
//                roomNumber,
//                imageUri,
//                System.currentTimeMillis())));
//
//        adapter.addItem(new ChatItem(username, String.valueOf(imageUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
//        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//    }

    private String toDate(long currentMillis) {
        return new SimpleDateFormat("hh:mm a").format(new Date(currentMillis));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.emit("left", gson.toJson(new RoomData(username, roomNumber + "of" + orderNick, orderNick)));
        mSocket.disconnect();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    picture = data.getStringExtra("picturePath");

                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageRef = storage.getReference();
                    final StorageReference mountainImagesRef = storageRef.child("images/" + "chat/" + System.currentTimeMillis()); //파일 경로 images/사용자 ID/
                    try {
                        InputStream stream = new FileInputStream(new File(picture));
                        UploadTask uploadTask = mountainImagesRef.putStream(stream);
                        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return mountainImagesRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult(); //이미지 경로를 사용자 DB에 저장을 하여 특정 사용자의 프로필을 저장하고 불러온다.
                                    mSocket.emit("newImage", gson.toJson(new MessageData("IMAGE",
                                            username,
                                            roomNumber + "of" + orderNick,
                                            String.valueOf(downloadUri),
                                            System.currentTimeMillis())));
                                    adapter.addItem(new ChatItem(username, String.valueOf(downloadUri), toDate(System.currentTimeMillis()), ChatType.RIGHT_IMAGE));
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                } else {
                                    Log.e("로그", "실패");
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        Log.e("로그", "에러:" + e.toString());
                    }
                }
            }
            break;
        }
    }
}