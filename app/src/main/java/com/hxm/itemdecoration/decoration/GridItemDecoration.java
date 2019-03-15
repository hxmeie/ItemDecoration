package com.hxm.itemdecoration.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by hxm on 2018/3/19.
 * 描述：todo: 适配瀑布流StaggeredGridLayoutManager,水平滑动方向
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    public static final int VERTICAL = GridLayoutManager.VERTICAL;
    public static final int HORIZONTAL = GridLayoutManager.HORIZONTAL;
    private Paint mVerPaint, mHorPaint;
    private Builder mBuilder;

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

    /**
     * 需要注意的一点是 getItemOffsets 是针对每一个 ItemView，而 onDraw 方法却是针对 RecyclerView 本身，
     * 所以在 onDraw 方法中需要遍历屏幕上可见的 ItemView，分别获取它们的位置信息，然后分别的绘制对应的分割线。
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //每一个itemView的（left+right）相等且（top+bottom）也相等，这样才能保证itemView大小都一样
        int spanCount = getSpanCount(parent);
        int itemCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildLayoutPosition(view);
        int column = itemPosition % spanCount;
        int bottom = 0;
        int left = column * mBuilder.dividerVerSize / spanCount;
        int right = mBuilder.dividerVerSize - (column + 1) * mBuilder.dividerVerSize / spanCount;
        if (!isLastRow(itemPosition, spanCount, itemCount))
            bottom = mBuilder.dividerHorSize;
        outRect.set(left, 0, right, bottom);
        //是否显示四周的
        if (mBuilder.showAround) {
            marginLeftAndRightOffsets(outRect, spanCount, itemPosition);
            marginTopAndBottomOffsets(outRect, spanCount, itemPosition, itemCount);
        }
    }

    private void marginLeftAndRightOffsets(Rect outRect, int spanCount, int itemPosition) {
        if (mBuilder.marginRight == 0 && mBuilder.marginLeft == 0)
            return;
        int average = (mBuilder.marginLeft + mBuilder.marginRight) / spanCount;
        outRect.left += (mBuilder.marginLeft - (itemPosition % spanCount) * average);
        outRect.right += ((itemPosition % spanCount) + 1) * average - mBuilder.marginLeft;
    }

    private void marginTopAndBottomOffsets(Rect outRect, int spanCount, int itemPosition, int itemCount) {
        // TODO: 2019/3/15 计算垂直方向
        if (mBuilder.marginTop == 0 && mBuilder.marginBottom == 0)
            return;
        if (isFirstRow(itemPosition, spanCount))
            outRect.top += mBuilder.marginTop;
        if (isLastRow(itemPosition, spanCount, itemCount))
            outRect.bottom += mBuilder.marginBottom;

    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        // TODO: 2019/3/15 和竖线原理一样
        int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (mBuilder.showAround) {
                // FIXME: 2019/3/15 有问题
                final int l = isFirstRow(i, spanCount) ? child.getLeft() - mBuilder.marginLeft :
                        child.getLeft() - mBuilder.dividerVerSize;
                final int t = isFirstRow(i, spanCount) ? child.getTop() - mBuilder.marginTop : 0;
                final int r = isLastColumn(i, spanCount, childCount) && isFirstRow(i, spanCount) ?
                        child.getRight() + mBuilder.marginRight : child.getRight();
                final int b = isLastRow(i, spanCount, childCount) ? mBuilder.marginBottom :
                        t + mBuilder.dividerHorSize;
                c.drawRect(l, t, r, b, mHorPaint);
            } else {
                final int left = child.getLeft();
                final int right = child.getRight();
                final int top = isFirstRow(i, spanCount) ? 0 : child.getBottom();
                final int bottom = isLastRow(i, spanCount, childCount) ? 0 :
                        top + mBuilder.dividerHorSize;
                c.drawRect(left, top, right, bottom, mHorPaint);
            }
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        int spanCount = getSpanCount(parent);
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (mBuilder.showAround) {
                final int l = isFirstColumn(i, spanCount) ? child.getLeft() - mBuilder.marginLeft :
                        child.getLeft() - mBuilder.dividerVerSize;
                final int t = child.getTop();
                final int r = isFirstColumn(i, spanCount) ? l + mBuilder.marginLeft :
                        l + mBuilder.dividerVerSize;
                final int b = child.getBottom() + mBuilder.dividerHorSize;
                c.drawRect(l, t, r, b, mVerPaint);
                if (isLastColumn(i, spanCount, childCount)) {
                    c.drawRect(child.getRight(), t, child.getRight() + mBuilder.marginRight, b, mVerPaint);
                }
            } else {
                final int left = isFirstColumn(i, spanCount) ? 0 :
                        child.getLeft() - mBuilder.dividerVerSize;
                final int top = child.getTop();
                final int right = isLastColumn(i, spanCount, childCount) ? 0 :
                        left + mBuilder.dividerVerSize;
                final int bottom = child.getBottom() + mBuilder.dividerHorSize;
                c.drawRect(left, top, right, bottom, mVerPaint);
            }
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
     * 是否是第一行
     */
    private boolean isFirstRow(int position, int spanCount) {
        if (mBuilder.orientation == VERTICAL) {
            //垂直方向上
            return position < spanCount;
        } else {
            //水平方向上
            return position % spanCount == 0;
        }
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRow(int position, int spanCount, int childCount) {
        if (mBuilder.orientation == VERTICAL) {
            int lastRowCount = childCount % spanCount;
            lastRowCount = lastRowCount == 0 ? spanCount : lastRowCount;
            return position >= childCount - lastRowCount;
        } else {
            return (position + 1) % spanCount == 0;
        }
    }

    /**
     * 是否是第一列
     */
    private boolean isFirstColumn(int position, int spanCount) {
        if (mBuilder.orientation == VERTICAL) {
            return position % spanCount == 0;
        } else {
            return position < spanCount;
        }
    }

    /**
     * 是否是最后一列
     */
    private boolean isLastColumn(int position, int spanCount, int childCount) {
        if (mBuilder.orientation == VERTICAL) {
            return (position + 1) % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            //最后一列itemView的position一定大于等于itemView总数量-最后一列itemView数量
            return position >= childCount - lastColumnCount;
        }
    }

    @IntDef({
            HORIZONTAL,
            VERTICAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    public static class Builder {
        private int horColor = Color.parseColor("#000000");//横向线颜色
        private int verColor = Color.parseColor("#000000");//纵向线颜色
        private int dividerHorSize = 2;//横向线宽度
        private int dividerVerSize = 2;//纵向线宽度
        private int marginLeft = 0, marginRight = 0;
        private int marginTop = 0, marginBottom = 0;
        private int margin = 0;
        private int orientation = VERTICAL;//滑动方向，默认垂直
        private boolean showAround = false;//四周是否显示
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

        public Builder margin(@Px int margin) {
            this.marginLeft = margin;
            this.marginRight = margin;
            this.marginTop = margin;
            this.marginBottom = margin;
            return this;
        }

        public Builder marginLeft(@Px int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder marginRight(@Px int marginRight) {
            this.marginRight = marginRight;
            return this;
        }

        public Builder marginTop(@Px int marginTop) {
            this.marginTop = marginTop;
            return this;
        }

        public Builder marginBottom(@Px int marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }

        public Builder marginLeftAndRight(@Px int margin) {
            this.marginLeft = margin;
            this.marginRight = margin;
            return this;
        }

        public Builder marginTopAndBottom(@Px int margin) {
            this.marginTop = margin;
            this.marginBottom = margin;
            return this;
        }

        /**
         * 设置滑动方向
         */
        public Builder orientation(@Orientation int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder showAround(boolean showAround) {
            this.showAround = showAround;
            return this;
        }


        public GridItemDecoration build() {
            return new GridItemDecoration(this);
        }

    }
}
