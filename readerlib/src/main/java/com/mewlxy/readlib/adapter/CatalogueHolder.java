package com.mewlxy.readlib.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import com.mewlxy.readlib.R;
import com.mewlxy.readlib.model.BookManager;
import com.mewlxy.readlib.model.ChapterBean;
import com.mewlxy.readlib.utlis.MD5Utils;
import com.mewlxy.readlib.utlis.StringUtils;

import androidx.core.content.ContextCompat;

public class CatalogueHolder extends ViewHolderImpl<ChapterBean> {

    private TextView mTvChapter;

    @Override
    public void initView() {
        mTvChapter = findById(R.id.category_tv_chapter);
    }

    @Override
    public void onBind(ChapterBean value, int pos){
        //首先判断是否该章已下载
        Drawable drawable = null;
        if (!TextUtils.isEmpty(value.getBookUrl()) && BookManager.isChapterCached(MD5Utils.INSTANCE.strToMd5By16(value.getBookUrl()),value.getName())){
            drawable = ContextCompat.getDrawable(getContext(),R.drawable.selector_category_load);
        }
        else {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
        }

        mTvChapter.setSelected(false);
        mTvChapter.setTextColor(ContextCompat.getColor(getContext(),R.color.colorTitle));
        mTvChapter.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        mTvChapter.setText(StringUtils.INSTANCE.convertCC(value.getName()));

    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.rlv_item_catalogue;
    }

    public void setSelectedChapter(){
        mTvChapter.setTextColor(ContextCompat.getColor(getContext(),R.color.light_red));
        mTvChapter.setSelected(true);
    }
}
