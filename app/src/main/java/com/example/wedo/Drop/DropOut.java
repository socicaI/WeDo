package com.example.wedo.Drop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Group.MainCategoryActivity;
import com.example.wedo.Login.LoginActivity;
import com.example.wedo.R;
import com.example.wedo.Schedule.LeaveGroupRequest;
import com.example.wedo.Schedule.ResultActivity;

public class DropOut extends AppCompatActivity {

    String userID, userPass, profileUri, nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_out);

        Intent intent = getIntent();
        nick = intent.getStringExtra("nick");
        userID = intent.getStringExtra("userID");
        userPass = intent.getStringExtra("userPass");
        profileUri = intent.getStringExtra("profileUri");
        System.out.println("userEmail: " + userPass);

        drop_out();
    }

    public void drop_out() {
        EditText password_check = (EditText) findViewById(R.id.password_check);
        Button drop_out_btn = (Button) findViewById(R.id.drop_out_btn);

        drop_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password_check.getText().toString().equals(userPass)) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(DropOut.this, "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            SharedPreferences pref = getSharedPreferences("loginInfo", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.clear();
                            editor.commit();
                            startActivity(intent);
                            finish();
                        }
                    };
                    DropOutRequest DropOutRequest = new DropOutRequest(nick, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(DropOut.this);
                    queue.add(DropOutRequest);
                } else {
                    Toast.makeText(DropOut.this, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}