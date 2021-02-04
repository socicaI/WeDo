package com.example.wedo.Schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
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
import com.example.wedo.ScheduleHttp.ScheduleComplete;
import com.example.wedo.ScheduleHttp.ScheduleinComplete;
import com.example.wedo.ScheduleHttp.UserScheduleAdd;
import com.example.wedo.ScheduleHttp.UserScheduleRemove;
import com.example.wedo.ScheduleHttp.UserScheduleUpdate;
import com.example.wedo.ScheduleHttp.ValidateSchedule;
import com.example.wedo.UserSearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private ExpandingList mExpandingList;   //목록 및 할 일에 관한 ExpandingList
    public CheckBox checkBox;   //할 일 체크박스
    private DrawerLayout drawerLayout;
    private View drawerView, drawerGroupView;
    public String id, nick, profilePath, userEmail, userID, userPass;   //그룹명, 사용자 이름, 프로필, 사용자 이메일, 사용자 Id, 사용자 Pass
    public String str_group, str_user, str_profile; //그룹명, 사용자 이름, 프로필
    private ArrayList<TaskModel> tasks;
    public String mainTitle = "a";
    public Double trueCheck = 0.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        iniView();
        load();
    }

    /**
     * 목차 및 할 일을 서버에서 불러오게 해주는 메소드
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
                            }
                        } else {
                            tempTaskModel.setTitle(tempTaskName);
                            if (!list.getJSONObject(i).getString("userSchedule").equals("null")) {
                                tempTaskModel.addSubTitle(list.getJSONObject(i).getString("userSchedule"));
                                tempTaskModel.addBooleanValue(list.getJSONObject(i).getString("complete"));
                            }
                            tasks.add(tempTaskModel);
                        }
                    }

                    /**tasksArray의 크기 만큼 돌면서 해당 목록 및 할 일을 생성하는 반복문*/
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println("tasks: " + tasks.size());
                        addItem(tasks.get(i).getTitle(), tasks.get(i).getSubTitleArray(), R.color.blue, R.drawable.wedo_img, tasks.get(i).getBooleanValueArray(), "0%");
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
     * 목차 추가 메소드
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

                /**목차 추가 버튼*/
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
                                            addItem(title, new String[]{}, R.color.blue, R.drawable.wedo_img, null, "0%");
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

    /**
     * 목차 추가, 수정, 삭제와 할 일 추가
     * 추가한 목차 및 할 일을 사용자에게 보여줌
     */
    private void addItem(String title, final String[] subItems, final int colorRes, int iconRes, final String[] booleanValue, String percent) {
        //Let's create an item with R.layout.expanding_layout
        ExpandingItem item = mExpandingList.createNewItem(R.layout.expanding_layout);

        String blue = "#7ED2FF";

        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);
            ((TextView) item.findViewById(R.id.percent)).setText(percent);
            //목차 생성할 때 색
            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));

            ImageView upImg = (ImageView) item.findViewById(R.id.up2);
            ImageView downImg = (ImageView) item.findViewById(R.id.down2);

            /**해당 목차를 클릭할 때마다 바뀌는 upImg, downImg 이미지*/
            ((TextView) item.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            item.createSubItems(subItems.length);

            /**해당 목차의 할 일을 보여주는 반복문*/
            for (int i = 0; i < item.getSubItemsCount(); i++) {
                //Let's get the created sub item by its index
                final View view = item.getSubItemView(i);

                //Let's set some values in
                configureSubItem(item, view, subItems[i], title, booleanValue[i]);
            }

            /**목차 수정*/
            item.findViewById(R.id.update_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
//                                                        ((TextView) item.findViewById(R.id.title)).setText(List);
//                                                        dialog.dismiss();
                                                        //스플레쉬
                                                        Intent intent = new Intent(getApplicationContext(), Loading.class);
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
                                                UserListUpdate UserListUpdate = new UserListUpdate(str_user, str_group, title, List, responseListener);
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
                                ValidateList ValidateList = new ValidateList(str_user, str_group, List, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                                queue.add(ValidateList);
                            }
                        }
                    });
                    dialog.show();
                }
            });

            /**할 일 추가*/
            item.findViewById(R.id.add_more_sub_items).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                                    final View newSubItem = item.createSubItem();
                                                    configureSubItem(item, newSubItem, List, title, "false");

                                                    /** 삽입 */
                                                    Intent intent = new Intent(getApplicationContext(), Loading.class);
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
                                            UserScheduleAdd UserScheduleAdd = new UserScheduleAdd(str_user, str_group, title, List, responseListener);
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
                            ValidateSchedule ValidateSchedule = new ValidateSchedule(str_user, str_group, title, List, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                            queue.add(ValidateSchedule);
                            return title;
                        }
                    });
                }
            });

            /** 목차 삭제 */
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

    /**
     * 할 일 수정, 삭제, 체크박스 관리
     */
    private void configureSubItem(final ExpandingItem item, final View view, final String subTitle, final String title, String check) {

        String grey = "#D8D8D8";
        String black = "#000000";
        String blue = "#7ED2FF";
        String orange = "#FFCD12";
        String green = "#1DDB16";

        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
        ((TextView) item.findViewById(R.id.title)).setText(title);
        checkBox = view.findViewById(R.id.checkBox);

        if (trueCheck == 0.0) {
            ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));
            item.setIndicatorColorRes(R.color.blue);
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
                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(orange));
                    item.setIndicatorColor(Color.parseColor(orange));
                }
                if (trueCheck == item.getSubItemsCount()) {
                    ((TextView) item.findViewById(R.id.percent)).setText(form.format(trueCheck * 100 / item.getSubItemsCount()) + "%");
                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(green));
                    item.setIndicatorColor(Color.parseColor(green));
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
                item.getSubItemsCount();
                if (((CheckBox) v).isChecked()) {
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
                                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(orange));
                                    item.setIndicatorColor(Color.parseColor(orange));
                                }
                                if (checkSchedule == item.getSubItemsCount()) {
                                    ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                    ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(green));
                                    item.setIndicatorColor(Color.parseColor(green));
                                } else {
                                    ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(grey));
                                }
                            } catch (Exception e) {
                                System.out.println("error message : " + e.getMessage());
                            }
                        }
                    };
                    ScheduleComplete ScheduleComplete = new ScheduleComplete(str_user, str_group, title, subTitle, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(ScheduleComplete);
                } else {
                    ((TextView) view.findViewById(R.id.sub_title)).setPaintFlags(0);
                    ((TextView) view.findViewById(R.id.sub_title)).setTextColor(Color.parseColor(black));

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            double checkSchedule = Double.parseDouble(response);
                            DecimalFormat form = new DecimalFormat("#");

                            if (checkSchedule < item.getSubItemsCount()) {
                                ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(orange));
                                item.setIndicatorColor(Color.parseColor(orange));
                            }
                            if (checkSchedule == item.getSubItemsCount()) {
                                ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(green));
                                item.setIndicatorColor(Color.parseColor(green));
                            }
                            if (checkSchedule * 100 / item.getSubItemsCount() == 0.0) {
                                ((TextView) item.findViewById(R.id.percent)).setText(form.format(checkSchedule * 100 / item.getSubItemsCount()) + "%");
                                ((TextView) item.findViewById(R.id.percent)).setTextColor(Color.parseColor(blue));
                                item.setIndicatorColorRes(R.color.blue);
                            }
                        }
                    };
                    ScheduleinComplete ScheduleinComplete = new ScheduleinComplete(str_user, str_group, title, subTitle, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(ResultActivity.this);
                    queue.add(ScheduleinComplete);
                }
            }
        });

