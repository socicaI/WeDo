package com.example.wedo.SearchFilter;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InviteRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/searchInvitees.php";
    private Map<String, String> map;

    public InviteRequest(String invitees, String nick, String group, String profilePath, String people, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("invitees", invitees);
        map.put("nick", nick);
        map.put("group", group);
        map.put("profilePath", profilePath);
        map.put("people", people);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
