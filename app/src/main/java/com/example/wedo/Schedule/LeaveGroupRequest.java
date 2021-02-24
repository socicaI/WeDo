package com.example.wedo.Schedule;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LeaveGroupRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/InviteesLeaveGroup.php";
    private Map<String, String> map;

    public LeaveGroupRequest(String nick, String orderNick, String orderGroup, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("nick", nick);
        map.put("orderUser", orderNick);
        map.put("orderGroup", orderGroup);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
