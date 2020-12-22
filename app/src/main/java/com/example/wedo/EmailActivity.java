package com.example.wedo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class EmailActivity extends AppCompatActivity {
    //registration_email_input=이메일 입력란
    private EditText registration_email_input;
    //email_validate_button=이메일 인증 버튼
    private Button registration_button;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        registration_email_input = findViewById(R.id.registration_email_input); //registration_email_input=이메일 입력란
        registration_button = findViewById(R.id.registration_button);   //email_validate_button=이메일 인증 버튼

         /*
        naver.com
        이메일 인증 버튼을 눌렀을 경우 입력한 이메일에 메일이 전송한다.
         */
        registration_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String userID = getIntent().getStringExtra("userID"); //회원가입 아이디
                String userPass = getIntent().getStringExtra("userPass"); //회원가입 비밀번호
                String Email = registration_email_input.getText().toString();
                boolean emailSuccess = false;

                if (registration_email_input.getText().toString().length() < 1) { //만약 이메일 입력란이 공백이라면 "이메일을 입력해주세요."라는 다이얼로그로 예외처리
                    AlertDialog.Builder builder = new AlertDialog.Builder(EmailActivity.this); //회원가입 화면에 문구를 다이얼로그로 띄어주기 위해 선언
                    dialog = builder.setMessage("이메일을 입력해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show(); //다이얼로그 실행
                    return; //return을 통해 사용자가 값을 다시 입력하기 위해 되돌린다.
                }
                /*
                가입이 되어있는 email인지 판별해주는 메소드이고, 가입되어 있지 않은 email일 경우 가입이 되게 구현함
                 */
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            AlertDialog.Builder builder = new AlertDialog.Builder(EmailActivity.this);
                            if (success) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                                Toast.makeText(getApplicationContext(), "회원 가입 되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        };
//                                        //서버로 volley를 이용해서 요청을 함
//                                        RegisterRequest registerRequest = new RegisterRequest(userID, userPass, Email, emailSuccess, responseListener);
//                                        RequestQueue queue = Volley.newRequestQueue(EmailActivity.this);
//                                        queue.add(registerRequest);

                                        Toast.makeText(getApplicationContext(), "이메일 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EmailActivity.this); //회원가입 화면에 문구를 다이얼로그로 띄어주기 위해 선언
                                        dialog = builder.setMessage("이메일 인증 후 서비스를 원활하게 이용하실 수 있습니다.")
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "가입에 되었습니다.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .create();
                                        dialog.show(); //다이얼로그 실행
                                    }
                                };
                                EmailRequest EmailRequest = new EmailRequest(Email, Email, Email, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(EmailActivity.this);
                                queue.add(EmailRequest);
                            } else {
                                dialog = builder.setMessage("이미 가입된 이메일입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Validate_emailRequest Validate_emailRequest = new Validate_emailRequest(Email, responseListener);
                RequestQueue queue = Volley.newRequestQueue(EmailActivity.this);
                queue.add(Validate_emailRequest);
            }
        });
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}