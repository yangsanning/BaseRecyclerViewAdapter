package ysn.com.recyclerview.listener;

import ysn.com.recyclerview.adapter.BaseRecyclerViewAdapter;
import ysn.com.recyclerview.holder.BaseViewHolder;

/**
 * @Author yangsanning
 * @ClassName OnMultiClickListener
 * @Description 所有点击事件的汇总
 * @Date 2020/4/4
 * @History 2020/4/4 author: description:
 */
public interface OnMultiClickListener {

    /**
     * 组头布局点击事件
     *
     * @param adapter       {@link BaseRecyclerViewAdapter}
     * @param holder        {@link BaseViewHolder}
     * @param groupPosition 组坐标
     */
    void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);

    /**
     * 子项点击事件
     *
     * @param adapter       {@link BaseRecyclerViewAdapter}
     * @param holder        {@link BaseViewHolder}
     * @param groupPosition 组坐标
     */
    void onItemClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition, int childPosition);

    /**
     * 脚布局点击事件
     *
     * @param adapter       {@link BaseRecyclerViewAdapter}
     * @param holder        {@link BaseViewHolder}
     * @param groupPosition 组坐标
     */
    void onFooterClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
}
