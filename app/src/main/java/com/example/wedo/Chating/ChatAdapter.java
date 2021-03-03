package com.example.wedo.Chating;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.wedo.R;
import com.example.wedo.SearchFilter.ItemAdapter;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ChatItem> items = new ArrayList<ChatItem>();
    private Context context;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == ChatType.CENTER_MESSAGE) {
            view = inflater.inflate(R.layout.chat_center_item, parent, false);
            return new CenterViewHolder(view);
        } else if (viewType == ChatType.LEFT_MESSAGE) {
            view = inflater.inflate(R.layout.chat_left_item, parent, false);
            return new LeftViewHolder(view);
        } else if (viewType == ChatType.RIGHT_MESSAGE) {
            view = inflater.inflate(R.layout.chat_right_item, parent, false);
            return new RightViewHolder(view);
        }
        else if (viewType == ChatType.LEFT_IMAGE) {
            view = inflater.inflate(R.layout.chat_left_image, parent, false);
            return new LeftImageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.chat_right_image, parent, false);
            return new RightImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof CenterViewHolder) {
            ChatItem item = items.get(position);
            ((CenterViewHolder) viewHolder).setItem(item);
        } else if (viewHolder instanceof LeftViewHolder) {
            ChatItem item = items.get(position);
            ((LeftViewHolder) viewHolder).setItem(item);
        } else if (viewHolder instanceof RightViewHolder) {
            ChatItem item = items.get(position);
            ((RightViewHolder) viewHolder).setItem(item);
        } else if (viewHolder instanceof LeftImageViewHolder) {
            ChatItem item = items.get(position);
            ((LeftImageViewHolder) viewHolder).setItem(item, context);
            // TODO : 리스너를 정의하시오.
            if (mListener != null){
                int pos = position;
                //final ItemModel item = mItems.get(viewHolder.getAdapterPosition());
                ((LeftImageViewHolder) viewHolder).image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClicked(pos);
                    }
                });
                //버튼등에도 동일하게 지정할 수 있음 holder.버튼이름.setOnClickListener..형식으로
            }
        } else {
            ChatItem item = items.get(position);
            ((RightImageViewHolder) viewHolder).setItem(item, context);
            // TODO : 리스너를 정의하시오.
            if (mListener != null){
                int pos = position;
                //final ItemModel item = mItems.get(viewHolder.getAdapterPosition());
                ((RightImageViewHolder) viewHolder).image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClicked(pos);
                    }
                });
                //버튼등에도 동일하게 지정할 수 있음 holder.버튼이름.setOnClickListener..형식으로
            }
        }
    }

    public int getItemCount() {
        return items.size();
    }

    public void addItem(ChatItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<ChatItem> items) {
        this.items = items;
    }

    public ChatItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public class CenterViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;

        public CenterViewHolder(View itemView) {
            super(itemView);

            contentText = itemView.findViewById(R.id.content_text);
        }

        public void setItem(ChatItem item) {
            contentText.setText(item.getContent());
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView contentText;
        TextView sendTimeText;

        public LeftViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name_text);
            contentText = itemView.findViewById(R.id.msg_text);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item) {
            nameText.setText(item.getName());
            contentText.setText(item.getContent());
            sendTimeText.setText(item.getSendTime());
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;
        TextView sendTimeText;

        public RightViewHolder(View itemView) {
            super(itemView);

            contentText = itemView.findViewById(R.id.msg_text);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item) {
            contentText.setText(item.getContent());
            sendTimeText.setText(item.getSendTime());
        }
    }

    public class LeftImageViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        ImageView image;
        TextView sendTimeText;

        public LeftImageViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name_text);
            image = itemView.findViewById(R.id.image_view);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item, Context context) {
            MultiTransformation option = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));


            Glide.with(context)
                    .load(item.getContent())
                    .apply(RequestOptions.bitmapTransform(option))
                    .into(image);
            nameText.setText(item.getName());
            sendTimeText.setText(item.getSendTime());
        }
    }

    public class RightImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView sendTimeText;

        public RightImageViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image_view);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item, Context context) {
            MultiTransformation option = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));
            

            Glide.with(context)
                    .load(item.getContent())
                    .apply(RequestOptions.bitmapTransform(option))
                    .into(image);
            sendTimeText.setText(item.getSendTime());
        }
    }
    //interface - 클릭인터페이스
    private ChatAdapter.onItemListener mListener;
    public void setOnClickListener(ChatAdapter.onItemListener listener){
        mListener = listener;
    }
    //onclick listener interface
    //1. interface onItemListener 선언
    //2. 내부에서 mListener 선언하고
    // 외부에서 접근가능하도록 setOnClickListener작성
    //3.onBindViewHolder에서 처리
    public interface onItemListener {
        void onItemClicked(int position);
        //void onItemClicked(ItemModel model); 모델값을 넘길수 있음
        //다른버튼도 정의할 수 있음 onShareButtonClicked(int position);
    }
}
