package ysn.com.baserecyclerviewadapter.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

/**
 * @Author yangsanning
 * @ClassName LayoutManagerUtils
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class LayoutManagerUtils {

    /**
     * 是否是StaggeredGridLayout
     */
    public static boolean isStaggeredGridLayout(ViewGroup.LayoutParams params) {
        return params instanceof StaggeredGridLayoutManager.LayoutParams;
    }

    /**
     * 获取列表当前第一个显示的item
     */
    public static int getFirstVisibleItem(RecyclerView recyclerView) {
        int firstVisibleItem = -1;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            if (layoutManager instanceof GridLayoutManager) {
                firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof LinearLayoutManager) {
                firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] firstPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(firstPositions);
                firstVisibleItem = getMinValue(firstPositions);
            }
        }
        return firstVisibleItem;
    }

    /**
     * 获取最小值
     */
    public static int getMinValue(int[] values) {
        int minValue = values[0];
        for (int value : values) {
            if (value < minValue) {
                minValue = value;
            }
        }
        return minValue;
    }
}
