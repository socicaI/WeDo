package com.example.wedo.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.R;
import com.example.wedo.Schedule.ResultActivity;
import com.example.wedo.SearchFilter.ItemAdapter;
import com.example.wedo.SearchFilter.SearchRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityInfo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public List<ActivityItemModel> itemList;
    private ActivityItemAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public String id, nick, profilePath, userEmail, userID, userPass, orderNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //주제
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        orderNick = extras.getString("orderNick");
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");

        setUpRecyclerView();
    }

    /****************************************************
     리사이클러뷰, 어댑터 셋팅
     ***************************************************/
    private void setUpRecyclerView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(ActivityInfo.this);
        //recyclerview
        RecyclerView recyclerView = findViewById(R.id.activityInfoRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        //adapter
        itemList = new ArrayList<>();

        /**
         * WeDo에 가입된 사용자 이름을 불러오는 클래스
         */
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray("activityInfo");
                    for (int i = 0; i < list.length(); i++) {
                        /**
                         * userName의 값을 출력해야하는 부분
                         */
                        if (list.getJSONObject(i).getString("CheckInfo").equals("체크 해제") || list.getJSONObject(i).getString("CheckInfo").equals("체크")) {
                            itemList.add(new ActivityItemModel(list.getJSONObject(i).getString("DateTime"),
                                    list.getJSONObject(i).getString("InviteesUser") + "가(이) "
                                    , list.getJSONObject(i).getString("CheckInfo") + " ",
                                    "'" + list.getJSONObject(i).getString("OrderList") + "'" + "의 "
                                            + list.getJSONObject(i).getString("OrderSchedule")));
                        } else if (list.getJSONObject(i).getString("CheckInfo").equals("추가") || list.getJSONObject(i).getString("CheckInfo").equals("수정") || list.getJSONObject(i).getString("CheckInfo").equals("삭제")) {
                            if (list.getJSONObject(i).getString("OrderSchedule").equals("null")) {
                                itemList.add(new ActivityItemModel(list.getJSONObject(i).getString("DateTime"),
                                        list.getJSONObject(i).getString("InviteesUser") + "가(이) "
                                        , list.getJSONObject(i).getString("CheckInfo") + " ",
                                        "'" + list.getJSONObject(i).getString("OrderList")+"'"));
                            } else {
                                itemList.add(new ActivityItemModel(list.getJSONObject(i).getString("DateTime"),
                                        list.getJSONObject(i).getString("InviteesUser") + "가(이) "
                                        , list.getJSONObject(i).getString("CheckInfo") + " ",
                                        "'" + list.getJSONObject(i).getString("OrderList") + "'" + "의 "
                                                + list.getJSONObject(i).getString("OrderSchedule")));
                            }
                        }
                        adapter = new ActivityItemAdapter(itemList);    //생성된 item들을 adapter에서 생성
                        recyclerView.setLayoutManager(layoutManager);   //recyclerView에 item을 Linear형식으로 만듦
                        recyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Bundle extras = getIntent().getExtras();
        ActivityInfoRequest ActivityInfoRequest = new ActivityInfoRequest(extras.getString("orderNick"), extras.getString("id"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ActivityInfo.this);
        queue.add(ActivityInfoRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("nick", nick);
        intent.putExtra("orderNick", orderNick);
        intent.putExtra("profilePath", profilePath);
        intent.putExtra("userID", userID);
        intent.putExtra("userPass", userPass);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        setUpRecyclerView();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}