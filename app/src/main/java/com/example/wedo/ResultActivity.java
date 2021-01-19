package com.example.wedo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ResultActivity extends AppCompatActivity {
    /**
     * ExpandingListView
     */
    ArrayList<String> scheduleDemo = new ArrayList<>();
    private ExpandingList mExpandingList;
    String tta;
    String schedule1;

    /**
     * RecyclerView 부분
     */
    private ArrayList<DictionaryList> mDictionaryList;
    private CustomAdapterList mAdapterList;
    private int count = -1;
    private String TAG_JSON = "userlist";
    private String tagSchedule = "userSchedule";

    private DrawerLayout drawerLayout;
    private View drawerView;

    private int subItemLength;


    public String id, nick, profilePath, userEmail, userID, userPass;
    public String str_group, str_user, str_profile, strID3;

    private Context mContext;

    int x = -1;

    int o;
    private Activity mActivity;

    ProgressDialog progressDialog;

    // Task Model ArrayList
    private ArrayList<TaskModel> tasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("ProgressDialog running...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);


        mActivity = ResultActivity.this;

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");    //그룹명
        nick = extras.getString("nick");
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");
        userID = extras.getString("userID");
        userPass = extras.getString("userPass");
        tasks = new ArrayList<>();

        /**
         * ExpandingList
         */
        mExpandingList = findViewById(R.id.expanding_list_main);
        createTitle();

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
         * 서버에 저장되어 있는 목록/일정 데이터를 불러오는 부분
         */
        load();

        progressDialog.dismiss();


    }

    /**
     * 서버에 저장되어 있는 목록/일정 데이터를 불러오게 해주는 클래스
     */
    private void load() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("리스폰스 확인 : " + response);
                    JSONArray list = jsonObject.getJSONArray("Schedule_List");
                    /**
                     * 그룹 배열의 크기만큼 반복문을 돌려 데이터를 String에 넣어줌과 동시에 RecyclerView item 생성
                     */

                    for (int i = 0; i < list.length(); i++) {
                        String tempTaskName = list.getJSONObject(i).getString("userlist");
                        TaskModel tempTaskModel = new TaskModel();

                        if (i > 0) {
                            if (tasks.get(tasks.size() - 1).getTitle().equals(tempTaskName)) {
                                tasks.get(tasks.size() - 1).addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                            } else {
                                tempTaskModel.setTitle(tempTaskName);

                                if (!list.getJSONObject(i).getString("userSchedule").equals("null")) {
                                    tempTaskModel.addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                                }

                                tasks.add(tempTaskModel);
                            }
                        } else {
                            tempTaskModel.setTitle(tempTaskName);

                            if (!list.getJSONObject(i).getString("userSchedule").equals("null")) {
                                tempTaskModel.addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                            }

                            tasks.add(tempTaskModel);
                        }

                    }
                    System.out.println("할 일: " + tasks.size());
                    for (int i = 0; i < tasks.size(); i++) {

                        addItem(tasks.get(i).getTitle(), tasks.get(i).getSubTitleArray(), R.color.grey, R.drawable.wedo_btn);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        ResultActivityListRequest ResultActivityListRequest = new ResultActivityListRequest(str_user, str_group, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
        queue.add(ResultActivityListRequest);
        progressDialog.show();


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

                ButtonSubmit.setText("목차 추가");
                editTextID.setHint("추가하실 목차를 입력해주세요.");

                final AlertDialog dialog = builder.create();

                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = editTextID.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(ResultActivity.this, "목차를 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                        if (success) {
                                            addItem(title, new String[]{}, R.color.grey, R.drawable.wedo_btn);
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
                                            Toast.makeText(ResultActivity.this, "동일한 목차가 존재합니다.", Toast.LENGTH_SHORT).show();
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
    }

    private void addItem(String title, final String[] subItems, final int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

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
                configureSubItem(item, view, subItems[i], title);
            }


//            item.findViewById(R.id.a).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ImageView imageView = (ImageView) item.findViewById(R.id.updown_item);
//                    if(imageView.equals(R.drawable.ic_down)){
//                        imageView.setImageResource(R.drawable.ic_up_down);
//
//                    }else {
//                        imageView.setImageResource(R.drawable.ic_down);
//                    }
//                }
//            });


            item.findViewById(R.id.update_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                    View title_view = LayoutInflater.from(ResultActivity.this)
                            .inflate(R.layout.edit_box, null, false);
                    builder.setView(title_view);

                    Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                    final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

                    editTextID.setHint(title);
                    ButtonSubmit.setText("수정하기");

                    final AlertDialog dialog = builder.create();


                    // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String title1 = editTextID.getText().toString();
                            if (title.equals("")) {
                                Toast.makeText(ResultActivity.this, "목차를 입력하세요.", Toast.LENGTH_SHORT).show();
                            } else {
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
                                                        //스플레쉬
                                                        Intent intent = new Intent(getApplicationContext(), Splash.class);
                                                        intent.putExtra("id", id);
                                                        intent.putExtra("nick", nick);
                                                        intent.putExtra("profilePath", profilePath);
                                                        intent.putExtra("userEmail", userEmail);
                                                        intent.putExtra("userID", userID);
                                                        intent.putExtra("userPass", userPass);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                };
                                                //서버로 volley를 이용해서 요청을 함
                                                UserListUpdate UserListUpdate = new UserListUpdate(str_user, str_group, title, title1, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                queue.add(UserListUpdate);
                                            } else {
                                                Toast.makeText(ResultActivity.this, "동일한 목차가 존재합니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                ValidateList ValidateList = new ValidateList(str_user, str_group, title1, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                queue.add(ValidateList);
                            }
                        }
                    });
                    dialog.show();
                }
            });

            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInsertDialog(new OnItemCreated() {
                        @Override
                        public String itemCreated(final String title1) {

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
                                                    configureSubItem(item, newSubItem, title1, title);
                                                    /**
                                                     * 삽입
                                                     */
                                                    Intent intent = new Intent(getApplicationContext(), Splash.class);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("nick", nick);
                                                    intent.putExtra("profilePath", profilePath);
                                                    intent.putExtra("userEmail", userEmail);
                                                    intent.putExtra("userID", userID);
                                                    intent.putExtra("userPass", userPass);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            };
                                            UserScheduleAdd UserScheduleAdd = new UserScheduleAdd(str_user, str_group, title, title1, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(UserScheduleAdd);
                                        } else {
                                            Toast.makeText(ResultActivity.this, "동일한 할 일이 존재합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ValidateSchedule ValidateSchedule = new ValidateSchedule(str_user, str_group, title, title1, responseListener);
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

            /**
             * 목록 수정 부분
             */

        }
    }

    private void configureSubItem(final ExpandingItem item, final View view, final String subTitle, final String title) {
//        TextView textView = (TextView)findViewById(R.id.sub_title);

        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        ((TextView) item.findViewById(R.id.title)).setText(title);

        String grey = "#808080";
        String black = "#000000";
        String tempColor = "#ff7f00";
        String green = "#00ac00";

        CheckBox checkBox = view.findViewById(R.id.checkBox);

        x = 0;
        int o = item.getSubItemsCount();

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBox.isChecked()) {
                    x++;
                    System.out.println("체크 확인: " + x + "개");
                    ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(((TextView) view.findViewById(R.id.sub_title)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(grey));
                    if (x >= 1) {
                        item.setIndicatorColor((Color.parseColor(tempColor)));
                    }
                } else {
                    x -= 1;
                    System.out.println("체크 풀림 확인: " + x);
                    ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(0);
                    ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(black));

                    if (x >= 1) {
                        item.setIndicatorColor((Color.parseColor(tempColor)));
                    }
                    if (x == o) {
                        item.setIndicatorColor((Color.parseColor(tempColor)));
                    }
                    if (x <= 0) {
                        item.setIndicatorColor((Color.parseColor(grey)));
                    }
                }
                if (x == o) {
                    item.setIndicatorColor((Color.parseColor(green)));
                }
            }
        });


        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.update_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                View title_view = LayoutInflater.from(ResultActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(title_view);

                Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

                editTextID.setHint(subTitle);
                ButtonSubmit.setText("할 일 추가");

                final AlertDialog dialog = builder.create();
                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title1 = editTextID.getText().toString();
                        if (title.equals("")) {
                            Toast.makeText(ResultActivity.this, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
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
                                                    configureSubItem(item, newSubItem, title1, title);
                                                    /**
                                                     * 삽입
                                                     */
                                                    Intent intent = new Intent(getApplicationContext(), Splash.class);
                                                    intent.putExtra("id", id);
                                                    intent.putExtra("nick", nick);
                                                    intent.putExtra("profilePath", profilePath);
                                                    intent.putExtra("userEmail", userEmail);
                                                    intent.putExtra("userID", userID);
                                                    intent.putExtra("userPass", userPass);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            };
                                            UserScheduleUpdate UserScheduleUpdate = new UserScheduleUpdate(str_user, str_group, title, subTitle, title1, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(UserScheduleUpdate);
                                        } else {
                                            Toast.makeText(ResultActivity.this, "동일한 할 일이 존재합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            ValidateSchedule ValidateSchedule = new ValidateSchedule(str_user, str_group, title, title1, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(ValidateSchedule);
                        }
                    }
                });
                dialog.show();
            }
        });

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
                                Intent intent = new Intent(getApplicationContext(), Splash.class);
                                intent.putExtra("id", id);
                                intent.putExtra("nick", nick);
                                intent.putExtra("profilePath", profilePath);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPass", userPass);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                finish();
                            }
                        };
                        /**
                         * sub 삭제
                         */
                        String list = item.toString();
                        String schedule = subTitle;

                        System.out.println("삭제: " + str_user);
                        System.out.println("삭제: " + str_group);
                        System.out.println("삭제: " + title);
                        System.out.println("삭제: " + schedule);

                        UserScheduleRemove UserScheduleRemove = new UserScheduleRemove(str_user, str_group, title, schedule, responseListener);
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
    }

    private void showInsertDialog(final OnItemCreated positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

        View title_view = LayoutInflater.from(ResultActivity.this)
                .inflate(R.layout.edit_box, null, false);
        builder.setView(title_view);

        Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
        final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

        editTextID.setHint("할 일을 추가해주세요.");
        ButtonSubmit.setText("할 일 추가");

        final AlertDialog dialog = builder.create();


        // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextID.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(ResultActivity.this, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    positive.itemCreated(editTextID.getText().toString());
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

    public void MatrixTime(int delayTime) {
        long saveTime = System.currentTimeMillis();
        long currTime = 0;
        while (currTime - saveTime < delayTime) {
            currTime = System.currentTimeMillis();
        }
    }

    public static void restartActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        }
    }
}
