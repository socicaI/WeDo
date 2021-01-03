package com.example.wedo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
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
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    /**
     * ExpandingListView
     */
    private ExpandingList mExpandingList;
    boolean schedule = false;
    String tta;

    /**
     * RecyclerView 부분
     */
    private ArrayList<DictionaryList> mDictionaryList;
    private CustomAdapterList mAdapterList;
    private int count = -1;
    private String TAG_JSON = "userlist";

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

        /**
         * ExpandingList
         */
        mExpandingList = findViewById(R.id.expanding_list_main);
        createTitle();

//        /**
//         * RecyclerView 부분
//         */
//        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
//        TextView emptyRecycler = (TextView) findViewById(R.id.emptyRecycler);
//        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//
//        mDictionaryList = new ArrayList<>();
//
//
//        //mAdapter = new CustomAdapter( mArrayList);
//        mAdapterList = new CustomAdapterList(this, mDictionaryList);


//        mRecyclerView.setAdapter(mAdapterList);

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
//        Response.Listener<String> responseListener = new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray list = jsonObject.getJSONArray(TAG_JSON);
//
//                    Log.e("user333: ", String.valueOf(list.length()));
//
//                    /**
//                     * 그룹 배열의 크기만큼 반복문을 돌려 데이터를 String에 넣어줌과 동시에 RecyclerView item 생성
//                     */
//                    for(int i=0; i<list.length(); i++){
//                        System.out.println("들어옴222222222222");
//                        JSONObject item = list.getJSONObject(i);
//
//                        String group1 = item.getString("list");
//                        Log.e("유저그룹333: ", group1);
//
//                        DictionaryList dict = new DictionaryList(group1);
//                        dict.setUser(str_user);
//                        mDictionaryList.add(dict); //마지막 줄에 삽입됨 1
//                        mAdapterList.notifyDataSetChanged();  //마지막 줄에 삽입됨 2
//                        emptyRecycler.setVisibility(View.GONE);
//                        mRecyclerView.setVisibility(View.VISIBLE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        ResultActivityListRequest ResultActivityListRequest = new ResultActivityListRequest(str_user, str_group, responseListener);
//        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
//        queue.add(ResultActivityListRequest);

//        Log.e("어레이에 값 들어있나", String.valueOf(mDictionaryList.size()));
//        if(mDictionaryList.size()==0){
//            emptyRecycler.setVisibility(View.VISIBLE);
//            mRecyclerView.setVisibility(View.GONE);
//        } else {
//            emptyRecycler.setVisibility(View.GONE);
//            mRecyclerView.setVisibility(View.VISIBLE);
//        }

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
    }

    /**
     * ExpandablelistView에 대한 Data
     */
    private void createTitle() {
        ImageButton addButton = (ImageButton) findViewById(R.id.button_main_insert);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                View title_view = LayoutInflater.from(ResultActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(title_view);

                Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

                ButtonSubmit.setText("목록 추가");
                editTextID.setHint("추가하실 목록명을 입력해주세요.");

                final AlertDialog dialog = builder.create();

                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = editTextID.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(ResultActivity.this, "목록명를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            addItem(title, new String[]{}, R.color.blue, R.drawable.wedo_btn);
                                            dialog.dismiss();
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                @Override
                                                public void onResponse(String response) {
                                                }
                                            };
                                            //서버로 volley를 이용해서 요청을 함
                                            UserListAdd UserListAdd = new UserListAdd(str_user, str_group, title, responseListener);
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
                            ValidateList ValidateList = new ValidateList(str_user, str_group, title, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(ValidateList);
                        }
                    }
                });
                dialog.show();
            }
        });

        /**
         * 서버에서 데이터를 불러서 여기에 삽입한다.
         */
    }

    private void addItem(String title, final String[] subItems, final int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);
            tta = title;

            //We can create items in batch.
            item.createSubItems(subItems.length);
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems[i]);
            }
            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInsertDialog(new OnItemCreated() {
                        @Override
                        public String itemCreated(final String title) {

                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        Log.e("success", String.valueOf(success));
                                        if (success) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    final View newSubItem = item.createSubItem();
                                                    configureSubItem(item, newSubItem, title);
                                                }
                                            };
                                            UserScheduleAdd UserScheduleAdd = new UserScheduleAdd(str_user, str_group, tta, title, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(UserScheduleAdd);
                                        } else {
                                            Toast.makeText(ResultActivity.this, "일정명이 존재합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ValidateSchedule ValidateSchedule = new ValidateSchedule(str_user, str_group, tta, title, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(ValidateSchedule);
                            return title;
                        }
                    });
                }
            });

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
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
                                    mExpandingList.removeItem(item);
                                    dialog1.dismiss();
                                }
                            };
                            //서버로 volley를 이용해서 요청을 함
                            String a = title;
                            UserListRemove UserListRemove = new UserListRemove(str_user, str_group, a, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(UserListRemove);
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
        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, final String subTitle) {
        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
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
                                item.removeSubItem(view);
                                dialog1.dismiss();
                            }
                        };
                        //서버로 volley를 이용해서 요청을 함
                        String list = tta;
                        String schedule = subTitle;
                        UserScheduleRemove UserScheduleRemove = new UserScheduleRemove(str_user, str_group, list, schedule, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                        queue.add(UserScheduleRemove);
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String sub=subTitle;
//                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//                intent.putExtra("key",sub);
//                startActivity(intent);
            }
        });
    }

    private void showInsertDialog(final OnItemCreated positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

        View title_view = LayoutInflater.from(ResultActivity.this)
                .inflate(R.layout.edit_box, null, false);
        builder.setView(title_view);

        Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
        final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

        editTextID.setHint("추가하실 일정을 입력하세요.");
        ButtonSubmit.setText("일정 추가");

        final AlertDialog dialog = builder.create();


        // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                positive.itemCreated(editTextID.getText().toString());
                String title = editTextID.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(ResultActivity.this, "일정명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }

    interface OnItemCreated {
        String itemCreated(String title);
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