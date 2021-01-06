package com.example.wedo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    /**
     * ExpandingListView
     */
    private ExpandingList mExpandingList;
    String tta;
    ArrayList<String> scheduleReal = new ArrayList<>();
    boolean ff = false;
    int[] ia = {0};
    String schedule1;
    int ii=0;

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
         * 서버에 list Data가 있는지 확인하고 가져오는 메소드
         */
        ArrayList<String> scheduleDemo = new ArrayList<>();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray(TAG_JSON);
                    /**
                     * 그룹 배열의 크기만큼 반복문을 돌려 데이터를 String에 넣어줌과 동시에 RecyclerView item 생성
                     */
                    for (int o = 0; o < list.length(); o++) {
                        final JSONObject[] item = {list.getJSONObject(o)};

                        String group1 = item[0].getString("list");
                        System.out.println("스케쥴11 : " + group1);
                        scheduleDemo.add(group1);
                        schedule1 = scheduleDemo.get(o);
                        System.out.println("야!!!!!!!!!!!!: " + schedule1);

                        /**
                         * group1과 관련된 schedule 데이터 가져오는 부분
                         */
//                        Response.Listener<String> responseListener1 = new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response1) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response1);
//                                    JSONArray schedule = jsonObject.getJSONArray("userSchedule");
//                                    System.out.println("스케줄 길이: " + schedule.length());
//
//
//                                    String scheduleArray[] = new String[schedule.length()];
//
//                                    /**
//                                     * 그룹 배열의 크기만큼 반복문을 돌려 데이터를 String에 넣어줌과 동시에 RecyclerView item 생성
//                                     */
//
//                                    for (int i = 0; i < schedule.length(); i++) {
//                                        JSONObject item = schedule.getJSONObject(i);
//                                        String Array = item.getString("schedule");
//                                        scheduleArray[i] = Array;
//                                        System.out.println("왐마!!!: "+scheduleArray[i]);
//                                    }
//
//                                    /**
//                                     * group1과 관련된 schedule 데이터 가져오는 부분
//                                     **/
//                                    addItem(scheduleDemo.get(ia[0]), scheduleArray, R.color.blue, R.drawable.wedo_btn);
//                                    ia[0]++;
//                                } catch (JSONException e) {
//                                    ff = true;
//                                    e.printStackTrace();
//                                }
//                                if (ff) {
//                                    System.out.println("그룹완 : " + scheduleDemo.get(ia[0]));
//                                    addItem(scheduleDemo.get(ia[0]), new String[]{}, R.color.blue, R.drawable.wedo_btn);
//                                    ia[0]++;
//                                }
//                                ff = false;
//                            }
//                        };
//
//                        System.out.println("ㅇ!!!!!!!!!!: " + str_user);
//                        System.out.println("ㅇ!!!!!!!!!!: " + str_group);
//                        System.out.println("ㅇ!!!!!!!!!!: " + scheduleDemo.get(ii));
//
//                        ResultActivitySchedultRequest ResultActivitySchedultRequest = new ResultActivitySchedultRequest(str_user, str_group, scheduleDemo.get(ii), responseListener1);
//                        ii++;
//                        RequestQueue queue1 = Volley.newRequestQueue(ResultActivity.this);
//                        queue1.add(ResultActivitySchedultRequest);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResultActivityListRequest ResultActivityListRequest = new ResultActivityListRequest(str_user, str_group, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
        queue.add(ResultActivityListRequest);
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
        /**
         * tta 추가되면 위의 있는 리스트뷰 최하위에 있는 애만 체크
         */
        tta = title;
        //Let's create an item with R.layout.expanding_layout
        final ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);

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
                                                    configureSubItem(item, newSubItem, title1);
                                                }
                                            };
                                            UserScheduleAdd UserScheduleAdd = new UserScheduleAdd(str_user, str_group, title, title1, responseListener);
                                            Log.e("갑자기 왜이래;;;", title);
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
                            Log.e("갑자기 왜이래;;;", title);
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
                        /**
                         * sub 삭제
                         */
                        String list = tta;
                        String schedule = subTitle;
                        Log.e("삭제삭제삭제", list);
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
}