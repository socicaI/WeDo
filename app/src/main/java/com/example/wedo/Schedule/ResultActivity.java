package com.example.wedo.Schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.wedo.ActivityInfo.ActivityInfo;
import com.example.wedo.Chating.InviteesChating;
import com.example.wedo.GroupHttp.MainCategoryGroupRequest;
import com.example.wedo.GroupHttp.UserGroupRemove;
import com.example.wedo.GroupHttp.UserGroupUpdate;
import com.example.wedo.GroupHttp.ValidateGroup;
import com.example.wedo.ListHttp.ResultActivityListRequest;
import com.example.wedo.ListHttp.UserListAdd;
import com.example.wedo.ListHttp.UserListRemove;
import com.example.wedo.ListHttp.UserListUpdate;
import com.example.wedo.ListHttp.ValidateList;
import com.example.wedo.Group.MainCategoryActivity;
import com.example.wedo.R;
import com.example.wedo.ScheduleHttp.DeviceInfoUpdate;
import com.example.wedo.ScheduleHttp.ScheduleComplete;
import com.example.wedo.ScheduleHttp.ScheduleinComplete;
import com.example.wedo.ScheduleHttp.UserScheduleAdd;
import com.example.wedo.ScheduleHttp.UserScheduleRemove;
import com.example.wedo.ScheduleHttp.UserScheduleUpdate;
import com.example.wedo.ScheduleHttp.ValidateSchedule;
import com.example.wedo.SearchFilter.UserSearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements OrderAdapter.onItemListener, SwipeRefreshLayout.OnRefreshListener {

    private ExpandingList mExpandingList;   //할 일 및 세부사항에 관한 ExpandingList
    public CheckBox checkBox;   //세부사항 체크박스
    private DrawerLayout drawerLayout;
    private View drawerView, drawerGroupView;
    public String id;
    public String nick;
    public String profilePath;
    public String userEmail;
    public String userID;
    public String userPass;
    public String orderNick;
    public String TitleProfile;
    public String people;   //팀원 수
    public String str_group, str_user, str_profile, order_user; //주제, 사용자 이름, 프로필
    private ArrayList<TaskModel> tasks;
    public String mainTitle = "a";
    Double trueCheck = 0.0;
    private List<OrderInvitees> OrderItemList;   //반장 리스트
    private OrderAdapter adapter;   //초대된 리스트와 관련된 어댑터
    private InviteesAdapter inviteesAdapter;    //초대받은사람 어댑터
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView emptyRecycler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        iniView();
        load();
        invitees();
        activityInfo();
    }

    /**
     * 활동내역 버튼을 눌렀을 경우
     */
    private void activityInfo() {
        LinearLayout activityInfo = (LinearLayout) findViewById(R.id.activityInfo);
        activityInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityInfo.class);
                intent.putExtra("id", id);
                intent.putExtra("nick", nick);
                intent.putExtra("orderNick", orderNick);
                intent.putExtra("profilePath", profilePath);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("TitleProfile", TitleProfile);
                intent.putExtra("userID", userID);
                intent.putExtra("userPass", userPass);
                intent.putExtra("people", people);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 할 일 및 세부사항을 서버에서 불러오게 해주는 메소드
     */
    private void load() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        emptyRecycler = (TextView) findViewById(R.id.emptyRecycler);
        mSwipeRefreshLayout.setOnRefreshListener(ResultActivity.this);
        mExpandingList.removeAllViews();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray list = jsonObject.getJSONArray("Schedule_List");
                    /**
                     * 서버에 불러온 Schedule_List의 크기 만큼 돌아 tasksArray에 추가 해주는 반복문
                     */
                    for (int i = 0; i < list.length(); i++) {
                        String tempTaskName = list.getJSONObject(i).getString("userlist");
                        TaskModel tempTaskModel = new TaskModel();
                        if (i > 0) {
                            if (tasks.get(tasks.size() - 1).getTitle().equals(tempTaskName)) {
                                tasks.get(tasks.size() - 1).addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                                tasks.get(tasks.size() - 1).addBooleanValue(list.getJSONObject(i).getString("complete"));
                            } else {
                                tempTaskModel.setTitle(tempTaskName);
                                if (!list.getJSONObject(i).getString("userSchedule").equals("null")) {
                                    tempTaskModel.addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                                    tempTaskModel.addBooleanValue(list.getJSONObject(i).getString("complete"));
                                }
                                tasks.add(tempTaskModel);
                                System.out.println("크기: "+tasks.size());
                            }
                        } else {
                            tempTaskModel.setTitle(tempTaskName);
                            if (!list.getJSONObject(i).getString("userSchedule").equals("null")) {
                                tempTaskModel.addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                                tempTaskModel.addBooleanValue(list.getJSONObject(i).getString("complete"));
                            }
                            tasks.add(tempTaskModel);
                            System.out.println("크기: "+tasks.size());
                        }
                    }
                    /**tasksArray의 크기 만큼 돌면서 해당 할 일 및 세부사항을 생성하는 반복문*/
                    for (int i = 0; i < tasks.size(); i++) {
                        addItem(tasks.get(i).getTitle(), tasks.get(i).getSubTitleArray(), R.color.grey, R.drawable.wedo_img, tasks.get(i).getBooleanValueArray(), "0%");
                    }
                    if (tasks.size() == 0) {
                        emptyRecycler.setVisibility(View.VISIBLE);
                        mExpandingList.setVisibility(View.GONE);
                    } else {
                        emptyRecycler.setVisibility(View.GONE);
                        mExpandingList.setVisibility(View.VISIBLE);
                    }
                    tasks.clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ResultActivityListRequest ResultActivityListRequest = new ResultActivityListRequest(order_user, str_group, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
        queue.add(ResultActivityListRequest);
    }

    /**
     * 할 일 추가 메소드
     */
    private void createTitle() {
        ImageButton addButton = (ImageButton) findViewById(R.id.button_main_insert);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success || nick.equals(orderNick)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                                View title_view = LayoutInflater.from(ResultActivity.this)
                                        .inflate(R.layout.edit_box, null, false);
                                builder.setView(title_view);

                                Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                                final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

                                ButtonSubmit.setText("할 일 추가");
                                editTextID.setHint(" 할 일을 입력해주세요.");

                                final AlertDialog dialog = builder.create();

                                /**할 일 추가 버튼*/
                                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String title = editTextID.getText().toString();
                                        if (title.equals("")) {
                                            Toast.makeText(ResultActivity.this, "할 일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonResponse = new JSONObject(response);
                                                        boolean success = jsonResponse.getBoolean("success");
                                                        if (success) {
                                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                    emptyRecycler.setVisibility(View.GONE);
                                                                    mExpandingList.setVisibility(View.VISIBLE);
                                                                    addItem(title, new String[]{}, R.color.grey, R.drawable.wedo_img, null, "0%");
                                                                    dialog.dismiss();
                                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                        }
                                                                    };
                                                                    //서버로 volley를 이용해서 요청을 함
                                                                    UserListAdd UserListAdd = new UserListAdd(order_user, str_group, title, responseListener);
                                                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                                    queue.add(UserListAdd);
                                                                }
                                                            };
                                                            CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, "null", "추가", responseListener);
                                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                            queue.add(CheckLogInfoRequest);
                                                        } else {
                                                            Toast.makeText(ResultActivity.this, "동일한 할 일이 존재합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            ValidateList ValidateList = new ValidateList(order_user, str_group, title, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(ValidateList);
                                        }
                                    }
                                });
                                dialog.show();
                            } else {
                                Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                kickBack();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Bundle extras = getIntent().getExtras();
                InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(InviteesCheck);
            }
        });
    }

    /**
     * 할 일 추가, 수정, 삭제와 세부사항 추가
     * 추가한 할 일 및 세부사항을 사용자에게 보여줌
     */
    private void addItem(String title, final String[] subItems, final int colorRes, int iconRes, final String[] booleanValue, String percent) {
        //Let's create an item with R.layout.expanding_layout
        ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);
        String grey = "#D8D8D8";
        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);
            ((TextView) item.findViewById(R.id.percent)).setText(percent);
            //할 일 생성할 때 색
            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(grey));

            ImageView upImg = (ImageView) item.findViewById(R.id.up2);
            ImageView downImg = (ImageView) item.findViewById(R.id.down);

            /**해당 할 일를 클릭할 때마다 바뀌는 upImg, downImg 이미지*/
            ((TextView) item.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success || nick.equals(orderNick)) {
                                    if (item.getSubItemsCount() > 0) {
                                        if (item.isExpanded()) {
                                            item.toggleExpanded();
                                            upImg.setVisibility(View.GONE);
                                            downImg.setVisibility(View.VISIBLE);
                                        } else {
                                            item.toggleExpanded();
                                            upImg.setVisibility(View.VISIBLE);
                                            downImg.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    kickBack();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Bundle extras = getIntent().getExtras();
                    InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(InviteesCheck);
                }
            });

            item.createSubItems(subItems.length);

            /**해당 할 일의 세부사항을 보여주는 반복문*/
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems[i], title, booleanValue[i]);
            }

            /**할 일 수정*/
            item.findViewById(R.id.update_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success || nick.equals(orderNick)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                                    View title_view = LayoutInflater.from(ResultActivity.this)
                                            .inflate(R.layout.edit_box, null, false);
                                    builder.setView(title_view);
                                    Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                                    EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);
                                    editTextID.setHint(title);
                                    ButtonSubmit.setText("수정하기");
                                    AlertDialog dialog = builder.create();
                                    // 다이얼로그에 있는 삽입 버튼을 클릭하면
                                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String List = editTextID.getText().toString();
                                            if (title.equals("")) {
                                                Toast.makeText(ResultActivity.this, "할 일을 입력하세요.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success = jsonResponse.getBoolean("success");
                                                            if (success) {
                                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                    @Override
                                                                    public void onResponse(String response) {
                                                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                //스플레쉬
                                                                                Intent intent = new Intent(getApplicationContext(), Loading.class);
                                                                                intent.putExtra("id", id);
                                                                                intent.putExtra("nick", nick);
                                                                                intent.putExtra("orderNick", orderNick);
                                                                                intent.putExtra("profilePath", profilePath);
                                                                                intent.putExtra("userEmail", userEmail);
                                                                                intent.putExtra("userID", userID);
                                                                                intent.putExtra("TitleProfile", TitleProfile);
                                                                                intent.putExtra("people", people);
                                                                                intent.putExtra("userPass", userPass);
                                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            }
                                                                        };
                                                                        //서버로 volley를 이용해서 요청을 함
                                                                        UserListUpdate UserListUpdate = new UserListUpdate(order_user, str_group, title, List, responseListener);
                                                                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                                        queue.add(UserListUpdate);
                                                                    }
                                                                };
                                                                CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, "null", "수정", responseListener);
                                                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                                queue.add(CheckLogInfoRequest);
                                                            } else {
                                                                Toast.makeText(ResultActivity.this, "동일한 할 일이 존재합니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };
                                                ValidateList ValidateList = new ValidateList(order_user, str_group, List, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                queue.add(ValidateList);
                                            }
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    kickBack();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Bundle extras = getIntent().getExtras();
                    InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(InviteesCheck);
                }
            });

            /**세부사항 추가*/
            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success || nick.equals(orderNick)) {
                                    showInsertDialog(new OnItemCreated() {
                                        @Override
                                        public String itemCreated(final String List) {
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
                                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            final View newSubItem = item.createSubItem();
                                                                            configureSubItem(item, newSubItem, List, title, "false");

                                                                            /** 삽입 */
                                                                            Intent intent = new Intent(getApplicationContext(), Loading.class);
                                                                            intent.putExtra("id", id);
                                                                            intent.putExtra("nick", nick);
                                                                            intent.putExtra("orderNick", orderNick);
                                                                            intent.putExtra("profilePath", profilePath);
                                                                            intent.putExtra("userEmail", userEmail);
                                                                            intent.putExtra("TitleProfile", TitleProfile);
                                                                            intent.putExtra("userID", userID);
                                                                            intent.putExtra("people", people);
                                                                            intent.putExtra("userPass", userPass);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    };
                                                                    UserScheduleAdd UserScheduleAdd = new UserScheduleAdd(order_user, str_group, title, List, responseListener);
                                                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                                    queue.add(UserScheduleAdd);
                                                                }
                                                            };
                                                            CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, List, "추가", responseListener);
                                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                            queue.add(CheckLogInfoRequest);
                                                        } else {
                                                            Toast.makeText(ResultActivity.this, "동일한 세부사항이 존재합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            ValidateSchedule ValidateSchedule = new ValidateSchedule(order_user, str_group, title, List, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(ValidateSchedule);
                                            return title;
                                        }
                                    });
                                } else {
                                    Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    kickBack();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Bundle extras = getIntent().getExtras();
                    InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(InviteesCheck);
                }
            });

            /** 할 일 삭제 */
            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success || nick.equals(orderNick)) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ResultActivity.this);
                                    View view1 = LayoutInflater.from(ResultActivity.this)
                                            .inflate(R.layout.edit_box2, null, false);
                                    builder1.setView(view1);

                                    final Button ButtonSubmit1 = (Button) view1.findViewById(R.id.button_remove_submit);
                                    final Button ButtonSubmit2 = (Button) view1.findViewById(R.id.button_cancel_submit);

                                    final AlertDialog dialog1 = builder1.create();
                                    ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                        @Override
                                                        public void onResponse(String response) {
                                                            mExpandingList.removeItem(item);
                                                            dialog1.dismiss();
                                                            if (mExpandingList.getItemsCount() == 0) {
                                                                emptyRecycler.setVisibility(View.VISIBLE);
                                                                mExpandingList.setVisibility(View.GONE);
                                                            } else {
                                                                emptyRecycler.setVisibility(View.GONE);
                                                                mExpandingList.setVisibility(View.VISIBLE);
                                                            }
                                                        }
                                                    };
                                                    //서버로 volley를 이용해서 요청을 함
                                                    String a = title;
                                                    UserListRemove UserListRemove = new UserListRemove(order_user, str_group, a, responseListener);
                                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                    queue.add(UserListRemove);
                                                }
                                            };
                                            String b = title;
                                            CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, b, "null", "삭제", responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(CheckLogInfoRequest);
                                        }
                                    });
                                    ButtonSubmit2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog1.dismiss();
                                        }
                                    });
                                    dialog1.show();
                                } else {
                                    Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                    kickBack();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    Bundle extras = getIntent().getExtras();
                    InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(InviteesCheck);
                }
            });
        }
    }

    /**
     * 세부사항 수정, 삭제, 체크박스 관리
     */
    private void configureSubItem(final ExpandingItem item, final View view, final String subTitle, final String title, String check) {

        String grey = "#D8D8D8";
        String black = "#9E9090";
        String blue = "#03A9F4";
        String yellow = "#F8D411";
//        String green = "#FFFFFFFF";

        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        ((TextView) item.findViewById(R.id.title)).setText(title);
        checkBox = view.findViewById(R.id.checkBox);

        if (trueCheck == 0.0) {
            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(grey));
            item.setIndicatorColorRes(R.color.grey);
        }

        for (int i = 0; i < item.getSubItemsCount(); i++) {
            if (check.equals("true")) {
                checkBox.setChecked(true);
                ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(((TextView) view.findViewById(R.id.sub_title)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(grey));
                if (!mainTitle.equals(title)) {
                    mainTitle = title;
                    trueCheck = 0.0;
                    trueCheck++;
                } else {
                    trueCheck++;
                }
                DecimalFormat form = new DecimalFormat("#");

                if (trueCheck < item.getSubItemsCount()) {

                    ((TextView) item.findViewById(R.id.percent)).setText(form.format(trueCheck * 100 / item.getSubItemsCount()) + "%");
                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(yellow));
                    item.setIndicatorColor(Color.parseColor(yellow));
                }
                if (trueCheck == item.getSubItemsCount()) {

                    ((TextView) item.findViewById(R.id.percent)).setText(form.format(trueCheck * 100 / item.getSubItemsCount()) + "%");
                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));
                    item.setIndicatorColor(Color.parseColor(blue));
                }
                break;
            }
            if (check.equals("false")) {
                checkBox.setChecked(false);
                ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(0);
                ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(black));
            }
        }

        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success || nick.equals(orderNick)) {
                                item.getSubItemsCount();
                                if (((CheckBox) v).isChecked()) {
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(((TextView) view.findViewById(R.id.sub_title)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                            ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(grey));

                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        double checkSchedule = Double.parseDouble(response);
                                                        DecimalFormat form = new DecimalFormat("#");

                                                        if (checkSchedule < item.getSubItemsCount()) {
                                                            ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                                            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(yellow));
                                                            item.setIndicatorColor(Color.parseColor(yellow));
                                                        }
                                                        if (checkSchedule == item.getSubItemsCount()) {
                                                            ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                                            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));
                                                            item.setIndicatorColor(Color.parseColor(blue));
                                                        } else {
                                                            ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(grey));
                                                        }
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            };
                                            ScheduleComplete ScheduleComplete = new ScheduleComplete(order_user, str_group, title, subTitle, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(ScheduleComplete);
                                        }
                                    };
                                    CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, subTitle, "체크", responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                    queue.add(CheckLogInfoRequest);
                                } else {
                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(0);
                                            ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(black));

                                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    double checkSchedule = Double.parseDouble(response);
                                                    DecimalFormat form = new DecimalFormat("#");

                                                    if (checkSchedule < item.getSubItemsCount()) {
                                                        ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                                        ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(yellow));
                                                        item.setIndicatorColor(Color.parseColor(yellow));
                                                    }
                                                    if (checkSchedule == item.getSubItemsCount()) {
                                                        ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                                        ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));
                                                        item.setIndicatorColor(Color.parseColor(blue));
                                                    }
                                                    if (checkSchedule * 100 / item.getSubItemsCount() == 0.0) {
                                                        ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                                        ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(grey));
                                                        item.setIndicatorColorRes(R.color.grey);
                                                    }
                                                }
                                            };
                                            ScheduleinComplete ScheduleinComplete = new ScheduleinComplete(order_user, str_group, title, subTitle, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(ScheduleinComplete);
                                        }
                                    };
                                    CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, subTitle, "체크 해제", responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                    queue.add(CheckLogInfoRequest);
                                }
                            } else {
                                Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                kickBack();
                                if (check.equals("true")) {
                                    checkBox.setChecked(true);
                                }
                                if (check.equals("false")) {
                                    checkBox.setChecked(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Bundle extras = getIntent().getExtras();
                InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(InviteesCheck);
            }
        });

