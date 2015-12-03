package cn.geminiwen.tint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * Created by geminiwen on 15/12/3.
 */
public class TintManager {

    private static final String TAG = TintManager.class.getSimpleName();

    private static final ColorFilterLruCache COLOR_FILTER_CACHE = new ColorFilterLruCache(6);

    private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;

    public static void tintDrawable(Drawable drawable, TintInfo tint, int[] state) {
        if (shouldMutateBackground(drawable) && drawable.mutate() != drawable) {
            Log.d(TAG, "Mutated drawable is not the same instance as the input.");
            return;
        }

        if (tint.mHasTintList || tint.mHasTintMode) {
            drawable.setColorFilter(createTintFilter(
                    tint.mHasTintList ? tint.mTintList : null,
                    tint.mHasTintMode ? tint.mTintMode : DEFAULT_MODE,
                    state));
        } else {
            drawable.clearColorFilter();
        }

        if (Build.VERSION.SDK_INT <= 10) {
            // On Gingerbread, GradientDrawable does not invalidate itself when it's
            // ColorFilter has changed, so we need to force an invalidation
            drawable.invalidateSelf();
        }
    }

    private static boolean shouldMutateBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            // For SDK 16+, we should be fine mutating the drawable
            return true;
        }


        if (drawable instanceof LayerDrawable) {
            return Build.VERSION.SDK_INT >= 16;
        } else if (drawable instanceof InsetDrawable) {
            return Build.VERSION.SDK_INT >= 14;
        } else if (drawable instanceof DrawableContainer) {
            // If we have a DrawableContainer, let's traverse it's child array
            final Drawable.ConstantState state = drawable.getConstantState();
            if (state instanceof DrawableContainer.DrawableContainerState) {
                final DrawableContainer.DrawableContainerState containerState =
                        (DrawableContainer.DrawableContainerState) state;
                for (Drawable child : containerState.getChildren()) {
                    if (!shouldMutateBackground(child)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private static PorterDuffColorFilter createTintFilter(ColorStateList tint,
                                                          PorterDuff.Mode tintMode, final int[] state) {
        if (tint == null || tintMode == null) {
            return null;
        }
        final int color = tint.getColorForState(state, Color.TRANSPARENT);
        return getPorterDuffColorFilter(color, tintMode);
    }

    private static PorterDuffColorFilter getPorterDuffColorFilter(int color, PorterDuff.Mode mode) {
        // First, lets see if the cache already contains the color filter
        PorterDuffColorFilter filter = COLOR_FILTER_CACHE.get(color, mode);

        if (filter == null) {
            // Cache miss, so create a color filter and add it to the cache
            filter = new PorterDuffColorFilter(color, mode);
            COLOR_FILTER_CACHE.put(color, mode, filter);
        }

        return filter;
    }


    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {

        public ColorFilterLruCache(int maxSize) {
            super(maxSize);
        }

        PorterDuffColorFilter get(int color, PorterDuff.Mode mode) {
            return get(generateCacheKey(color, mode));
        }

        PorterDuffColorFilter put(int color, PorterDuff.Mode mode, PorterDuffColorFilter filter) {
            return put(generateCacheKey(color, mode), filter);
        }

        private static int generateCacheKey(int color, PorterDuff.Mode mode) {
            int hashCode = 1;
            hashCode = 31 * hashCode + color;
            hashCode = 31 * hashCode + mode.hashCode();
            return hashCode;
        }
    }

}
