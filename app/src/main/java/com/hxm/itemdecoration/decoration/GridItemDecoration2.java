package com.hxm.itemdecoration.decoration;

import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class can only be used in the RecyclerView which use a GridLayoutManager
 * or StaggeredGridLayoutManager, but it's not always work for the StaggeredGridLayoutManager,
 * because we can't figure out which position should belong to the last column or the last row
 */
public class GridItemDecoration2 extends RecyclerView.ItemDecoration {
    public static final int GRID_OFFSETS_HORIZONTAL = GridLayoutManager.HORIZONTAL;
    public static final int GRID_OFFSETS_VERTICAL = GridLayoutManager.VERTICAL;

    private final SparseArray<OffsetsCreator> mTypeOffsetsFactories = new SparseArray<>();
    @Orientation
    private int mOrientation;
    private int mVerticalItemOffsets;
    private int mHorizontalItemOffsets;
    private boolean mIsOffsetEdge;//是否与屏幕之间有间隔
    private boolean mIsOffsetLast;

    public GridItemDecoration2(@Orientation int orientation) {
        setOrientation(orientation);
        mIsOffsetLast = true;
        mIsOffsetEdge = true;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void setVerticalItemOffsets(int verticalItemOffsets) {
        this.mVerticalItemOffsets = verticalItemOffsets;
    }

    public void setHorizontalItemOffsets(int horizontalItemOffsets) {
        this.mHorizontalItemOffsets = horizontalItemOffsets;
    }

    public void setOffsetEdge(boolean isOffsetEdge) {
        this.mIsOffsetEdge = isOffsetEdge;
    }

    public void setOffsetLast(boolean isOffsetLast) {
        this.mIsOffsetLast = isOffsetLast;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //偏移量在测量itemView的时候相当于padding值
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int adapterPosition = parent.getChildAdapterPosition(view);

        int horizontalOffsets = getHorizontalOffsets(parent, view);
        int verticalOffsets = getVerticalOffsets(parent, view);

        boolean isFirstRow = isFirstRow(adapterPosition, spanCount);
        boolean isLastRow = isLastRow(adapterPosition, spanCount, childCount);
        boolean isFirstColumn = isFirstColumn(adapterPosition, spanCount);
        boolean isLastColumn = isLastColumn(adapterPosition, spanCount, childCount);

        if (!mIsOffsetEdge) {//周围是否有间距
            //垂直滑动
            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                //水平方向总偏移量
                int totalHorizontalOffsets = (spanCount - 1) * horizontalOffsets;
                //水平方向平均每个itemView的偏移量
                int hAverageOffset = totalHorizontalOffsets / spanCount;
                int hDelta = horizontalOffsets - hAverageOffset;
                int hLeft = adapterPosition % spanCount * hDelta;
                int hRight = hAverageOffset - hLeft;
                int top = 0;
                int bottom = isLastRow ? 0 : verticalOffsets;
                outRect.set(hLeft, top, hRight, bottom);
            } else {
                //水平滑动
                //垂直方向总偏移量
                int totalVerticalOffsets = (spanCount - 1) * verticalOffsets;
                //垂直方向平均每个itemView的偏移量
                int vAverageOffset = totalVerticalOffsets / spanCount;
                int vDeltaTimes = adapterPosition % spanCount;
                int vTop = isFirstRow ? 0 : vDeltaTimes * (verticalOffsets - vAverageOffset);
                int vBottom = isFirstRow ? vAverageOffset : (vDeltaTimes + 1) * vAverageOffset - vDeltaTimes * verticalOffsets;
                int left = isFirstColumn ? 0 : horizontalOffsets;
                int right = 0;
                outRect.set(left, vTop, right, vBottom);
            }

        } else {
            //垂直滚动
            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                int totalHorizontalOffsets = (spanCount + 1) * horizontalOffsets;
                int hAverageOffset = totalHorizontalOffsets / spanCount;
                int hDeltaTimes = adapterPosition % spanCount;
                int hLeft = isFirstColumn ? horizontalOffsets : (hDeltaTimes + 1) * horizontalOffsets - hDeltaTimes * hAverageOffset;
                int hRight = isFirstColumn ? hAverageOffset - horizontalOffsets : (hDeltaTimes + 1) * (hAverageOffset - horizontalOffsets);
                int vTop = isFirstRow ? verticalOffsets : 0;
                outRect.set(hLeft, vTop, hRight, verticalOffsets);
            } else {
                int totalVerticalOffsets = (spanCount + 1) * verticalOffsets;
                int vAverageOffset = totalVerticalOffsets / spanCount;
                int vDeltaTimes = adapterPosition % spanCount;
                int vTop = isFirstRow ? verticalOffsets : (vDeltaTimes + 1) * verticalOffsets - vDeltaTimes * vAverageOffset;
                int vBottom = isFirstRow ? vAverageOffset - verticalOffsets : (vDeltaTimes + 1) * (vAverageOffset - verticalOffsets);
                int hRight = isLastColumn ? horizontalOffsets : 0;
                outRect.set(horizontalOffsets, vTop, hRight, vBottom);
            }
        }
    }

    private int getHorizontalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mHorizontalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final OffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createHorizontal(parent, adapterPosition);
        }

        return 0;
    }

    private int getVerticalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mVerticalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final OffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createVertical(parent, adapterPosition);
        }

        return 0;
    }

    /**
     * 是否是第一列
     *
     * @param position
     * @param spanCount
     * @return
     */
    private boolean isFirstColumn(int position, int spanCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            return position % spanCount == 0;
        } else {
            return position < spanCount;
        }
    }

    /**
     * 是否是最后一列
     *
     * @param position
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastColumn(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            return (position + 1) % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            //最后一列itemView的position一定大于等于itemView总数量-最后一列itemView数量
            return position >= childCount - lastColumnCount;
        }
    }

    /**
     * 是否是第一行
     *
     * @param position
     * @param spanCount
     * @return
     */
    private boolean isFirstRow(int position, int spanCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            //垂直方向上
            return position < spanCount;
        } else {
            //水平方向上
            return position % spanCount == 0;
        }
    }

    /**
     * 是否是最后一行
     *
     * @param position
     * @param spanCount
     * @param childCount
     * @return
     */
    private boolean isLastRow(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            int lastRowCount = childCount % spanCount;
            lastRowCount = lastRowCount == 0 ? spanCount : lastRowCount;
            return position >= childCount - lastRowCount;
        } else {
            return (position + 1) % spanCount == 0;
        }
    }

    /**
     * 是否是最后一行
     */
    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int lastRowCount = childCount % spanCount;
            lastRowCount = lastRowCount == 0 ? spanCount : lastRowCount;
            return pos >= childCount - lastRowCount;
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

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else {
            throw new UnsupportedOperationException("the GridDividerItemDecoration can only be used in " +
                    "the RecyclerView which use a GridLayoutManager or StaggeredGridLayoutManager");
        }
    }

    public void registerTypeDrawable(int itemType, OffsetsCreator offsetsCreator) {
        mTypeOffsetsFactories.put(itemType, offsetsCreator);
    }

    @IntDef({
            GRID_OFFSETS_HORIZONTAL,
            GRID_OFFSETS_VERTICAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    public interface OffsetsCreator {
        int createVertical(RecyclerView parent, int adapterPosition);

        int createHorizontal(RecyclerView parent, int adapterPosition);
    }

}
