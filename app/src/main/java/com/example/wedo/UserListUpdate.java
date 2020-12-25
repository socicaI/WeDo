package com.example.wedo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserListUpdate extends StringRequest {
    //서버 url 설정(php파일 연동)
    final static  private String URL="http://azureindex00.dothome.co.kr/userListUpdate.php";
    private Map<String,String> map;

    public UserListUpdate(String nick, String usergroup, String userlist, String changelist, Response.Listener<String>listener){
        super(Request.Method.POST,URL,listener,null);

        map=new HashMap<>();
        map.put("nick", nick);
        map.put("usergroup", usergroup);
        map.put("userlist", userlist);
        map.put("changelist", changelist);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
