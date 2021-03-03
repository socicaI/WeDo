package com.example.wedo.Schedule;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ThrowOutRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/deleteInvite.php";
    private Map<String, String> map;

    public ThrowOutRequest(String nick, String orderNick, String orderGroup, String people, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("nick", nick);
        map.put("orderUser", orderNick);
        map.put("orderGroup", orderGroup);
        map.put("people", people);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

