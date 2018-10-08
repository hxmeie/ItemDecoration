package com.hxm.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hxm on 2018/10/8
 * 描述：联系测试
 */
public class TestDecoration extends RecyclerView.ItemDecoration {
    private Paint paint;

    public TestDecoration() {
        paint=new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count=parent.getChildCount();
        for (int i = 0; i <count ; i++) {
            View child=parent.getChildAt(i);
            int left=child.getLeft()+30;
            int top=child.getBottom();
            int right=child.getRight()-60;
            int bottom=top+15;
            c.drawRect(left,top,right,bottom,paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,15);
    }
}
