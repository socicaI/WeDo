package com.example.wedo.Group;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserDeviceValue extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/FCM/userDevice.php";
    private Map<String, String> map;

    public UserDeviceValue(String nick, String deviceValue, String orderUser, String orderGroup, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("nick", nick);
        map.put("deviceValue", deviceValue);
        map.put("orderUser", orderUser);
        map.put("orderGroup", orderGroup);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
