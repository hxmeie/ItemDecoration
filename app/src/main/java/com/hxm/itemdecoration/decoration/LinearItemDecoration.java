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
 * Created by hxm on 2018/10/8
 * <p>
 * 例：
 * <p>
 * 1、自定义相应属性
 * LinearItemDecoration decoration=new LinearItemDecoration.Builder(this)
 * .colorRes(R.colorRes.aaaa)
 * .marginLeft(SizeUtil.dp2px(15))
 * .marginRight(SizeUtil.dp2px(15))
 * .drawHeader(false)
 * .drawFooter(false)
 * .dividerSize(SizeUtil.dp2px(10))
 * .build();
 * <p>
 * 2、使用默认值
 * LinearItemDecoration decoration=new LinearItemDecoration(this);
 * <p>
 * 描述：垂直方向，第一项顶部不画线，最后一项底部不划线
 */
public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;
    private Builder builder;

    public LinearItemDecoration(Context context) {
        builder = new Builder(context);
        init();
    }

    private LinearItemDecoration(Builder builder) {
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
            if (itemPosition == 0 && !builder.drawHeader)
                continue;
            if (itemPosition == itemCount - 1)
                continue;
            if (itemPosition == itemCount - 2 && !builder.drawFooter)
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
        if ((position == 0 && !builder.drawHeader) || (position == childCount - 1)
                || ((position == childCount - 2) && !builder.drawFooter)) {
            return;
        }
        int bottom = builder.dividerSize;
        outRect.set(0, 0, 0, bottom);
    }

    public static class Builder {
        private boolean drawHeader = true;//第一项底部是否画线，默认画
        private boolean drawFooter = true;//最后一项顶部是否画线，默认画
        private int marginLeft = 0;
        private int marginRight = 0;
        private int dividerSize = 2;//横线分割线宽度，默认2px
        private int color = Color.parseColor("#000000");//Color
        private Context c;

        public Builder(Context context) {
            this.c = context;
        }

        public Builder drawHeader(boolean b) {
            this.drawHeader = b;
            return this;
        }

        public Builder drawFooter(boolean b) {
            this.drawFooter = b;
            return this;
        }

        public Builder marginLeft(@Px int left) {
            this.marginLeft = left;
            return this;
        }

        public Builder marginRight(@Px int right) {
            this.marginRight = right;
            return this;
        }


        public Builder dividerSize(@Px int dividerSize) {
            this.dividerSize = dividerSize;
            return this;
        }

        public Builder colorInt(@ColorInt int color) {
            this.color = color;
            return this;
        }

        public Builder colorRes(@ColorRes int color) {
            this.color = ContextCompat.getColor(c, color);
            return this;
        }

        public LinearItemDecoration build() {
            return new LinearItemDecoration(this);
        }
    }
}
