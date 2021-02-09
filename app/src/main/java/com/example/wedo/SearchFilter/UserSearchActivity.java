package com.example.wedo.SearchFilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.wedo.Group.Dictionary;
import com.example.wedo.Group.MainCategoryActivity;
import com.example.wedo.GroupHttp.MainCategoryGroupRequest;
import com.example.wedo.ListHttp.ResultActivityListRequest;
import com.example.wedo.R;
import com.example.wedo.Schedule.ResultActivity;
import com.example.wedo.Schedule.TaskModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 초대할 사용자를 검색으로 해당 사용자를 보여주는 클래스
 */
public class UserSearchActivity extends AppCompatActivity implements ItemAdapter.onItemListener {

    public String id, nick, profilePath, userEmail, userID, userPass;
    private ItemAdapter adapter;
    public List<ItemModel> itemList;
    private EditText userSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        setUpRecyclerView();
        search();
        load();
    }

    /****************************************************
     리사이클러뷰, 어댑터 셋팅
     ***************************************************/
    private void setUpRecyclerView() {
        //recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
                    JSONArray list = jsonObject.getJSONArray("userSearch");
                    for (int i = 0; i < list.length(); i++) {
                        /**
                         * userName의 값을 출력해야하는 부분
                         */
                        itemList.add(new ItemModel(list.getJSONObject(i).getString("profilePath"), list.getJSONObject(i).getString("nick")));
                        adapter = new ItemAdapter(itemList);    //생성된 item들을 adapter에서 생성
                        recyclerView.setLayoutManager(layoutManager);   //recyclerView에 item을 Linear형식으로 만듦
                        recyclerView.setAdapter(adapter);
                        adapter.setOnClickListener(UserSearchActivity.this);    //어댑터의 리스너 호출
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Bundle extras = getIntent().getExtras();
        SearchRequest SearchRequest = new SearchRequest(extras.getString("nick"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserSearchActivity.this);
        queue.add(SearchRequest);
    }

    public void search() {
        userSearch = (EditText) findViewById(R.id.userSearch);

        userSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInfo = userSearch.getText().toString();
                adapter.getFilter().filter(userInfo);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String userInfo = userSearch.getText().toString();
                adapter.getFilter().filter(userInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String userInfo = userSearch.getText().toString();
                adapter.getFilter().filter(userInfo);
            }
        });
    }

    public void load() {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //그룹명
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");
    }

    /****************************************************
     리사이클러뷰 클릭이벤트 인터페이스 구현
     ***************************************************/
    @Override
    public void onItemClicked(int position) {
        ItemModel model = itemList.get(position);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success){
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), "" + model.getText() + "님을 초대하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        };
                        Bundle extras = getIntent().getExtras();
                        InviteRequest InviteRequest = new InviteRequest(model.getText(), extras.getString("nick"), extras.getString("id"), model.getImageResource(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(UserSearchActivity.this);
                        queue.add(InviteRequest);
                    }else{
                        Toast.makeText(getApplicationContext(), "이미 초대된 사용자입니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    System.out.println("에러 메시지: "+ e.toString());
                }
            }
        };
        Bundle extras = getIntent().getExtras();
        ValidateInvite ValidateInvite = new ValidateInvite(model.getText(), extras.getString("nick"), extras.getString("id"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserSearchActivity.this);
        queue.add(ValidateInvite);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("nick", nick);
        intent.putExtra("profilePath", profilePath);
        intent.putExtra("userID", userID);
        intent.putExtra("userPass", userPass);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
        finish();
    }
}