package com.example.wedo.LookingFor;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자의 ID를 메일로 쏴주도록 서버에 요청
 */
public class IDEmailRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/c.php";
    private Map<String, String> map;

    public IDEmailRequest(String from, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("from", from);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
