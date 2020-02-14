package com.mewlxy.readlib.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.mewlxy.readlib.model.ChapterBean;


public class CatalogueAdapter extends EasyAdapter<ChapterBean> {
    private int currentSelected = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        CatalogueHolder holder = (CatalogueHolder) view.getTag();

        if (position == currentSelected){
            holder.setSelectedChapter();
        }

        return view;
    }

    @Override
    protected IViewHolder<ChapterBean> onCreateViewHolder(int viewType) {
        return new CatalogueHolder();
    }

    public void setChapter(int pos){
        currentSelected = pos;
        notifyDataSetChanged();
    }

}
