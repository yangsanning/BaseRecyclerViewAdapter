package ysn.com.recyclerview.listener;

import ysn.com.recyclerview.adapter.BaseRecyclerViewAdapter;
import ysn.com.recyclerview.holder.BaseViewHolder;

/**
 * @Author yangsanning
 * @ClassName OnItemClickListener
 * @Description 子项点击事件
 * @Date 2020/4/4
 * @History 2020/4/4 author: description:
 */
public interface OnItemClickListener {

    /**
     * 子项点击事件
     *
     * @param adapter       {@link BaseRecyclerViewAdapter}
     * @param holder        {@link BaseViewHolder}
     * @param groupPosition 组坐标
     */
    void onItemClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition, int childPosition);
}
