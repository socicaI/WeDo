package com.example.wedo.Group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.ListHttp.ValidateGmailNick;
import com.example.wedo.ListHttp.ValidateGmailNick2;
import com.example.wedo.ListHttp.ValidateKakaoNick;
import com.example.wedo.ListHttp.ValidateKakaoNick2;
import com.example.wedo.ListHttp.ValidateRegisterNick;
import com.example.wedo.ListHttp.ValidateRegisterNick2;
import com.example.wedo.R;
import com.example.wedo.RegisterHttp.NickValidateRequest;
import com.example.wedo.RegisterHttp.nickCorrectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class NickChangeActivity extends AppCompatActivity {

    EditText ID;
    TextView nickRedundancyCheckText;
    String strID, nick, userID, userPass, GoogleUserEmail, profileUri, kakaoUserEmail;
    Button nickValidateButton, nickChangeBtn;
    boolean nickValidate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_change);

        nickRedundancyCheckText = (TextView) findViewById(R.id.nickRedundancyCheckText);
        ID = (EditText) findViewById(R.id.setNickInput);

        Intent intent = getIntent();
        strID = intent.getStringExtra("nick");
        userID = intent.getStringExtra("userID");
        userPass = intent.getStringExtra("userPass");
        GoogleUserEmail = intent.getStringExtra("GoogleUserEmail");
        profileUri = intent.getStringExtra("profileUri");
        kakaoUserEmail = intent.getStringExtra("userEmail");

        ID.setHint(strID);

        /**
         * 사용자 이름 변경 구간
         */
        nickChangeBtn = (Button) findViewById(R.id.nickChangeBtn);
        nickChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nickValidate) {
                    /**
                     * Register
                     */
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success1 = jsonResponse.getBoolean("success1");
                                Log.e("성공1", String.valueOf(success1));
                                if (success1) { //Register로 가입한 사용자
                                    Log.e("성공2", String.valueOf(success1));
                                    Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Toast.makeText(NickChangeActivity.this, "사용자 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonResponse1 = new JSONObject(response);
                                                        String nick = jsonResponse1.getString("nick");
                                                        String profileUri = jsonResponse1.getString("profilePath");
                                                        Intent intent = new Intent(NickChangeActivity.this, MainCategoryActivity.class);
                                                        intent.putExtra("userID", userID);
                                                        intent.putExtra("userPass", userPass);
                                                        intent.putExtra("ID", nick);
                                                        intent.putExtra("profileUri", profileUri);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            nickCorrectRequest nickCorrectRequest = new nickCorrectRequest(userID, userPass, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                            queue.add(nickCorrectRequest);
                                        }
                                    };
                                    ValidateRegisterNick2 ValidateRegisterNick2 = new ValidateRegisterNick2(nick, strID, responseListener1);
                                    RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                    queue.add(ValidateRegisterNick2);
                                }


                                /**
                                 * google
                                 */
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse1 = new JSONObject(response);
                                            boolean success2 = jsonResponse1.getBoolean("success2");
                                            Log.e("성공3", String.valueOf(success2));
                                            if (success2) { //Register로 가입한 사용자
                                                Log.e("성공4", String.valueOf(success2));
                                                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(NickChangeActivity.this, "사용자 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                                                        intent.putExtra("ID", nick);
                                                        intent.putExtra("profileUri", profileUri);
                                                        intent.putExtra("GoogleUserEmail", GoogleUserEmail);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                };
                                                ValidateGmailNick2 ValidateGmailNick2 = new ValidateGmailNick2(nick, strID, responseListener1);
                                                RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                                queue.add(ValidateGmailNick2);
                                            }


                                            /**
                                             * kakao
                                             */
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonResponse1 = new JSONObject(response);
                                                        boolean success3 = jsonResponse1.getBoolean("success3");
                                                        Log.e("성공5", String.valueOf(success3));
                                                        if (success3) { //Register로 가입한 사용자
                                                            Log.e("성공6", String.valueOf(success3));
                                                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    Toast.makeText(NickChangeActivity.this, "사용자 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                                                                    intent.putExtra("ID", nick);
                                                                    intent.putExtra("profileUri", profileUri);
                                                                    intent.putExtra("userEmail", kakaoUserEmail);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            };
                                                            ValidateKakaoNick2 ValidateKakaoNick2 = new ValidateKakaoNick2(nick, strID, responseListener1);
                                                            RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                                            queue.add(ValidateKakaoNick2);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            ValidateKakaoNick ValidateKakaoNick = new ValidateKakaoNick(strID, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                            queue.add(ValidateKakaoNick);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                ValidateGmailNick ValidateGmailNick = new ValidateGmailNick(strID, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                                queue.add(ValidateGmailNick);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    ValidateRegisterNick ValidateRegisterNick = new ValidateRegisterNick(strID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                    queue.add(ValidateRegisterNick);
                } else {
                    Toast.makeText(NickChangeActivity.this, "사용할 이름을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nickValidateButton = (Button) findViewById(R.id.nickValidateButton);
        nickValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = ID.getText().toString();
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
                            if (success) {
                                nickValidate = true;
                                nickRedundancyCheckText.setText("사용 가능한 이름입니다.");
                                nickRedundancyCheckText.setTextColor(Color.parseColor("#03A9F4"));
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
                RequestQueue queue = Volley.newRequestQueue(NickChangeActivity.this);
                queue.add(NickValidateRequest);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}