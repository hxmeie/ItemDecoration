package com.hxm.itemdecoration.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by hxm on 2018/3/19.
 * 描述：
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mVerPaint, mHorPaint;
    private Builder mBuilder;

    // TODO: 2018/10/8 未完成
    GridItemDecoration(Builder builder) {
        init(builder);
    }

    private void init(Builder builder) {
        this.mBuilder = builder;
        mVerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mVerPaint.setStyle(Paint.Style.FILL);
        mVerPaint.setColor(builder.verColor);
        mHorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHorPaint.setStyle(Paint.Style.FILL);
        mHorPaint.setColor(builder.horColor);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int spanCount = getSpanCount(parent);
        int itemCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildLayoutPosition(view);
        int column = itemPosition % spanCount;
        int bottom = 0;
        int left = column * mBuilder.dividerVerSize / spanCount;
        int right = mBuilder.dividerVerSize - (column + 1) * mBuilder.dividerVerSize / spanCount;
//        if (!(isLastRaw(parent, itemPosition, spanCount, itemCount) && !mBuilder.showAround))
        if (!isLastRaw(parent, itemPosition, spanCount, itemCount))
            bottom = mBuilder.dividerHorSize;
        outRect.set(left, 0, right, bottom);
        marginOffsets(outRect, spanCount, itemPosition);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mBuilder.dividerHorSize;
            c.drawRect(left, top, right, bottom, mHorPaint);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
//            if ((parent.getChildAdapterPosition(child)) % getSpanCount(parent) == 0)
//                continue;
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + mBuilder.dividerHorSize;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mBuilder.dividerVerSize;
            c.drawRect(left, top, right, bottom, mVerPaint);
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            return pos >= childCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            } else {
                // StaggeredGridLayoutManager 且横向滚动
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

    private void marginOffsets(Rect outRect, int spanCount, int itemPosition) {
        if (mBuilder.marginRight == 0 && mBuilder.marginLeft == 0)
            return;

        int itemShrink = (mBuilder.marginLeft + mBuilder.marginRight) / spanCount;
        outRect.left += (mBuilder.marginLeft - (itemPosition % spanCount) * itemShrink);

        outRect.right += ((itemPosition % spanCount) + 1) * itemShrink - mBuilder.marginLeft;
    }

    public static class Builder {
        private int horColor = Color.parseColor("#000000");//横向线颜色
        private int verColor = Color.parseColor("#000000");//纵向线颜色
        private int dividerHorSize = 2;//横向线宽度
        private int dividerVerSize = 2;//纵向线宽度
        private int marginLeft = 0, marginRight = 0;//左右两侧间距
        private boolean showAround;//四周是否显示
        private Context c;

        public Builder(Context c) {
            this.c = c;
        }

        /**
         * 设置divider的颜色
         */
        public Builder colorRes(@ColorRes int color) {
            this.horColor = ContextCompat.getColor(c, color);
            this.verColor = ContextCompat.getColor(c, color);
            return this;
        }

        /**
         * 设置divider的颜色
         */
        public Builder colorInt(@ColorInt int color) {
            this.horColor = color;
            this.verColor = color;
            return this;
        }

        /**
         * 单独设置横向divider的颜色
         */
        public Builder horColorRes(@ColorRes int horColor) {
            this.horColor = ContextCompat.getColor(c, horColor);
            return this;
        }

        /**
         * 单独设置横向divider的颜色
         */
        public Builder horColorInt(@ColorInt int horColor) {
            this.horColor = horColor;
            return this;
        }

        /**
         * 单独设置纵向divider的颜色
         */
        public Builder verColorRes(@ColorRes int verColor) {
            this.verColor = ContextCompat.getColor(c, verColor);
            return this;
        }

        /**
         * 单独设置纵向divider的颜色
         */
        public Builder verColorInt(@ColorInt int verColor) {
            this.verColor = verColor;
            return this;
        }

        /**
         * 设置divider的宽度
         */
        public Builder size(@Px int size) {
            this.dividerHorSize = size;
            this.dividerVerSize = size;
            return this;
        }

        /**
         * 设置横向divider的宽度
         */
        public Builder horSize(@Px int horSize) {
            this.dividerHorSize = horSize;
            return this;
        }

        /**
         * 设置纵向divider的宽度
         */
        public Builder verSize(@Px int verSize) {
            this.dividerVerSize = verSize;
            return this;
        }

        public Builder margin(@Px int marginLeft, @Px int marginRight) {
            this.marginLeft = marginLeft;
            this.marginRight = marginRight;
            return this;
        }


        public GridItemDecoration build() {
            return new GridItemDecoration(this);
        }

    }
}
