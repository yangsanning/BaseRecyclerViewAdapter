package ysn.com.demo.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ysn.com.recyclerview.adapter.BaseRecyclerViewAdapter;
import ysn.com.recyclerview.holder.BaseViewHolder;
import ysn.com.demo.R;
import ysn.com.demo.bean.ChildrenBean;
import ysn.com.demo.bean.ExpandableGroupBean;

/**
 * @Author yangsanning
 * @ClassName ExpandableAdapter
 * @Description 一句话概括作用
 * @Date 2019/6/18
 * @History 2019/6/18 author: description:
 */
public class ExpandableAdapter extends BaseRecyclerViewAdapter<ExpandableGroupBean> {

    private ArrayList<ExpandableGroupBean> expandableGroupList;

    public ExpandableAdapter(Context context, ArrayList<ExpandableGroupBean> groups) {
        super(context);
        expandableGroupList = groups;
    }

    @Override
    public List<ExpandableGroupBean> getDatas() {
        return expandableGroupList;
    }

    @Override
    public int getGroupCount() {
        return expandableGroupList == null ? 0 : expandableGroupList.size();
    }

    @Override
    public int getItemCount(int groupPosition) {
        //如果当前组为展开时，返回子项数，否则返回0
        if (isExpand(groupPosition)) {
            ArrayList<ChildrenBean> childrenList = expandableGroupList.get(groupPosition).getChildrenList();
            return childrenList == null ? 0 : childrenList.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.item_adapter_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.item_adapter_footer;
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_adapter_children;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        ExpandableGroupBean expandableGroupBean = expandableGroupList.get(groupPosition);
        holder.setText(R.id.adapter_item_header_text, expandableGroupBean.getHeader());
        holder.setVisible(R.id.adapter_item_expand, View.VISIBLE);
        holder.get(R.id.adapter_item_expand).setRotation(expandableGroupBean.isExpand() ? 90 : 0);
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        holder.setText(R.id.adapter_item_footer_text, expandableGroupList.get(groupPosition).getFooter());
    }

    @Override
    public void onBindItemViewHolder(BaseViewHolder holder, int groupPosition, int groupItemPosition) {
        holder.setText(R.id.adapter_item_children_text,
                expandableGroupList.get(groupPosition).getChildrenList().get(groupItemPosition).getChild());
    }

    /**
     * 是否展开
     */
    public boolean isExpand(int groupPosition) {
        return expandableGroupList.get(groupPosition).isExpand();
    }

    /**
     * 展开指定组
     */
    public void expandGroup(int groupPosition) {
        expandGroup(groupPosition, false);
    }

    /**
     * 展开指定组
     */
    public void expandGroup(int groupPosition, boolean animate) {
        ExpandableGroupBean expandableGroup = expandableGroupList.get(groupPosition);
        expandableGroup.setExpand(true);
        if (animate) {
            notifyChildrenInserted(groupPosition);
        } else {
            notifyDataChanged();
        }
    }

    /**
     * 收起定组
     */
    public void collapseGroup(int groupPosition) {
        collapseGroup(groupPosition, false);
    }

    /**
     * 收起定组
     */
    public void collapseGroup(int groupPosition, boolean animate) {
        ExpandableGroupBean expandableGroup = expandableGroupList.get(groupPosition);
        expandableGroup.setExpand(false);
        if (animate) {
            notifyItemRemove(groupPosition);
        } else {
            notifyDataChanged();
        }
    }
}
