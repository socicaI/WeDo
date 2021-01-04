package com.example.wedo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class Main2Activity extends AppCompatActivity {
    String strID;
    String profilePath; //프로필 Uri 경로
    private long backKeyPressedTime = 0; //뒤로가기 2번 클릭 시 종료하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView ID = findViewById(R.id.id);
        ImageView profile = findViewById(R.id.profileImageView);
        Button btnLogout = findViewById(R.id.btnLogout);
        Intent intent = getIntent();
        strID = intent.getStringExtra("ID");
        profilePath = intent.getStringExtra("profileUri");  //사용자 프로필 경로를 받아온다.

        /**
         * 프로필 불러오는 부분
         */
        Uri uri = Uri.parse(profilePath);
        Glide.with(getApplicationContext())
                .load(uri)
                .into(profile);

        ID.setText("'"+strID+"'님 어서오세요");
        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finish();
        }
    }
}