//        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        view.findViewById(R.id.update_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success || nick.equals(orderNick)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

                                View title_view = LayoutInflater.from(ResultActivity.this)
                                        .inflate(R.layout.edit_box, null, false);
                                builder.setView(title_view);

                                Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
                                final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

                                editTextID.setHint(subTitle);
                                ButtonSubmit.setText("세부사항 수정");

                                final AlertDialog dialog = builder.create();
                                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
                                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String subtitle = editTextID.getText().toString();
                                        if (title.equals("")) {
                                            Toast.makeText(ResultActivity.this, "세부사항을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                                                                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            final View newSubItem = item.createSubItem();
                                                                            configureSubItem(item, newSubItem, subtitle, title, "false");
                                                                            /**
                                                                             * 삽입
                                                                             */
                                                                            Intent intent = new Intent(getApplicationContext(), Loading.class);
                                                                            intent.putExtra("id", id);
                                                                            intent.putExtra("nick", nick);
                                                                            intent.putExtra("orderNick", orderNick);
                                                                            intent.putExtra("profilePath", profilePath);
                                                                            intent.putExtra("userEmail", userEmail);
                                                                            intent.putExtra("userID", userID);
                                                                            intent.putExtra("people", people);
                                                                            intent.putExtra("userPass", userPass);
                                                                            intent.putExtra("TitleProfile", TitleProfile);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    };
                                                                    UserScheduleUpdate UserScheduleUpdate = new UserScheduleUpdate(order_user, str_group, title, subTitle, subtitle, responseListener);
                                                                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                                    queue.add(UserScheduleUpdate);
                                                                }
                                                            };
                                                            CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, subTitle, "수정", responseListener);
                                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                            queue.add(CheckLogInfoRequest);
                                                        } else {
                                                            Toast.makeText(ResultActivity.this, "동일한 세부사항이 존재합니다.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            };
                                            ValidateSchedule ValidateSchedule = new ValidateSchedule(order_user, str_group, title, subtitle, responseListener);
                                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                            queue.add(ValidateSchedule);
                                        }
                                    }
                                });
                                dialog.show();
                            } else {
                                Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                kickBack();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Bundle extras = getIntent().getExtras();
                InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(InviteesCheck);
            }
        });

        /**
         * 세부사항 삭제
         */
        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success || nick.equals(orderNick)) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ResultActivity.this);
                                View view1 = LayoutInflater.from(ResultActivity.this)
                                        .inflate(R.layout.edit_box2, null, false);
                                builder1.setView(view1);
                                final Button ButtonSubmit1 = (Button) view1.findViewById(R.id.button_remove_submit);
                                final Button ButtonSubmit2 = (Button) view1.findViewById(R.id.button_cancel_submit);

                                final AlertDialog dialog1 = builder1.create();
                                ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                                    @Override
                                                    public void onResponse(String response) {
                                                        item.removeSubItem(view);
                                                        dialog1.dismiss();
                                                        Intent intent = new Intent(getApplicationContext(), Loading.class);
                                                        intent.putExtra("id", id);
                                                        intent.putExtra("nick", nick);
                                                        intent.putExtra("orderNick", orderNick);
                                                        intent.putExtra("profilePath", profilePath);
                                                        intent.putExtra("userEmail", userEmail);
                                                        intent.putExtra("TitleProfile", TitleProfile);
                                                        intent.putExtra("people", people);
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
                                                String schedule = subTitle;

                                                UserScheduleRemove UserScheduleRemove = new UserScheduleRemove(order_user, str_group, title, schedule, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                queue.add(UserScheduleRemove);
                                            }
                                        };
                                        String schedule = subTitle;
                                        CheckLogInfoRequest CheckLogInfoRequest = new CheckLogInfoRequest(order_user, str_group, str_user, title, schedule, "삭제", responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                        queue.add(CheckLogInfoRequest);
                                    }
                                });
                                ButtonSubmit2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog1.dismiss();
                                    }
                                });
                                dialog1.show();
                            } else {
                                Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                kickBack();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Bundle extras = getIntent().getExtras();
                InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(InviteesCheck);
            }
        });
    }

    /**
     * 주제 추가에 대한 AlertDialog
     */
    private void showInsertDialog(final OnItemCreated positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);

        View title_view = LayoutInflater.from(ResultActivity.this)
                .inflate(R.layout.edit_box, null, false);
        builder.setView(title_view);

        Button ButtonSubmit = (Button) title_view.findViewById(R.id.button_dialog_submit);
        final EditText editTextID = (EditText) title_view.findViewById(R.id.mesgase);

        editTextID.setHint("세부사항을 추가해주세요.");
        ButtonSubmit.setText("세부사항 추가");

        final AlertDialog dialog = builder.create();

        // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면
        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextID.getText().toString();
                if (title.equals("")) {
                    Toast.makeText(ResultActivity.this, "세부사항을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    positive.itemCreated(editTextID.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onRefresh() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success || nick.equals(orderNick)) {
                        trueCheck = 0.0;
                        iniView();
                        load();
                        invitees();
                    } else {
                        Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                        kickBack();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Bundle extras = getIntent().getExtras();
        InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
        RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
        queue.add(InviteesCheck);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 사용자가 입력한 할 일 및 세부사항 명을 출력해주기 위해 필요한 메소드
     */
    interface OnItemCreated {
        String itemCreated(String title);
    }

    /**
     * 뒤로 가기 버튼을 눌렀을 때 사용자 프로필, userID, 사용자 이름, userPass, userEmail를 Intent로 전달
     */
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

    public void iniView() {
        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //주제
        nick = extras.getString("nick");    //사용자 이름
        orderNick = extras.getString("orderNick");  //주제 반장 이름
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");  //사용자 이메일
        userID = extras.getString("userID");    //사용자 Id
        userPass = extras.getString("userPass");    //사용자 Pass
        TitleProfile = extras.getString("TitleProfile"); //주제 만든이의 프로필
        people = extras.getString("people");
        tasks = new ArrayList<>();  // Task Model 클래스 ArrayList


        /** ExpandingList */
        mExpandingList = findViewById(R.id.expanding_list_main);
        createTitle();

        str_group = id; //주제
        str_user = nick;    //사용자 이름
        order_user = orderNick; //주제 팀장 명
        str_profile = profilePath;   //프로필
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        drawerGroupView = (View) findViewById(R.id.drawer_group);
        ImageView userProfileImg = (ImageView) findViewById(R.id.userProfileImg);   //사용자 프로필 or 주제 초대된 사용자 프로필
        TextView userNickName = (TextView) findViewById(R.id.userNickName); //사용자 이름 or 주제 초대된 사용자 이름
        Button userGroupInvite = (Button) findViewById(R.id.userGroupInvite);   //주제 초대 버튼 (관리자만)
        ImageView btn_open = (ImageView) findViewById(R.id.btnOpen);    //채팅, 주제수정, 주제삭제를 보여주는 햄버거 버튼
        TextView textView = (TextView) findViewById(R.id.id);
        textView.setText(str_group);    //주제
        ImageView inviteProfile = (ImageView) findViewById(R.id.inviteProfile);
        LinearLayout OrderProfile = (LinearLayout) findViewById(R.id.OrderProfile);

        /**
         * 프로필 불러오는 부분
         */
        Uri uri = Uri.parse(TitleProfile);
        Glide.with(getApplicationContext())
                .load(uri)
                .into(inviteProfile);

        /** 초대된 주제을 확인 할 수 있는 인물 버튼 */
        inviteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitees();
                drawerLayout.openDrawer(drawerGroupView);

                /**사용자 프로필 or 주제 초대된 사용자 프로필*/
                Uri uri = Uri.parse(str_profile);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(userProfileImg);

                /**사용자 이름 or 주제 초대된 사용자 이름*/
                userNickName.setText(str_user);

                /**초대할 사용자를 검색하기 위해 검색 화면으로 전환*/
                if (nick.equals(orderNick)) {
                    OrderProfile.setVisibility(View.VISIBLE);
                    userGroupInvite.setVisibility(View.VISIBLE);
                    userGroupInvite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("nick", nick);
                            intent.putExtra("orderNick", orderNick);
                            intent.putExtra("profilePath", profilePath);
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("userID", userID);
                            intent.putExtra("userPass", userPass);
                            intent.putExtra("TitleProfile", TitleProfile);
                            intent.putExtra("people", people);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        /** 채팅, 주제수정, 주제 삭제가 있는 햄버거 버튼 */
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        /**
         * 채팅과 관련된 버튼
         */
        LinearLayout chatBtn = (LinearLayout) findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success || nick.equals(orderNick)) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                };
                                DeviceInfoUpdate DeviceInfoUpdate = new DeviceInfoUpdate(nick, orderNick, id, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                queue.add(DeviceInfoUpdate);

                                Intent intent = new Intent(getApplicationContext(), InviteesChating.class);
                                intent.putExtra("id", id);
                                intent.putExtra("nick", nick);
                                intent.putExtra("orderNick", orderNick);
                                intent.putExtra("profilePath", profilePath);
                                intent.putExtra("userEmail", userEmail);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPass", userPass);
                                intent.putExtra("TitleProfile", TitleProfile);
                                intent.putExtra("people", people);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ResultActivity.this, "강퇴되어 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                                kickBack();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Bundle extras = getIntent().getExtras();
                InviteesCheck InviteesCheck = new InviteesCheck(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(InviteesCheck);
            }
        });

        /**
         * 주제 팀장일 경우 주제 수정 및 삭제 버튼
         * 주제 팀장이 아닐 경우 주제 나가기 버튼
         * */
        if (nick.equals(orderNick)) {
            /** 주제 수정 */
            LinearLayout updateGroup = (LinearLayout) findViewById(R.id.updateGroup);
            updateGroup.setVisibility(View.VISIBLE);
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

                    //주제 수정 버튼
                    ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String strID = editTextID.getText().toString();
                            if (strID.equals("")) {
                                Toast.makeText(ResultActivity.this, "주제를 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                                                        Intent intent = new Intent(getApplicationContext(), Loading.class);
                                                        intent.putExtra("id", strID);
                                                        intent.putExtra("nick", nick);
                                                        intent.putExtra("orderNick", orderNick);
                                                        intent.putExtra("profilePath", profilePath);
                                                        intent.putExtra("userEmail", userEmail);
                                                        intent.putExtra("TitleProfile", TitleProfile);
                                                        intent.putExtra("userID", userID);
                                                        intent.putExtra("userPass", userPass);
                                                        intent.putExtra("people", people);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                };
                                                //서버로 volley를 이용해서 요청을 함
                                                UserGroupUpdate UserGroupUpdate = new UserGroupUpdate(str_user, str_group, strID, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                                queue.add(UserGroupUpdate);
                                                textView.setText(strID);
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(ResultActivity.this, "동일한 주제가 존재합니다.", Toast.LENGTH_SHORT).show();
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
                        }
                    });
                    dialog.show();
                }
            });

            /** 주제 삭제 */
            LinearLayout removeGroup = (LinearLayout) findViewById(R.id.removeGroup);
            removeGroup.setVisibility(View.VISIBLE);
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

                    //주제 삭제 버튼
                    ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
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
                            UserGroupRemove UserGroupRemove = new UserGroupRemove(str_user, str_group, str_group + "of" + str_user, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(UserGroupRemove);
                        }
                    });

                    //주제 삭제 취소 버튼
                    ButtonSubmit2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog1.dismiss();
                        }
                    });
                    dialog1.show();
                }
            });
        } else {
            LinearLayout leaveGroup = (LinearLayout) findViewById(R.id.leaveGroup);
            leaveGroup.setVisibility(View.VISIBLE);
            leaveGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ResultActivity.this);
                    View view1 = LayoutInflater.from(ResultActivity.this)
                            .inflate(R.layout.edit_box2, null, false);
                    builder1.setView(view1);

                    final TextView out = (TextView) view1.findViewById(R.id.delete_text);
                    out.setText("나가시겠습니까?");
                    final Button ButtonSubmit1 = (Button) view1.findViewById(R.id.button_remove_submit);
                    final Button ButtonSubmit2 = (Button) view1.findViewById(R.id.button_cancel_submit);

                    final AlertDialog dialog1 = builder1.create();
                    ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                @Override
                                public void onResponse(String response) {
                                    dialog1.dismiss();
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
                            int people_count = Integer.parseInt(people) - 1;
                            people = String.valueOf(people_count);
                            LeaveGroupRequest LeaveGroupRequest = new LeaveGroupRequest(str_user, orderNick, id, people, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(LeaveGroupRequest);
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

    /**
     * 초대된 사용자
     */
    public void invitees() {
        if (nick.equals(orderNick)) {
            //recyclerview
            RecyclerView recyclerView = findViewById(R.id.inviteesInfo);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(ResultActivity.this).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

            //adapter
            OrderItemList = new ArrayList<>();
            adapter = new OrderAdapter(OrderItemList);    //생성된 item들을 adapter에서 생성
            recyclerView.setLayoutManager(layoutManager);   //recyclerView에 item을 Linear형식으로 만듦
            recyclerView.setAdapter(adapter);
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
                            OrderItemList.add(new OrderInvitees(list.getJSONObject(i).getString("profilePath"), list.getJSONObject(i).getString("invitees")));
                            adapter.setOnClickListener(ResultActivity.this);    //어댑터의 리스너 호출
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Bundle extras = getIntent().getExtras();
            OrderRequest OrderRequest = new OrderRequest(extras.getString("orderNick"), extras.getString("id"), responseListener);
            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
            queue.add(OrderRequest);
        } else {
            Bundle extras = getIntent().getExtras();
            //recyclerview
            RecyclerView recyclerView = findViewById(R.id.inviteesInfo);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

            //adapter
            OrderItemList = new ArrayList<>();

            /**팀장 아이템 생성*/
            OrderItemList.add(new OrderInvitees(extras.getString("profilePath"), extras.getString("orderNick") + "(방장)"));
            inviteesAdapter = new InviteesAdapter(OrderItemList);    //생성된 item들을 adapter에서 생성
            recyclerView.setLayoutManager(layoutManager);   //recyclerView에 item을 Linear형식으로 만듦
            recyclerView.setAdapter(inviteesAdapter);
            DividerItemDecoration dividerItemDecoration =
                    new DividerItemDecoration(getApplicationContext(), new LinearLayoutManager(ResultActivity.this).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);

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
                            OrderItemList.add(new OrderInvitees(list.getJSONObject(i).getString("profilePath"), list.getJSONObject(i).getString("invitees")));
                        }
                        inviteesAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            InviteesRequest InviteesRequest = new InviteesRequest(extras.getString("nick"), extras.getString("orderNick"), extras.getString("id"), responseListener);
            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
            queue.add(InviteesRequest);
        }
    }

    /**
     * 주제 반장이 주제에서 특정 사용자를 내보낼 수 있는 메소드
     */
    @Override
    public void onItemClicked(int position) {
        OrderInvitees model = OrderItemList.get(position);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(ResultActivity.this);
        View view1 = LayoutInflater.from(ResultActivity.this)
                .inflate(R.layout.edit_box2, null, false);
        builder1.setView(view1);

        final TextView out = (TextView) view1.findViewById(R.id.delete_text);
        out.setText("내보내시겠습니까?");
        final Button ButtonSubmit1 = (Button) view1.findViewById(R.id.button_remove_submit);
        final Button ButtonSubmit2 = (Button) view1.findViewById(R.id.button_cancel_submit);

        final AlertDialog dialog1 = builder1.create();
        ButtonSubmit1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        OrderItemList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ResultActivity.this, model.getText() + "을 내보냈습니다.", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                    }
                };
                //서버로 volley를 이용해서 요청을 함
                int people_count = Integer.parseInt(people) - 1;
                people = String.valueOf(people_count);
                ThrowOutRequest ThrowOutRequest = new ThrowOutRequest(model.getText(), orderNick, id, people, responseListener);
                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                queue.add(ThrowOutRequest);
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

    public void kickBack() {
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
