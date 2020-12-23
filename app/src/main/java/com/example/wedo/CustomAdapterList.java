package com.example.wedo;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomAdapterList extends RecyclerView.Adapter<CustomAdapterList.CustomViewHolder> {

    private ArrayList<DictionaryList> mDictionaryList;
    public ResultActivity resultActivity = new ResultActivity();
    private Context mContext;
//    int Count;

    @NonNull
    @Override
    public CustomAdapterList.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list2, viewGroup, false);

        TextView id_listitem = (TextView) view.findViewById(R.id.id_listitem);
        id_listitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id_listitem.setFocusable(true);
                id_listitem.setFocusableInTouchMode(true);
//                Log.e("뭐가 문제일까...","nick: "+resultActivity.strID3+"group: "+resultActivity.userID);
            }
        });
//        id_listitem.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        id_listitem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
//                    if(id_listitem.getText().toString().equals("")){
//                        Toast.makeText(mContext, "목록명을 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        String changeList = id_listitem.getText().toString();
//                        id_listitem.setFocusable(false);
//                        id_listitem.setFocusableInTouchMode(false);
//                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(id_listitem.getWindowToken(), 0);   //키보드 내리기
//                        Response.Listener<String> responseListener = new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                try {
//                                    JSONObject jsonResponse = new JSONObject(response);
//                                    boolean success = jsonResponse.getBoolean("success");
//                                    if (success) {
//                                        /**
//                                         * 수정되는 부분
//                                         */
//                                        Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
//                                            @Override
//                                            public void onResponse(String response) {
//                                                id_listitem.setFocusable(false);
//                                                id_listitem.setFocusableInTouchMode(false);
//                                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                                                imm.hideSoftInputFromWindow(id_listitem.getWindowToken(), 0);   //키보드 내리기
//                                            }
//                                        };
//                                        //서버로 volley를 이용해서 요청을 함
//                                        UserListUpdate UserListUpdate = new UserListUpdate(resultActivity.nick, resultActivity.userID, resultActivity.strID3, changeList, responseListener);
//                                        RequestQueue queue = Volley.newRequestQueue(mContext);
//                                        queue.add(UserListUpdate);
//                                    } else {
//                                        Toast.makeText(mContext, "목록명이 존재합니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        };
//                        ValidateList ValidateList = new ValidateList(resultActivity.str_user, resultActivity.str_group, changeList, responseListener);
//                        RequestQueue queue = Volley.newRequestQueue(mContext);
//                        queue.add(ValidateList);
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
//
        CustomAdapterList.CustomViewHolder viewHolder = new CustomAdapterList.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapterList.CustomViewHolder viewholder, int position) {

        viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.id.setGravity(Gravity.CENTER);


        viewholder.id.setText(mDictionaryList.get(position).getlist());
    }

    @Override
    public int getItemCount() {
        return (null != mDictionaryList ? mDictionaryList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener { // 1. 리스너 추가
        protected TextView id;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.id_listitem);

            view.setOnCreateContextMenuListener(this); //2. 리스너 등록
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 3. 메뉴 추가


//            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
//            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
//            Edit.setOnMenuItemClickListener(onEditMenu);
//            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 4. 캔텍스트 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View view = LayoutInflater.from(mContext)
                                .inflate(R.layout.edit_box, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText editTextID = (EditText) view.findViewById(R.id.mesgase);

                        editTextID.setHint(mDictionaryList.get(getAdapterPosition()).getlist());
                        ButtonSubmit.setText("편집하기");

                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String strID = editTextID.getText().toString();

                                Response.Listener<String> responseListener = new Response.Listener<String>() {//volley
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                };
                                //서버로 volley를 이용해서 요청을 함

                                UserGroupUpdate UserGroupUpdate = new UserGroupUpdate(mDictionaryList.get(getAdapterPosition()).getUser(), mDictionaryList.get(getAdapterPosition()).getlist(), strID, responseListener);
                                Log.e("아이디 ", mDictionaryList.get(getAdapterPosition()).getUser() + "내용 : " + mDictionaryList.get(getAdapterPosition()).getlist());
                                Log.e("아이디 ", mDictionaryList.get(getAdapterPosition()) + "내용 : " + mDictionaryList.get(getAdapterPosition()));

                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                queue.add(UserGroupUpdate);

                                DictionaryList dict = new DictionaryList(strID);    //목록 변경 된 이름을 setter에 넣기 (strID)
                                dict.setUser(mDictionaryList.get(getAdapterPosition()).getUser());    //변경후 사용자 이름 setter에 넣기 (mList.get(getAdapterPosition()))

                                mDictionaryList.set(getAdapterPosition(), dict);
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
                        View view1 = LayoutInflater.from(mContext)
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
                                    }
                                };
                                //서버로 volley를 이용해서 요청을 함

                                UserGroupRemove UserGroupRemove = new UserGroupRemove(mDictionaryList.get(getAdapterPosition()).getUser(), mDictionaryList.get(getAdapterPosition()).getlist(), responseListener);
                                Log.e("아이디 ", mDictionaryList.get(getAdapterPosition()).getUser() + "내용 : " + mDictionaryList.get(getAdapterPosition()).getlist());
                                Log.e("아이디 ", mDictionaryList.get(getAdapterPosition()) + "내용 : " + mDictionaryList.get(getAdapterPosition()));

                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                queue.add(UserGroupRemove);

                                mDictionaryList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), mDictionaryList.size());
                                dialog1.dismiss();
                            }
                        });
                        ButtonSubmit2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.dismiss();
                            }
                        });
                        dialog1.show();
                        break;
                }
                return true;
            }
        };
    }


//    public CustomAdapter(ArrayList<Dictionary> list) {
//        this.mList = list;
//    }

    public CustomAdapterList(Context context, ArrayList<DictionaryList> list) {
        mDictionaryList = list;
        mContext = context;

    }


}