//        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
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
                        String subtitle = editTextID.getText().toString();
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
                                                    configureSubItem(item, newSubItem, subtitle, title, "false");
                                                    /**
                                                     * 삽입
                                                     */
                                                    Intent intent = new Intent(getApplicationContext(), Loading.class);
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
                                            UserScheduleUpdate UserScheduleUpdate = new UserScheduleUpdate(str_user, str_group, title, subTitle, subtitle, responseListener);
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
                            ValidateSchedule ValidateSchedule = new ValidateSchedule(str_user, str_group, title, subtitle, responseListener);
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
                                Intent intent = new Intent(getApplicationContext(), Loading.class);
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
                        String schedule = subTitle;

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

    /**
     * 그룹 추가에 대한 AlertDialog
     */
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

    /**
     * 사용자가 입력한 목록 및 할 일 명을 출력해주기 위해 필요한 메소드
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
        finishAndRemoveTask();
    }

    public void iniView() {

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");    //그룹명
        nick = extras.getString("nick");    //사용자 이름
        profilePath = extras.getString("profilePath");  //프로필
        userEmail = extras.getString("userEmail");  //사용자 이메일
        userID = extras.getString("userID");    //사용자 Id
        userPass = extras.getString("userPass");    //사용자 Pass
        tasks = new ArrayList<>();  // Task Model 클래스 ArrayList

        /** ExpandingList */
        mExpandingList = findViewById(R.id.expanding_list_main);
        createTitle();

        str_group = id; //그룹명
        str_user = nick;    //사용자 이름
        str_profile = profilePath;   //프로필
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        drawerGroupView = (View) findViewById(R.id.drawer_group);
        ImageView userProfileImg = (ImageView) findViewById(R.id.userProfileImg);   //사용자 프로필 or 그룹 초대된 사용자 프로필
        TextView userNickName = (TextView) findViewById(R.id.userNickName); //사용자 이름 or 그룹 초대된 사용자 이름
        Button userGroupInvite = (Button) findViewById(R.id.userGroupInvite);   //그룹 초대 버튼 (관리자만)
        ImageView btn_open = (ImageView) findViewById(R.id.btnOpen);    //채팅, 그룹수정, 그룹삭제를 보여주는 햄버거 버튼
        TextView textView = (TextView) findViewById(R.id.id);
        textView.setText(str_group);    //그룹명
        ImageView groupInvite = (ImageView) findViewById(R.id.group_invite);

        /** 초대된 그룹을 확인 할 수 있는 인물 버튼 */
        groupInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerGroupView);

                /**사용자 프로필 or 그룹 초대된 사용자 프로필*/
                Uri uri = Uri.parse(str_profile);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(userProfileImg);

                /**사용자 이름 or 그룹 초대된 사용자 이름*/
                userNickName.setText(str_user);

                /**초대할 사용자를 검색하기 위해 검색 화면으로 전환*/
                userGroupInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), UserSearchActivity.class);
                        intent.putExtra("profileUri", str_profile);
                        intent.putExtra("ID", str_user);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("userID", userID);
                        intent.putExtra("userPass", userPass);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        /** 채팅, 그룹수정, 그룹 삭제가 있는 햄버거 버튼 */
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        /** 그룹 수정 */
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

                //그룹 수정 버튼
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String strID = editTextID.getText().toString();
                        if (strID.equals("")) {
                            Toast.makeText(ResultActivity.this, "그룹명을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
                    }
                });
                dialog.show();
            }
        });

        /** 그룹 삭제 */
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

                //그룹 삭제 버튼
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

                //그룹 삭제 취소 버튼
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
