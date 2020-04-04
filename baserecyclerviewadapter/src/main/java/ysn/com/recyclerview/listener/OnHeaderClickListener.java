package ysn.com.recyclerview.listener;

import ysn.com.recyclerview.adapter.BaseRecyclerViewAdapter;
import ysn.com.recyclerview.holder.BaseViewHolder;

/**
 * @Author yangsanning
 * @ClassName OnHeaderClickListener
 * @Description 组头布局点击事件
 * @Date 2020/4/4
 * @History 2020/4/4 author: description:
 */
public interface OnHeaderClickListener {

    /**
     * 组头布局点击事件
     *
     * @param adapter       {@link BaseRecyclerViewAdapter}
     * @param holder        {@link BaseViewHolder}
     * @param groupPosition 组坐标
     */
    void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
}
