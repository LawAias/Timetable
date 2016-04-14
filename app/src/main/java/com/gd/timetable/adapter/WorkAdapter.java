package com.gd.timetable.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gd.timetable.R;
import com.gd.timetable.bean.WorkInfo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 作业数据的adapter
 */
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    private List<WorkInfo> mWorkInfoList;
    private Activity mAct;

    OnRecItemClick mOnRecItemClick;


    /**
     * 回调接口，点击了哪个item
     */
    public interface OnRecItemClick {
        void OnClickRecItem(WorkInfo mWorkInfo, View animView);

        void OnLongClickRecItem(WorkInfo mWorkInfo, View animView);
    }


    public WorkAdapter(List<WorkInfo> mWorkInfoList, Activity act, OnRecItemClick onRecItemClick) {
        this.mWorkInfoList = mWorkInfoList;
        this.mAct = act;
        mOnRecItemClick = onRecItemClick;
    }


    public void clearAll() {
        int size = this.mWorkInfoList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mWorkInfoList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addInfos(List<WorkInfo> list) {
        int nowLast = this.mWorkInfoList.size();
        this.mWorkInfoList.addAll(list);
        this.notifyItemRangeInserted(nowLast, nowLast + list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_note, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final WorkInfo mWorkInfo = mWorkInfoList.get(i);

//        imageLoader.init(ImageLoaderConfiguration.createDefault(mAct));
//        imageLoader.displayImage(mWorkInfo.getLogo().getUrl(), viewHolder.image,
//                PhotoUtil.normalImageOptions);
        Glide.with(mAct).load(mWorkInfo.getLogo().getUrl()).into(viewHolder.image);

        viewHolder.name.setText(mWorkInfo.getTitle());

        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        viewHolder.time.setText(mAct.getString(R.string.update_time, time.format(mWorkInfo.getUpdatedAt())));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecItemClick != null) {
                    mOnRecItemClick.OnClickRecItem(mWorkInfo, viewHolder.image);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnRecItemClick != null) {
                    mOnRecItemClick.OnLongClickRecItem(mWorkInfo, viewHolder.image);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWorkInfoList == null ? 0 : mWorkInfoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView time;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.noteName);
            time = (TextView) itemView.findViewById(R.id.noteTime);
            image = (ImageView) itemView.findViewById(R.id.notetypeImg);
        }

    }
}
