package com.example.wedo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateRegisterNick extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://azureindex00.dothome.co.kr/ValidateRegisterNick.php";
    private Map<String, String> map;

    public ValidateRegisterNick(String beforeNick,  Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("beforeNick", beforeNick);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}