package com.example.wedo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.xml.transform.Result;

public class ResultActivity extends AppCompatActivity {

    /**
     * RecyclerView 부분
     */
    private ArrayList<DictionaryList> mDictionaryList;
    private CustomAdapterList mAdapterList;
    private int count = -1;
    private String TAG_JSON="userlist";

    private DrawerLayout drawerLayout;
    private View drawerView;
    public String id, nick, profilePath, userEmail, userID, userPass;
    public String str_group, str_user, str_profile, strID3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");    //그룹명
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");

        TextView textView = (TextView) findViewById(R.id.id);
        str_group = id;
        str_user = nick;
        str_profile = profilePath;   //프로필
        textView.setText(str_group);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        ImageView btn_open = (ImageView) findViewById(R.id.btnOpen);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        /**
         * 서버에 list Data가 있는지 확인하고 가져오는 메소드
         */
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray(TAG_JSON);

                    Log.e("user333: ", String.valueOf(list.length()));

                    /**
                     * 그룹 배열의 크기만큼 반복문을 돌려 데이터를 String에 넣어줌과 동시에 RecyclerView item 생성
                     */
                    for(int i=0; i<list.length(); i++){
                        System.out.println("들어옴222222222222");
                        JSONObject item = list.getJSONObject(i);

                        String group1 = item.getString("list");
                        Log.e("유저그룹333: ", group1);

                        DictionaryList dict = new DictionaryList(group1);
                        dict.setUser(str_user);
                        mDictionaryList.add(dict); //마지막 줄에 삽입됨 1
                        mAdapterList.notifyDataSetChanged();  //마지막 줄에 삽입됨 2
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResultActivityListRequest ResultActivityListRequest = new ResultActivityListRequest(str_user, str_group, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
        queue.add(ResultActivityListRequest);

        Button updateGroup = (Button) findViewById(R.id.updateGroup);
        updateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                View view = LayoutInflater.from(ResultActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view);
                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText editTextID = (EditText) view.findViewById(R.id.mesgase);

                editTextID.setHint(textView.getText().toString());
                ButtonSubmit.setText("편집하기");

                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strID = editTextID.getText().toString();
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                            }
                                        };
                                        //서버로 volley를 이용해서 요청을 함

                                        UserGroupUpdate UserGroupUpdate = new UserGroupUpdate(str_user, str_group, strID, responseListener);

                                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                        queue.add(UserGroupUpdate);
                                        textView.setText(strID);
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(ResultActivity.this, "그룹명이 존재합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ValidateGroup ValidateGroup = new ValidateGroup(str_user, strID, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                        queue.add(ValidateGroup);
                    }
                });
                dialog.show();
            }
        });

        Button removeGroup = (Button) findViewById(R.id.removeGroup);
        removeGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ResultActivity.this);
                View view1 = LayoutInflater.from(ResultActivity.this)
                        .inflate(R.layout.edit_box2, null, false);
                builder1.setView(view1);
                final Button ButtonSubmit1 = (Button) view1.findViewById(R.id.button_remove_submit);
                final Button ButtonSubmit2 = (Button) view1.findViewById(R.id.button_cancel_submit);

                final AlertDialog dialog1 = builder1.create();
                ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                                intent.putExtra("profileUri", str_profile);
                                intent.putExtra("ID", str_user);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPass", userPass);
                                startActivity(intent);
                                finish();
                            }
                        };
                        //서버로 volley를 이용해서 요청을 함

                        UserGroupRemove UserGroupRemove = new UserGroupRemove(str_user, str_group, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                        queue.add(UserGroupRemove);
                    }
                });
                ButtonSubmit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });
                dialog1.show();
            }
        });

        /**
         * RecyclerView 부분
         */
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDictionaryList = new ArrayList<>();


        //mAdapter = new CustomAdapter( mArrayList);
        mAdapterList = new CustomAdapterList(this, mDictionaryList);


        mRecyclerView.setAdapter(mAdapterList);

        ImageButton buttonInsert = (ImageButton) findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {

            // 1. 화면 아래쪽에 있는 데이터 추가 버튼을 클릭하면
            @Override
            public void onClick(View v) {

                // 2. 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                View view = LayoutInflater.from(ResultActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view);

                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText editTextID = (EditText) view.findViewById(R.id.mesgase);

                ButtonSubmit.setText("목록 추가");
                editTextID.setHint("추가하실 목록명을 입력하세요.");

                final AlertDialog dialog = builder.create();


                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면

                ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        // 4. 사용자가 입력한 내용을 가져와서
                        strID3 = editTextID.getText().toString();

                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        Log.e("마! 들어왔나!!", "성공했나");
                                        DictionaryList dict = new DictionaryList(strID3);
//                        mArrayList.add(0, dict); //첫번째 줄에 삽입됨 1
                                        dict.setUser(str_user);

                                        mDictionaryList.add(dict); //마지막 줄에 삽입됨 1


                                        // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.

//                        mAdapter.notifyItemInserted(0); //첫번째 줄에 삽입됨 2
                                        mAdapterList.notifyDataSetChanged();  //마지막 줄에 삽입됨 2
                                        dialog.dismiss();

                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                            @Override
                                            public void onResponse(String response) {
                                            }
                                        };
                                        //서버로 volley를 이용해서 요청을 함
                                        UserListAdd UserListAdd = new UserListAdd(str_user, str_group, strID3, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                        queue.add(UserListAdd);
                                    } else {
                                        Toast.makeText(ResultActivity.this, "목록명이 존재합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        ValidateList ValidateList = new ValidateList(str_user, str_group, strID3, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                        queue.add(ValidateList);
                    }
                });
                dialog.show();
            }

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
        intent.putExtra("profileUri", str_profile);
        intent.putExtra("ID", str_user);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userID", userID);
        intent.putExtra("userPass", userPass);
        startActivity(intent);
        finish();
    }
}