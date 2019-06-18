package ysn.com.demo.adapter;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ysn.com.baserecyclerviewadapter.adapter.BaseRecyclerViewAdapter;
import ysn.com.baserecyclerviewadapter.holder.BaseViewHolder;
import ysn.com.demo.R;
import ysn.com.demo.bean.ChildrenBean;
import ysn.com.demo.bean.GroupBean;

/**
 * @Author yangsanning
 * @ClassName GroupedListAdapter
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class GroupedListAdapter extends BaseRecyclerViewAdapter<GroupBean> {

    private ArrayList<GroupBean> datas;

    public GroupedListAdapter(Context context, ArrayList<GroupBean> datas) {
        super(context);
        this.datas = datas;
    }


    @Override
    public List<GroupBean> getDatas() {
        return datas;
    }

    @Override
    public int getGroupCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ChildrenBean> children = datas.get(groupPosition).getChildrenList();
        return children == null ? 0 : children.size();
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
    public int getChildLayout(int viewType) {
        return R.layout.item_adapter_children;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        holder.setText(R.id.adapter_item_header_text,
                datas.get(groupPosition).getHeader());
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {
        holder.setText(R.id.adapter_item_footer_text,
                datas.get(groupPosition).getFooter());
    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        holder.setText(R.id.adapter_item_children_text,
                datas.get(groupPosition).getChildrenList().get(childPosition).getChild());
    }
}
