package com.example.wedo.Register;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Login.LoginActivity;
import com.example.wedo.R;
import com.example.wedo.RegisterHttp.EmailConfirmationNum;
import com.example.wedo.RegisterHttp.EmailRequest;
import com.example.wedo.RegisterHttp.NickValidateRequest;
import com.example.wedo.RegisterHttp.RegisterRequest;
import com.example.wedo.RegisterHttp.ValidateRequest;
import com.example.wedo.RegisterHttp.Validate_emailRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    //registration_id_input=아이디 입력란, registration_password_input=비밀번호 입력란, registration_password_check_input=비밀번호 입력 재확인란, registrationEmailInput=이메일 입력란, registrationEmailCorrectInput=이메일 인증번호, setNickInput=닉네임 입력란
    private EditText registration_id_input, registration_password_input, registration_password_check_input, registrationEmailInput, registrationEmailCorrectInput, setNickInput;
    //id_validate_button=아이디 중복 검사 버튼, registrationButton=가입 버튼, emailValidateButton=이메일 인증 버튼, nickValidateButton=닉네임 중복 검사 버튼
    private Button id_validate_button, registrationButton, emailValidateButton, nickValidateButton;
    //비밀번호를 일치하게 입력했는지 판별해주는 이미지뷰
    private TextView password_check_text, profileImageText;
    //아이디 중복 여부에 대한 다이얼로그
    private AlertDialog dialog;
    //id_validate=아이디이 중복인지 아닌지 판별하기 위한 boolean / pass_validate=비밀번호가 일치한지 판결하기 위한 boolean / emailValidate=email을 양식에 맞게 입력했는지 판별해주는 boolean / emailCheck=email을 재전송하는 건지 판별해주는 boolean / nickValidate=닉네임이 중복인지 판별해주는 boolean
    private boolean id_validate = false, pass_validate = false, emailValidate = false, emailCheck = false, nickValidate = false, profile = false;
    String inputEmail; //이메일 입력란에 입력한 값을 String 형식으로 변환.
    String num, num2, userID, nick; //num=이메일 인증번호, num2=이메일 인증번호 확인, userID=아이디, nick=앱에서 사용자가 사용할 이름(닉네임)

    /**
     * 프로필 사진
     */
    ImageView profileImageView;
    String profilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        password_check_text = findViewById(R.id.password_check_text); ///비밀번호를 일치하게 입력했는지 판별해주는 이미지뷰
        registration_id_input = findViewById(R.id.registration_id_input); //registration_id_input=아이디 입력란
        registration_password_input = findViewById(R.id.registration_password_input); //registration_password_input=비밀번호 입력란
        registration_password_check_input = findViewById(R.id.registration_password_check_input); //registration_password_check_input=비밀번호 입력 재확인란
        registrationButton = findViewById(R.id.registrationButton);   //registrationButton=가입되어 메인 화면으로 이동하는 버튼
        setNickInput = findViewById(R.id.setNickInput); //setNickInput=닉네임 입력란
        profileImageText = findViewById(R.id.profileImageText);

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

        /**
        비밀번호 입력 재확인란이 비밀번호 입력란과 서로 일치한지 확인하기 위한 메소드
         */
        registration_password_check_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
            비밀번호 입력란과 비밀번호 입력 재확인란이 서로 일치한지 판별하여 맞다면 'V' 이미지를 아닐 경우 'X' 이미지를 보여주기 위한 메소드
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (registration_password_check_input.getText().toString().length() > 0) {
                    if (registration_password_input.getText().toString().equals(registration_password_check_input.getText().toString())) { //비밀번호 입력 재확인란이 공백이 아니면서 비밀번호 입력란과 일치할 경우
                        password_check_text.setText("비밀번호가 일치합니다.");
                        password_check_text.setTextColor(Color.parseColor("#3498DB"));
                        pass_validate = true;
                    } else {
                        password_check_text.setText("비밀번호가 일치하지 않습니다.");
                        password_check_text.setTextColor(Color.parseColor("#FF0000"));
                        pass_validate = false;
                    }
                } else {
                    password_check_text.setText("비밀번호가 일치하지 않습니다.");
                    password_check_text.setTextColor(Color.parseColor("#FF0000"));
                    pass_validate = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /**
        위와 동일 (비밀번호 입력란과 비밀번호 입력 재확인란과 서로 일치한지 확인하기 위한 메소드)
         */
        registration_password_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
            비밀번호 입력란과 비밀번호 입력 재확인란이 서로 일치한지 판별하여 맞다면 'V' 이미지를 아닐 경우 'X' 이미지를 보여주기 위한 메소드
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (registration_password_input.getText().toString().length() > 0) {
                    if (registration_password_input.getText().toString().equals(registration_password_check_input.getText().toString())) { //비밀번호 입력 재확인란이 공백이 아니면서 비밀번호 입력란과 일치할 경우
                        password_check_text.setText("비밀번호가 일치합니다.");
                        password_check_text.setTextColor(Color.parseColor("#3498DB"));
                        pass_validate = true;
                    } else {
                        password_check_text.setText("비밀번호가 일치하지 않습니다.");
                        password_check_text.setTextColor(Color.parseColor("#FF0000"));
                        pass_validate = false;
                    }
                } else {
                    password_check_text.setText("비밀번호가 일치하지 않습니다.");
                    password_check_text.setTextColor(Color.parseColor("#FF0000"));
                    pass_validate = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /**
        아이디를 다시 수정했을 경우
         */
        registration_id_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id_validate = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
        사용할 이름을 다시 수정했을 경우
         */
        setNickInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nickValidate = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**
        이메일의 인증 버튼을 통해 양식에 맞게 이메일을 입력했다면 해당 메일로 인증 메일이 전송이 되는 메소드.
         */
        emailValidateButton = findViewById(R.id.emailValidateButton);
        emailValidateButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       registrationEmailInput = findViewById(R.id.registrationEmailInput);
                                                       inputEmail = registrationEmailInput.getText().toString(); //이메일 입력란에 입력한 값을 String 형식으로 변환.

                                                       if (inputEmail.length() < 1) {   //이메일 입력란이 비어있을 경우 다이얼로그 출력.
                                                           emailValidate = false;
                                                           AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                           dialog = builder.setMessage("이메일을 입력해주세요.")
                                                                   .setPositiveButton("확인", null)
                                                                   .create();
                                                           dialog.show();
                                                           return;
                                                       } else { //이메일 입력란에 값이 존재할 경우 양식에 맞는지 정규식 검사를 진행.
                                                           String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
                                                           String sendEmail = String.valueOf(emailCheck);

                                                           Pattern p = Pattern.compile(regex);
                                                           Matcher m = p.matcher(inputEmail);
                                                           if (m.matches()) {   //이메일 양식에 맞게 입력했을 경우 해당 이메일로 인증 번호가 전송.
                                                               Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                   @Override
                                                                   public void onResponse(String response) {
                                                                       try {
                                                                           JSONObject jsonResponse = new JSONObject(response);
                                                                           boolean success = jsonResponse.getBoolean("success");
                                                                           AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                                           if (success) {
                                                                               int result = (int) (Math.floor(Math.random() * 1000000) + 100000);
                                                                               if (result > 1000000) {
                                                                                   result = result - 100000;
                                                                               }
                                                                               num = String.valueOf(result);
                                                                               Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                                                                   @Override
                                                                                   public void onResponse(String response) {
                                                                                       if (emailValidateButton.getText().toString().equals("인증")) {
                                                                                           Toast.makeText(RegisterActivity.this, "인증 번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                                                                                           registrationEmailCorrectInput.setVisibility(View.VISIBLE);
                                                                                           emailValidateButton.setText("재전송");
                                                                                           emailValidate = true;
                                                                                       } else {
                                                                                           Toast.makeText(RegisterActivity.this, "인증 번호가 재전송되었습니다.", Toast.LENGTH_SHORT).show();
                                                                                           emailValidate = true;
                                                                                       }
                                                                                   }
                                                                               };
                                                                               EmailRequest EmailRequest = new EmailRequest(inputEmail, num, sendEmail, responseListener1);
                                                                               RequestQueue queue1 = Volley.newRequestQueue(RegisterActivity.this);
                                                                               queue1.add(EmailRequest);
                                                                           } else {
                                                                               dialog = builder.setMessage("이미 가입된 이메일입니다.")
                                                                                       .setNegativeButton("확인", null)
                                                                                       .create();
                                                                               dialog.show();
                                                                               emailValidate = false;
                                                                           }
                                                                       } catch (JSONException e) {
                                                                           e.printStackTrace();
                                                                       }
                                                                   }
                                                               };
                                                               Validate_emailRequest Validate_emailRequest = new Validate_emailRequest(inputEmail, responseListener);
                                                               RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                                                               queue.add(Validate_emailRequest);
                                                           } else { //이메일 양식에 맞지 않게 텍스트를 입력했을 경우 다이얼로그 출력.
                                                               emailValidate = false;
                                                               AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                               dialog = builder.setMessage("양식에 맞게 입력해주세요.")
                                                                       .setPositiveButton("확인", null)
                                                                       .create();
                                                               dialog.show(); //다이얼로그 실행
                                                               return;
                                                           }
                                                       }
                                                   }
                                               }
        );

        /*
        사용할 이름에 대한 중복 체크 버튼을 통해 DB에 중복되는 닉네임이 있는지 체크하는 메소드
         */
        nickValidateButton = findViewById(R.id.nickValidateButton);
        nickValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = setNickInput.getText().toString();
                if (nick.equals("")) {
                    nickValidate = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this); //회원가입 화면에 문구를 다이얼로그로 띄어주기 위해 선언
                    dialog = builder.setMessage("사용하실 이름을 입력해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show(); //다이얼로그 실행
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            if (success) {
                                nickValidate = true;
                                dialog = builder.setMessage("사용 가능한 이름입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            } else {
                                nickValidate = false;
                                dialog = builder.setMessage("중복된 이름입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                NickValidateRequest NickValidateRequest = new NickValidateRequest(nick, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(NickValidateRequest);
            }
        });

        /**
        아이디 중복 체크 버튼을 통해 DB에 중복되는 아이디가 있는지 체크하는 메소드
         */
        id_validate_button = findViewById(R.id.id_validate_button); //중복 확인 버튼
        id_validate_button.setOnClickListener(new View.OnClickListener() { //즁복 확인 버튼을 눌렀을 경우
            @Override
            public void onClick(View view) {
                userID = registration_id_input.getText().toString(); //중복 검사를 위해 아이디 입력란에 입력한 Text를 String으로 변환하여 userID에 저장
                if (userID.equals("")) { //만약 userID가 공백이라면 "아이디를 입력해주세요."라는 다이얼로그로 예외처리
                    id_validate = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this); //회원가입 화면에 문구를 다이얼로그로 띄어주기 위해 선언
                    dialog = builder.setMessage("아이디를 입력해주세요.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show(); //다이얼로그 실행
                    return; //return을 통해 사용자가 값을 다시 입력하기 위해 되돌린다.
                }


                /**
                ID 입력란에 입력 되어 있는 상태에서 사용할 수 있는 ID인지 사용할 수 없는 ID인지 판별해주는 메소드
                 */
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            if (success) {
                                id_validate = true;
                                dialog = builder.setMessage("사용 가능한 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            } else {
                                id_validate = false;
                                dialog = builder.setMessage("중복된 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        /*
        이메일 인증 화면으로 이동하는 메소드
         */
        registrationEmailCorrectInput = findViewById(R.id.registrationEmailCorrectInput);
        registrationEmailCorrectInput.setVisibility(View.GONE);

        registrationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (profile) {
                    if (id_validate) {
                        if (nickValidate) {
                            if (pass_validate) {
                                if (emailValidate) {

                                    num2 = registrationEmailCorrectInput.getText().toString();

                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean success = jsonResponse.getBoolean("success");
                                                boolean currectEmail = false;
                                                if (success) {
                                                    ProgressDialog dialog1 = ProgressDialog.show(RegisterActivity.this, "",
                                                            "WeDo-잠시만 기다려주세요 :)", true);
                                                    dialog1.onStart();
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();

                                                    StorageReference storageRef = storage.getReference();
                                                    final StorageReference mountainImagesRef = storageRef.child("images/" + nick + "/profile.jpg"); //파일 경로 images/사용자 ID/
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
                                                                    Log.e("성공", "성공: " + downloadUri);
                                                                    Response.Listener<String> responseListener6 = new Response.Listener<String>() {//volley
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            Toast.makeText(getApplicationContext(), "회원 가입 되었습니다.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    };
                                                                    //서버로 volley를 이용해서 요청을 함
                                                                    RegisterRequest RegisterRequest = new RegisterRequest(userID, nick, registration_password_check_input.getText().toString(), inputEmail, "true", downloadUri.toString(), responseListener6);
                                                                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                                                                    queue.add(RegisterRequest);

                                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                                    startActivity(intent);
                                                                    dialog1.dismiss();
                                                                    finish();
                                                                } else {
                                                                    Log.e("로그", "실패");
                                                                }
                                                            }
                                                        });
                                                    } catch (FileNotFoundException e) {
                                                        Log.e("로그", "에러:" + e.toString());
                                                    }

                                                    currectEmail = true;
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "인증 번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                                                    currectEmail = false;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    EmailConfirmationNum EmailConfirmationNum = new EmailConfirmationNum(inputEmail, num2, responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                                    queue.add(EmailConfirmationNum);
                                } else {
                                    Toast.makeText(getApplicationContext(), "이메일 인증해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "사용할 이름을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "프로필 사진을 찍어주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                }
            }
            break;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
