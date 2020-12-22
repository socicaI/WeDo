package com.example.wedo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;

public class MainCategoryActivity extends AppCompatActivity {

    /**
     * RecyclerView 부분
     */
    private ArrayList<Dictionary> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;
    private TextView emptyView;


    private DrawerLayout drawerLayout;
    private View drawerView;
    private ImageView profile1, profile2;
    TextView ID;

    String strID;
    String profilePath; //프로필 Uri 경로
    String userID, userPass, userEmail;
    private long backKeyPressedTime = 0; //뒤로가기 2번 클릭 시 종료하기 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);

        /**
         * RecyclerView 부분
         */
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        //mAdapter = new CustomAdapter( mArrayList);
        mAdapter = new CustomAdapter(this, mArrayList);



        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Dictionary dict = mArrayList.get(position);
                Toast.makeText(getApplicationContext(), dict.getId() + ' ', Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getBaseContext(), ResultActivity.class);

                intent.putExtra("id", dict.getId());

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        ImageButton buttonInsert = (ImageButton) findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {


            // 1. 화면 아래쪽에 있는 데이터 추가 버튼을 클릭하면
            @Override
            public void onClick(View v) {


                // 2. 레이아웃 파일 edit_box.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.

                AlertDialog.Builder builder = new AlertDialog.Builder(MainCategoryActivity.this);

                View view = LayoutInflater.from(MainCategoryActivity.this)
                        .inflate(R.layout.edit_box, null, false);
                builder.setView(view);


                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                final EditText editTextID = (EditText) view.findViewById(R.id.mesgase);

                ButtonSubmit.setText("그룹 추가");

                final AlertDialog dialog = builder.create();


                // 3. 다이얼로그에 있는 삽입 버튼을 클릭하면

                ButtonSubmit.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        // 4. 사용자가 입력한 내용을 가져와서
                        String strID2 = editTextID.getText().toString();
                        // 5. ArrayList에 추가하고

                        Dictionary dict = new Dictionary(strID2);
//                        mArrayList.add(0, dict); //첫번째 줄에 삽입됨 1
                        dict.setUser(strID);

                        mArrayList.add(dict); //마지막 줄에 삽입됨 1



                        // 6. 어댑터에서 RecyclerView에 반영하도록 합니다.

//                        mAdapter.notifyItemInserted(0); //첫번째 줄에 삽입됨 2
                        mAdapter.notifyDataSetChanged();  //마지막 줄에 삽입됨 2
                        dialog.dismiss();

                        Response.Listener<String> responseListener6 = new Response.Listener<String>() {//volley
                            @Override
                            public void onResponse(String response) {
                            }
                        };
                        //서버로 volley를 이용해서 요청을 함
                        UserGroupAdd UserGroup = new UserGroupAdd(strID, strID2, responseListener6);
                        RequestQueue queue = Volley.newRequestQueue(MainCategoryActivity.this);
                        queue.add(UserGroup);
                    }
                });
                dialog.show();
            }
        });

        Intent intent = getIntent();
        strID = intent.getStringExtra("ID");    //사용자 이름
        profilePath = intent.getStringExtra("profileUri");  //사용자 프로필 경로를 받아온다.
        userID = intent.getStringExtra("userID");
        userPass = intent.getStringExtra("userPass");
        userEmail = intent.getStringExtra("userEmail");


        profile1 = (ImageView) findViewById(R.id.profileImageView1);
        profile2 = (ImageView) findViewById(R.id.profileImageView2);
        ID = findViewById(R.id.id);

        /**
         * 프로필 불러오는 부분
         */
        Uri uri = Uri.parse(profilePath);
        Glide.with(getApplicationContext())
                .load(uri)
                .into(profile1);

        Glide.with(getApplicationContext())
                .load(uri)
                .into(profile2);


        ID.setText("'" + strID + "'");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);

        ImageView btn_open = (ImageView) findViewById(R.id.btnOpen);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        Button nickChange = (Button) findViewById(R.id.nickBtn);
        nickChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 사용자 이름을 변경해주는 화면으로 전환되게 구현
                 */
                Intent intent = new Intent(MainCategoryActivity.this, NickChangeActivity.class);
                intent.putExtra("nick", strID);
                intent.putExtra("userID", userID);
                intent.putExtra("userPass", userPass);
                intent.putExtra("profileUri", profilePath);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });

        Button logout = (Button) findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show();

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Intent intent = new Intent(MainCategoryActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

                drawerLayout.setDrawerListener(listener);
                drawerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }

            DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            };
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finish();
        }
    }

    /**
     * RecyclerView 부분
     */
    /**
     * RecyclerView 아이템 클릭 이벤트 구현
     */
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainCategoryActivity.ClickListener clickListener;


        /**
         * RecyclerView의 Item이 존재 여부 확인
         */
        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainCategoryActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}