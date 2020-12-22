package com.example.wedo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LookingForPass extends AppCompatActivity {

    private EditText looking_for_email_input, looking_for_id_input;  //찾으려는 아이디의 이메일 입력란, 아이디 입력란
    private Button looking_for_pass_button;    //이메일 전송 버튼
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looking_for_pass);

        looking_for_pass_button = findViewById(R.id.looking_for_pass_button);
        looking_for_pass_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                looking_for_id_input = findViewById(R.id.looking_for_id_input);
                looking_for_email_input = findViewById(R.id.looking_for_email_input);
                String email = looking_for_email_input.getText().toString(); //회원가입 아이디
       /*
       가입이 되어있는 email인지 판별해주는 메소드
       */
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LookingForPass.this);
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
                                            String id2 = looking_for_id_input.getText().toString();
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(LookingForPass.this);
                                            /**********************************************************************************/
                                            if (success2) {
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success3 = jsonResponse.getBoolean("success3");
                                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(LookingForPass.this);
                                                            if (success3) {
                                                                //메일 쏘는 곳
                                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(LookingForPass.this); //회원가입 화면에 문구를 다이얼로그로 띄어주기 위해 선언
                                                                        dialog = builder.setMessage("전송된 메일을 통해 비밀번호를 변경해주세요.")
                                                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                                        startActivity(intent);
                                                                                        finish();
                                                                                    }
                                                                                })
                                                                                .create();
                                                                        dialog.show(); //다이얼로그 실행
                                                                    }
                                                                };
                                                                EmailPassRequest EmailPassRequest = new EmailPassRequest(email2, responseListener);
                                                                RequestQueue queue = Volley.newRequestQueue(LookingForPass.this);
                                                                queue.add(EmailPassRequest);
                                                            } else { //특정 이메일이 false일 때
                                                                dialog = builder2.setMessage("이메일 또는 아이디가 틀립니다.")
                                                                        .setNegativeButton("확인", null)
                                                                        .create();
                                                                dialog.show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };
                                                LookingForPassRequest LookingForPassRequest = new LookingForPassRequest(id2, email, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(LookingForPass.this);
                                                queue.add(LookingForPassRequest);
                                            } else { //특정 이메일이 false일 때
                                                /********************************************************************************/
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
                                RequestQueue queue = Volley.newRequestQueue(LookingForPass.this);
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
                RequestQueue queue = Volley.newRequestQueue(LookingForPass.this);
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