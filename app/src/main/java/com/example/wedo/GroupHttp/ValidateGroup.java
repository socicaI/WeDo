package com.example.wedo.GroupHttp;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ValidateGroup extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://13.209.99.25/ValidateGroup.php";
    private Map<String, String> map;

    public ValidateGroup(String nick, String usergroup, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("nick", nick);
        map.put("usergroup", usergroup);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
