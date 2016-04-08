package com.gd.timetable.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gd.timetable.R;
import com.gd.timetable.bean.ScheduleInfo;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleInfo> mScheduleList;
    private Activity mAct;
    OnScheduleClick mOnNewsItemClick;

    /**
     * 回调接口，点击了哪个item
     */
    public interface OnScheduleClick {
        void OnClickSchedule(ScheduleInfo scheduleInfo);
        void OnLongClickSchedule(ScheduleInfo scheduleInfo);
    }


    public ScheduleAdapter(List<ScheduleInfo> list, Activity act, OnScheduleClick onNewsItemClick) {
        this.mScheduleList = list;
        this.mAct = act;
        mOnNewsItemClick = onNewsItemClick;

    }



    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_schedule, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final ScheduleInfo scheduleInfo = mScheduleList.get(i);
        
        viewHolder.name.setText(scheduleInfo.getName());


        StringBuilder sb = new StringBuilder();
        sb.append("课程名称:").append(scheduleInfo.getName()).append("\n")
                .append("时间:").append(scheduleInfo.getDate()+" "+scheduleInfo.getTime()).append("\n")
                .append("地址:").append(scheduleInfo.getPlace()).append("\n")
                .append("任课教师:").append(scheduleInfo.getTeacher()).append("\n");

        viewHolder.mTxInfo.setText(sb.toString());

         viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnNewsItemClick !=null){
                    mOnNewsItemClick.OnClickSchedule(scheduleInfo);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mScheduleList == null ? 0 : mScheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView mTxInfo;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.title);
            mTxInfo = (TextView) itemView.findViewById(R.id.info);
        }

    }
}
