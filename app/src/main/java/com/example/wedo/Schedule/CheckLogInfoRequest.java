package com.example.wedo.Schedule;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckLogInfoRequest extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static private String URL = "http://54.180.0.255/checkLogInfo.php";
    private Map<String, String> map;

    public CheckLogInfoRequest(String OrderUser, String OrderGroup, String InviteesUser, String OrderList, String OrderSchedule, String CheckInfo, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("OrderUser", OrderUser);
        map.put("OrderGroup", OrderGroup);
        map.put("InviteesUser", InviteesUser);
        map.put("OrderList", OrderList);
        map.put("OrderSchedule", OrderSchedule);
        map.put("CheckInfo", CheckInfo);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}