package com.example.wedo.Chating;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChatNotif extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/FCM/send.php";
    private Map<String, String> map;

    public ChatNotif(String nick, String content, String group, String orderNick, String profilePath, String userID, String userPass, String TitleProfile, String people, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("nick", nick);
        map.put("content", content);
        map.put("group", group);
        map.put("orderNick", orderNick);
        map.put("profilePath", profilePath);
//        map.put("userEmail", userEmail);
        map.put("userID", userID);
        map.put("userPass", userPass);
        map.put("TitleProfile", TitleProfile);
        map.put("people", people);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
