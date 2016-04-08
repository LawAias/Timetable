package com.gd.timetable.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gd.timetable.R;
import com.gd.timetable.bean.NoteInfo;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 笔记数据的adapter
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<NoteInfo> mNoteInfoList;
    private Activity mAct;

    OnNoteItemClick mOnNoteItemClick;

    /**
     * 回调接口，点击了哪个item
     */
    public interface OnNoteItemClick{
        void OnClickNoteItem(NoteInfo mNoteInfo, View animView);
        void OnLongClickNoteItem(NoteInfo mNoteInfo, View animView);
    }

    int colorAccent;

    public NoteAdapter(List<NoteInfo> mNoteInfoList, Activity act, OnNoteItemClick onNoteItemClick) {
        this.mNoteInfoList = mNoteInfoList;
        this.mAct = act;
        mOnNoteItemClick = onNoteItemClick;
        colorAccent = mAct.getResources().getColor(R.color.primary);
    }


    public void clearAll() {
        int size = this.mNoteInfoList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mNoteInfoList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addNoteInfos(List<NoteInfo> list) {
        int nowLast = this.mNoteInfoList.size();
        this.mNoteInfoList.addAll(list);
        this.notifyItemRangeInserted(nowLast, nowLast + list.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_note, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final NoteInfo mNoteInfo = mNoteInfoList.get(i);


        Drawable icon = new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_event_note).color(colorAccent);

        viewHolder.image.setImageDrawable(icon);
        viewHolder.name.setText(mNoteInfo.getTitle());

        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        viewHolder.time.setText(time.format(mNoteInfo.getUpdatedAt()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnNoteItemClick!=null){
                    mOnNoteItemClick.OnClickNoteItem(mNoteInfo, viewHolder.image);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnNoteItemClick!=null){
                    mOnNoteItemClick.OnLongClickNoteItem(mNoteInfo, viewHolder.image);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteInfoList == null ? 0 : mNoteInfoList.size();
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
