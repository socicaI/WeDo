package com.example.wedo.Schedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.wedo.R;

public class Loading extends Activity {

    public String id, nick, profilePath, userEmail, userID, userPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //그룹명
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");


        setContentView(R.layout.activity_loading);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 1000); // 1초 후에 hd handler 실행  3000ms = 3초

    }

    private class splashhandler implements Runnable {
        public void run() {

            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("nick", nick);
            intent.putExtra("profilePath", profilePath);
            intent.putExtra("userID", userID);
            intent.putExtra("userPass", userPass);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
            finish();
            //로딩이 끝난 후, ChoiceFunction 이동


            Loading.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}

