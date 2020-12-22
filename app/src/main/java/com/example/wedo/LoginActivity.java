package com.example.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;

public class LoginActivity extends AppCompatActivity {
    private EditText login_id_input, login_password_input; //회원 아이디 입력란, 회원 비밀번호 입력란
    private Button login_button; //로그인 버튼
    private TextView membership_registration, looking_for_id, looking_for_pass; //회원가입 버튼, 아이디 찾기 버튼, 비밀번호 찾기 버튼
    private ImageView kakao_login_button; //카카오 로그인 버튼
    private long backKeyPressedTime = 0; //뒤로가기 2번 클릭 시 종료하기 위한 변수
    private SessionCallback sessionCallback; //카카오톡 로그인(분석 필요)
    private LoginButton login_kakao_real;
    private boolean kakao = false, gmail = false;

    /*
    google
     */
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "mainTag";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 123;
    private String GoogleUserEmail;

    /**
     * 카카오
     */
    String kakaoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); //로그인 레이아웃과 관련된 클래스

        /*
        아이디 찾기를 눌렀을 경우 아이디 찾기 화면으로 이동
         */
        looking_for_id = findViewById(R.id.looking_for_id);
        looking_for_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LookingFor_ID.class);
                startActivity(intent);
            }
        });
        /*
        비밀번호 찾기를 눌렀을 경우 비밀번호 찾기 화면으로 이동
         */
        looking_for_pass = findViewById(R.id.looking_for_password);
        looking_for_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LookingForPass.class);
                startActivity(intent);
            }
        });

        //google
        signInButton = findViewById(R.id.SignIn_Button);

        //google
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //카카오톡 로그인 구간
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
        getAppKeyHash();

        login_id_input = findViewById(R.id.login_id_input); //회원 로그인 입력란
        login_password_input = findViewById(R.id.login_password_input); //회원 비밀번호 입력란
        login_button = findViewById(R.id.login_button); //로그인 버튼
        membership_registration = findViewById(R.id.membership_registration); //회원가입 버튼
        kakao_login_button = findViewById(R.id.kakao_login_button);

        /*
        카카오
         */
        kakao_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_kakao_real.performClick();
            }
        });
        login_kakao_real = findViewById(R.id.login_kakao_real);

        /*
        회원가입 버튼을 클릭 시 회원가입 할 수 있는 페이지도 전환하기 위한 메소드
         */
        membership_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); //회원가입 페이지로 이동
                startActivity(intent);
            }
        });


        /*
        로그인 버튼을 클릭 시 아이디와 비밀번호를 체크하여 회원인지 아닌지 판별하기 위한 메소드
         */
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = login_id_input.getText().toString(); //아이디 입력란의 Text를 String으로 변환하여 userID에 넣은 후 DB에 회원이 맞는지 검사할 때 필요한 변수
                final String userPass = login_password_input.getText().toString(); //비밀번호 입력란의 Text를 String으로 변환하여 userPass에 넣은 후 DB에 회원으 ㅣ비밀번호가 맞는지 검사할 때 필요한 변수

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject = new JSONObject(response);
                            boolean success = jasonObject.getBoolean("success");
                            if (success) {//회원등록 성공한 경우
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success4 = jsonResponse.getBoolean("success4");
                                            String nick = jsonResponse.getString("nick");
                                            String profileUri = jsonResponse.getString("profilePath");

                                            if (success4) {
                                                Toast.makeText(getApplicationContext(), "로그인되었습니다.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MainCategoryActivity.class);
                                                intent.putExtra("userID", userID);
                                                intent.putExtra("userPass", userPass);
                                                intent.putExtra("ID", nick);
                                                intent.putExtra("profileUri", profileUri);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(LoginActivity.this, GmailSetNickActivity.class);
                                                intent.putExtra("userID", userID);
                                                intent.putExtra("userPass", userPass);
                                                startActivity(intent);
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                nickCorrectRequest nickCorrectRequest = new nickCorrectRequest(userID, userPass, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                queue.add(nickCorrectRequest);
                            } else {//회원등록 실패한 경우
                                Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPass, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    /*
    google
     */
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    // [END on_start_check_user]

    /*
    로그인 화면에서 뒤로가기 2번 클릭 시 앱 종료
     */
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

    //카카오톡/google 로그인에 관한 코드 - 분석 필요
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d("GoogleUserName", account.getDisplayName());
                Log.d("GoogleUserEmail", account.getEmail());

                String GoogleUserName = account.getDisplayName();
                GoogleUserEmail = account.getEmail();

                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        if (gmail) {
                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jasonObject = new JSONObject(response);
                                        boolean success4 = jasonObject.getBoolean("success4");
                                        Log.e("로그", String.valueOf(success4));
                                        if (success4) {
                                            Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jasonObject = new JSONObject(response);
                                                        String profilePath = jasonObject.getString("profilePath");
                                                        String nick = jasonObject.getString("Nick");
                                                        Log.e("로그123", profilePath);
                                                        Log.e("로그123", GoogleUserEmail);
                                                        Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                                                        intent.putExtra("ID", nick);
                                                        intent.putExtra("profileUri", profilePath);
                                                        startActivity(intent);
                                                        finish();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            GmailLogin2 GmailLogin2 = new GmailLogin2(GoogleUserEmail, responseListener1);
                                            RequestQueue queue1 = Volley.newRequestQueue(LoginActivity.this);
                                            queue1.add(GmailLogin2);

                                        } else {//닉네임을 설정하지 않았을 경우
                                            Intent intent = new Intent(getApplicationContext(), GmailSetNickActivity.class);
                                            intent.putExtra("userEmail", GoogleUserEmail);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            GmailUserInfo GmailUserInfo = new GmailUserInfo(GoogleUserEmail, responseListener1);
                            RequestQueue queue1 = Volley.newRequestQueue(LoginActivity.this);
                            queue1.add(GmailUserInfo);
                        }
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                Google_Register Google_Register = new Google_Register(GoogleUserName, GoogleUserEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(Google_Register);
                gmail = true;
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
    google
     */
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
//                            Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();

                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    // [END auth_with_google]
    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 닫혔습니다. 다시 시도해 주세요: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {

//                        intent.putExtra("ID", result.getKakaoAccount().getEmail());

                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                        @Override
                        public void onResponse(String response) {
                            if (kakao) {
                                /**
                                 * 수정해야하는 부분
                                 */
                                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jasonObject = new JSONObject(response);
                                            boolean success4 = jasonObject.getBoolean("success4");
                                            Log.e("로그123", String.valueOf(success4));
                                            if (success4) {
                                                Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jasonObject = new JSONObject(response);
                                                            String profilePath = jasonObject.getString("profilePath");
                                                            String nick = jasonObject.getString("nick");
                                                            Log.e("로그123", profilePath);
                                                            Log.e("로그123", kakaoId);

                                                            Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                                                            intent.putExtra("ID", nick);
                                                            intent.putExtra("profileUri", profilePath);
                                                            intent.putExtra("userEmail", kakaoId);
                                                            startActivity(intent);
                                                            finish();

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };
                                                KakaoLogin2 KakaoLogin2 = new KakaoLogin2(kakaoId, responseListener1);
                                                RequestQueue queue1 = Volley.newRequestQueue(LoginActivity.this);
                                                queue1.add(KakaoLogin2);

                                            } else {//닉네임을 설정하지 않았을 경우
                                                Intent intent = new Intent(getApplicationContext(), KakaoSetNickActivity.class);
                                                intent.putExtra("userEmail", kakaoId);
                                                startActivity(intent);
                                                Session.getCurrentSession().close();
                                                finish();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                KakaoUserInfo KakaoUserInfo = new KakaoUserInfo(kakaoId, responseListener1);
                                RequestQueue queue1 = Volley.newRequestQueue(LoginActivity.this);
                                queue1.add(KakaoUserInfo);
                            }                        }
                    };
                    //서버로 volley를 이용해서 요청을 함
                    kakaoId = result.getKakaoAccount().getEmail();
                    Kakao_Register Kakao_Register = new Kakao_Register(result.getNickname(), result.getKakaoAccount().getEmail(), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(Kakao_Register);
                    kakao = true;
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
        }
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }
}