package com.example.wedo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://azureindex00.dothome.co.kr/Register.php";
    private Map<String, String> map;

    public RegisterRequest(String userID, String nick, String userPass, String email, String emailSuccess, String downloadUri, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);//위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("Nick", nick);
        map.put("userPass", userPass);
        map.put("email", email);
        map.put("emailSuccess", String.valueOf(emailSuccess));
        map.put("downloadUri", downloadUri);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
