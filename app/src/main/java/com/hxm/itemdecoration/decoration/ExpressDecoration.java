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
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hxm on 2018/10/10
 * 描述：
 */
public class ExpressDecoration extends RecyclerView.ItemDecoration {
    public static final int CIRCLE_STYLE_SOLID = 1;
    public static final int CIRCLE_STYLE_STROKE = 2;
    private static int defaultColor = Color.parseColor("#000000");
    private static int defaultSize = 2;
    private Paint paint;
    private ExpressDecoration.Builder builder;

    public ExpressDecoration(Context context) {
        builder = new ExpressDecoration.Builder(context);
        init();
    }

    private ExpressDecoration(ExpressDecoration.Builder builder) {
        this.builder = builder;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(builder.color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        int itemCount = parent.getAdapter().getItemCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            int itemPosition = parent.getChildLayoutPosition(childView);
            if (itemPosition == itemCount - 1)
                continue;
            int left = childView.getLeft() + builder.marginLeft - parent.getPaddingLeft();
            int top = childView.getBottom();
            int right = childView.getRight() - builder.marginRight + parent.getPaddingRight();
            int bottom = top + builder.dividerSize;
            c.drawRect(left, top, right, bottom, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        int childCount = parent.getAdapter().getItemCount();
        if (position == childCount - 1)
            return;
        int bottom = builder.dividerSize;
        outRect.set(0, 0, 0, bottom);
    }

    public static class Builder {
        private int expressWidth = 30;//相当于圆的直径，item内容在这个宽度右侧绘制，lin
        private int expressCircleStyle = CIRCLE_STYLE_SOLID;
        private int expressCircleColor = defaultColor;
        private int expressLineSize = defaultSize;
        private int expressLineColor = defaultColor;
        private int marginLeft = 0;
        private int marginRight = 0;
        private int dividerSize = defaultSize;//横线分割线宽度，默认2px
        private int color = defaultColor;//Color
        private Context c;

        public Builder(Context context) {
            this.c = context;
        }

        public ExpressDecoration.Builder marginLeft(@Px int left) {
            this.marginLeft = left;
            return this;
        }

        public ExpressDecoration.Builder marginRight(@Px int right) {
            this.marginRight = right;
            return this;
        }


        public ExpressDecoration.Builder dividerSize(@Px int dividerSize) {
            this.dividerSize = dividerSize;
            return this;
        }

        public ExpressDecoration.Builder colorInt(@ColorInt int color) {
            this.color = color;
            return this;
        }

        public ExpressDecoration.Builder colorRes(@ColorRes int color) {
            this.color = ContextCompat.getColor(c, color);
            return this;
        }

        public ExpressDecoration build() {
            return new ExpressDecoration(this);
        }
    }

}
