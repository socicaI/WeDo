package com.example.wedo.Schedule;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InviteesRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/Invitees.php";
    private Map<String, String> map;

    public InviteesRequest(String nick, String orderNick, String group, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("nick", nick);
        map.put("orderNick", orderNick);
        map.put("group", group);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}