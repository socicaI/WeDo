package com.example.wedo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GmailSetNickActivity extends AppCompatActivity {

    boolean profile=false, nickValidate=false;
    Button nickValidateButton, mainButton;
    String nick, profileUri;
    EditText setNickInput;
    TextView nickRedundancyCheckText, profileImageText;

    /**
     * 프로필 사진
     */
    ImageView profileImageView;
    String profilePath, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nick_gmail);

        setNickInput = findViewById(R.id.setNickInput); //setNickInput=닉네임 입력란
        nickRedundancyCheckText = findViewById(R.id.nickRedundancyCheckText);
        mainButton = findViewById(R.id.mainButton);
        profileImageText = findViewById(R.id.profileImageText);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userEmail");
        Log.e("로그12345",userId);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profile){
                    if(nickValidate){
                        ProgressDialog dialog1 = ProgressDialog.show(GmailSetNickActivity.this, "",
                                "WeDo-잠시만 기다려주세요 :)", true);
                        dialog1.onStart();
                        FirebaseStorage storage = FirebaseStorage.getInstance();

                        StorageReference storageRef = storage.getReference();
                        final StorageReference mountainImagesRef = storageRef.child("images/"+nick+"/profile.jpg"); //파일 경로 images/사용자 ID/
                        try {
                            InputStream stream = new FileInputStream(new File(profilePath));
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
                                        profileUri = String.valueOf(downloadUri);

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(getApplicationContext(), "환영합니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(GmailSetNickActivity.this, MainCategoryActivity.class);
                                                intent.putExtra("ID", nick);
                                                intent.putExtra("profileUri", profileUri);
                                                startActivity(intent);
                                                dialog1.dismiss();
                                                finish();
                                            }
                                        };
                                        //서버로 volley를 이용해서 요청을 함
                                        GmailNickProfile GmailNickProfile = new GmailNickProfile(userId, nick, profileUri, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(GmailSetNickActivity.this);
                                        queue.add(GmailNickProfile);
                                    } else {
                                        Log.e("로그", "실패");
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러:" + e.toString());
                        }
                    }
                    else {
                        Toast.makeText(GmailSetNickActivity.this, "사용할 이름을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(GmailSetNickActivity.this, "프로필 사진을 찍어주세요.", Toast.LENGTH_SHORT).show();
                    profileImageText.setText("프로필 사진을 찍어주세요.");
                    profileImageText.setTextColor(Color.parseColor("#FF0000"));
                }
            }
        });

        /**
         * 프로필 사진
         */
        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(CameraActivity.class);
            }
        });

        nickValidateButton = findViewById(R.id.nickValidateButton);
        nickValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = setNickInput.getText().toString();
                if (nick.equals("")) {
                    nickValidate = false;
                    nickRedundancyCheckText.setText("사용하실 이름을 입력해주세요.");
                    nickRedundancyCheckText.setTextColor(Color.parseColor("#FF0000"));
                    return; //return을 통해 사용자가 값을 다시 입력하기 위해 되돌린다.
                }

                /*
                사용할 이름 입력란에 입력 되어 있는 상태에서 사용할 수 있는 이름인지 사용할 수 없는 이름인지 판별해주는 메소드
                 */
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            AlertDialog.Builder builder = new AlertDialog.Builder(GmailSetNickActivity.this);
                            if (success) {
                                nickValidate = true;
                                nickRedundancyCheckText.setText("사용 가능한 이름입니다.");
                                nickRedundancyCheckText.setTextColor(Color.parseColor("#3498DB"));
                            } else {
                                nickValidate = false;
                                nickRedundancyCheckText.setText("중복된 이름입니다.");
                                nickRedundancyCheckText.setTextColor(Color.parseColor("#FF0000"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                NickValidateRequest NickValidateRequest = new NickValidateRequest(nick, responseListener);
                RequestQueue queue = Volley.newRequestQueue(GmailSetNickActivity.this);
                queue.add(NickValidateRequest);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(GmailSetNickActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그", "profilePath: " + profilePath);
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(90);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    Bitmap rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), rotateMatrix, true); //사진 회전하는 부분
                    profileImageView.setImageBitmap(rotateBitmap);
                    profile = true;
                    profileImageText.setText("");
                }
            }
            break;
        }
    }
}