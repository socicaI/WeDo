package com.example.wedo.RegisterHttp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Validate_emailRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://13.209.99.25/Validate_email.php";
    private Map<String,String> map;

    public Validate_emailRequest(String Email, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("Email",Email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}