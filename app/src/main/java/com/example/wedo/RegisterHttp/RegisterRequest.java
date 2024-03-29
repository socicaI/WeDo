package com.example.wedo.RegisterHttp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 기입한 양식을 DB에 저장할 수 있게 서버에 요청
 */
public class RegisterRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/Register.php";
    private Map<String, String> map;

    public RegisterRequest(String userID, String nick, String userPass, String email, String emailSuccess, String profilePath, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);//위 url에 post방식으로 값을 전송

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("Nick", nick);
        map.put("userPass", userPass);
        map.put("email", email);
        map.put("emailSuccess", String.valueOf(emailSuccess));
        map.put("profilePath", profilePath);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
