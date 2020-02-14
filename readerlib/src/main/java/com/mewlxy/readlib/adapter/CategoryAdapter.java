package com.mewlxy.readlib.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.mewlxy.readlib.model.ChapterBean;


public class CategoryAdapter extends EasyAdapter<ChapterBean> {
    private int currentSelected = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CategoryHolder holder = (CategoryHolder) view.getTag();

        if (position == currentSelected){
            holder.setSelectedChapter();
        }

        return view;
    }

    @Override
    protected IViewHolder<ChapterBean> onCreateViewHolder(int viewType) {
        return new CategoryHolder();
    }

    public void setChapter(int pos){
        currentSelected = pos;
        notifyDataSetChanged();
    }

}
