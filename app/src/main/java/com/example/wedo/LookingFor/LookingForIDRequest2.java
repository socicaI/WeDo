package com.example.wedo.LookingFor;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.OAuthCredential;

import java.util.HashMap;
import java.util.Map;

public class LookingForIDRequest2 extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://54.180.0.255/LookingForID2.php";
    private Map<String,String> map;

    public LookingForIDRequest2(String Email, String auth, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("Email",Email);
        map.put("auth", auth);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
