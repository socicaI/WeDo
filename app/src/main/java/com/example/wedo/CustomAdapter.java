package com.example.wedo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * ArrayList에 있는 Dictionary 클래스 타입의 데이터를 RecyclerView에 보여주기 위한 클래스
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Dictionary> mList;
    private Context mContext;
//    int Count;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.id.setGravity(Gravity.CENTER);


        viewholder.id.setText(mList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
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


            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

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

                        editTextID.setHint(mList.get(getAdapterPosition()).getId());
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

                                UserGroupUpdate UserGroupUpdate = new UserGroupUpdate(mList.get(getAdapterPosition()).getUser(), mList.get(getAdapterPosition()).getId(), strID, responseListener);
                                Log.e("아이디 ", mList.get(getAdapterPosition()).getUser() + "내용 : " + mList.get(getAdapterPosition()).getId());
                                Log.e("아이디 ", mList.get(getAdapterPosition()) + "내용 : " + mList.get(getAdapterPosition()));

                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                queue.add(UserGroupUpdate);

                                Dictionary dict = new Dictionary(strID);    //목록 변경 된 이름을 setter에 넣기 (strID)
                                dict.setUser(mList.get(getAdapterPosition()).getUser());    //변경후 사용자 이름 setter에 넣기 (mList.get(getAdapterPosition()))

                                mList.set(getAdapterPosition(), dict);
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

                                UserGroupRemove UserGroupRemove = new UserGroupRemove(mList.get(getAdapterPosition()).getUser(), mList.get(getAdapterPosition()).getId(), responseListener);
                                Log.e("아이디 ", mList.get(getAdapterPosition()).getUser() + "내용 : " + mList.get(getAdapterPosition()).getId());
                                Log.e("아이디 ", mList.get(getAdapterPosition()) + "내용 : " + mList.get(getAdapterPosition()));

                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                queue.add(UserGroupRemove);

                                mList.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(), mList.size());
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

    public CustomAdapter(Context context, ArrayList<Dictionary> list) {
        mList = list;
        mContext = context;

    }


}
