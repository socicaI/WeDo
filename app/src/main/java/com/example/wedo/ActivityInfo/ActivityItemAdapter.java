package com.example.wedo.ActivityInfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wedo.R;

import java.util.List;

public class ActivityItemAdapter extends RecyclerView.Adapter<ActivityItemAdapter.ItemViewHolder>{

    private List<ActivityItemModel> mDataList;

    public ActivityItemAdapter(List<ActivityItemModel> items) {
        mDataList = items;
    }

    @NonNull
    @Override
    public ActivityItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_info_row_item,
                parent, false);
        return new ActivityItemAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ActivityItemAdapter.ItemViewHolder holder, final int position) {
        ActivityItemModel currentItem = mDataList.get(position);
        /**가입된 사용자 이름을 불러옴*/
        holder.textView.setText(currentItem.getDateTime());
        holder.user.setText(currentItem.getUser());
        holder.status.setText(currentItem.getStatus());
        holder.text.setText(currentItem.getText());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        // TODO : 뷰홀더 완성하시오
        TextView textView;
        TextView user;
        TextView status;
        TextView text;

        ItemViewHolder(View itemView) {
            super(itemView);
            // TODO : 뷰홀더 완성하시오
            textView = itemView.findViewById(R.id.activityInfoDatetime);
            user = itemView.findViewById(R.id.user);
            status = itemView.findViewById(R.id.status);
            text = itemView.findViewById(R.id.text);
        }
    }

    public interface onItemListener {
        void onItemClicked(int position);
    }
}
