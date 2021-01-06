package com.example.wedo;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EmailRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://13.209.99.25/b.php";
    private Map<String,String> map;

    public EmailRequest(String from, String num, String sendEmail, Response.Listener<String>listener){
        super(Method.POST,URL,listener,null);
        map=new HashMap<>();
        map.put("from",from);
        map.put("num", num);
        map.put("sendEmail", sendEmail);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
