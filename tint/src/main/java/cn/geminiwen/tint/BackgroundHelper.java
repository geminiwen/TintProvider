package cn.geminiwen.tint;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.drawable.DrawableUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by geminiwen on 15/12/3.
 */
public class BackgroundHelper {

    private final View mView;
    private final TintManager mTintManager;

    private TintInfo mBackgroundTint;

    BackgroundHelper(View view, TintManager tintManager) {
        mView = view;
        mTintManager = tintManager;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mView.getContext().obtainStyledAttributes(attrs,
                R.styleable.ViewBackgroundHelper, defStyleAttr, 0);
        try {
           if (a.hasValue(R.styleable.ViewBackgroundHelper_backgroundTint)) {
                ViewCompat.setBackgroundTintList(mView,
                        a.getColorStateList(R.styleable.ViewBackgroundHelper_backgroundTint));
            }
            if (a.hasValue(R.styleable.ViewBackgroundHelper_backgroundTintMode)) {
                ViewCompat.setBackgroundTintMode(mView,
                        DrawableUtils.parseTintMode(
                                a.getInt(R.styleable.ViewBackgroundHelper_backgroundTintMode, -1),
                                null));
            }
        } finally {
            a.recycle();
        }
    }

    void onSetBackgroundResource(int resId) {
        // Update the default background tint
        applySupportBackgroundTint();
    }

    void onSetBackgroundDrawable(Drawable background) {
        // We don't know that this drawable is, so we need to clear the default background tint
        applySupportBackgroundTint();
    }

    void setSupportBackgroundTintList(ColorStateList tint) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mTintList = tint;
        mBackgroundTint.mHasTintList = true;

        applySupportBackgroundTint();
    }

    ColorStateList getSupportBackgroundTintList() {
        return mBackgroundTint != null ? mBackgroundTint.mTintList : null;
    }

    void setSupportBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mTintMode = tintMode;
        mBackgroundTint.mHasTintMode = true;

        applySupportBackgroundTint();
    }

    PorterDuff.Mode getSupportBackgroundTintMode() {
        return mBackgroundTint != null ? mBackgroundTint.mTintMode : null;
    }

    void applySupportBackgroundTint() {
        final Drawable background = mView.getBackground();
        if (background != null) {
            if (mBackgroundTint != null) {
                TintManager.tintDrawable(background, mBackgroundTint, mView.getDrawableState());
            }
        }
    }


}
