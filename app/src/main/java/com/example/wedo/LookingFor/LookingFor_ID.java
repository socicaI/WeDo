package com.example.wedo.LookingFor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Login.LoginActivity;
import com.example.wedo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LookingFor_ID extends AppCompatActivity {

    private EditText looking_for_email_input;  //찾으려는 아이디의 이메일 입력란
    private Button looking_for_email_button;    //이메일 전송 버튼
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looking_for__i_d);

        looking_for_email_button = findViewById(R.id.looking_for_email_button);
        looking_for_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                looking_for_email_input = findViewById(R.id.looking_for_email_input);
                String email = looking_for_email_input.getText().toString(); //회원가입 아이디
       /*
       가입이 되어있는 email인지 판별해주는 메소드이고, 가입되어 있지 않은 email일 경우 가입이 되게 구현함
       */
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LookingFor_ID.this);
                            String email2 = looking_for_email_input.getText().toString(); //회원가입 아이디
                            String auth = "true";
                            Log.d("email2", email2);
                            if (success) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success2 = jsonResponse.getBoolean("success2");
                                            Log.d("success2", String.valueOf(success2));
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(LookingFor_ID.this);
                                            if (success2) {
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        dialog = builder2.setMessage("회원의 아이디가 메일로 전송되었습니다.")
                                                                .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent(LookingFor_ID.this, LoginActivity.class);
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                .create();
                                                        dialog.show();
                                                    }
                                                };
                                                IDEmailRequest IDEmailRequest = new IDEmailRequest(email2, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(LookingFor_ID.this);
                                                queue.add(IDEmailRequest);
                                            } else { //특정 이메일이 false일 때
                                                dialog = builder2.setMessage("인증되지 않은 이메일입니다.")
                                                        .setNegativeButton("확인", null)
                                                        .create();
                                                dialog.show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                LookingForIDRequest2 LookingForIDRequest2 = new LookingForIDRequest2(email2, auth, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(LookingFor_ID.this);
                                queue.add(LookingForIDRequest2);
                            } else {
                                dialog = builder.setMessage("가입된 이메일이 아닙니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LookingForIDRequest LookingForIDRequest = new LookingForIDRequest(email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LookingFor_ID.this);
                queue.add(LookingForIDRequest);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}