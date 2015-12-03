package cn.geminiwen.tinitprovider.demo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.TintableBackgroundView;
import android.util.AttributeSet;
import android.view.View;

import cn.geminiwen.tint.BackgroundHelper;

/**
 * Created by geminiwen on 15/12/3.
 */
public class MyView extends View implements TintableBackgroundView {

    private BackgroundHelper mBackgroundHelper;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackgroundHelper = new BackgroundHelper(this);
        mBackgroundHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
        if (mBackgroundHelper != null) {
            mBackgroundHelper.onSetBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
            super.setBackgroundResource(resid);
        if (mBackgroundHelper != null) {
            mBackgroundHelper.onSetBackgroundResource(resid);
        }
    }

    @Override
    public void setSupportBackgroundTintList(ColorStateList tint) {
        mBackgroundHelper.setSupportBackgroundTintList(tint);
    }

    @Nullable
    @Override
    public ColorStateList getSupportBackgroundTintList() {
        return mBackgroundHelper.getSupportBackgroundTintList();
    }

    @Override
    public void setSupportBackgroundTintMode(PorterDuff.Mode tintMode) {
        mBackgroundHelper.setSupportBackgroundTintMode(tintMode);
    }

    @Nullable
    @Override
    public PorterDuff.Mode getSupportBackgroundTintMode() {
        return mBackgroundHelper.getSupportBackgroundTintMode();
    }
}
