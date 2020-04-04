package ysn.com.recyclerview.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ysn.com.recyclerview.holder.BaseViewHolder;
import ysn.com.recyclerview.mode.annotation.AdapterType;
import ysn.com.recyclerview.mode.bean.Group;
import ysn.com.recyclerview.utils.LayoutManagerUtils;

/**
 * @Author yangsanning
 * @ClassName BaseRecyclerViewAdapter
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context context;
    /**
     * 是否启用DataBinding
     */
    private boolean isEnabledDataBinding;

    /**
     * 记录组信息
     */
    protected ArrayList<Group> groupList = new ArrayList<>();
    private boolean isDataChanged;
    private int tempPosition;

    private OnHeaderClickListener onHeaderClickListener;
    private OnFooterClickListener onFooterClickListener;
    private OnChildClickListener onChildClickListener;

    public BaseRecyclerViewAdapter(Context context) {
        this(context, (false));
    }

    public BaseRecyclerViewAdapter(Context context, boolean isEnabledDataBinding) {
        this.context = context;
        this.isEnabledDataBinding = isEnabledDataBinding;
        registerAdapterDataObserver(new DataObserver());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        resetGroupList();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // 针对StaggeredGridLayout进行，保证组头和组尾占满一行。
        if (LayoutManagerUtils.isStaggeredGridLayout(holder.itemView.getLayoutParams())) {
            int position = holder.getLayoutPosition();
            if (getItemType(position) == AdapterType.HEADER || getItemType(position) == AdapterType.FOOTER) {
                ((StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams()).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isDataChanged) {
            resetGroupList();
        }
        return getRangeGroupItemCount(0, groupList.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isEnabledDataBinding) {
            view = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(tempPosition, viewType), parent, (false)).getRoot();
        } else {
            view = LayoutInflater.from(context).inflate(getLayoutId(tempPosition, viewType), parent, (false));
        }
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final int groupPosition = getGroupPosition(position);
        switch (getItemType(position)) {
            case AdapterType.HEADER:
                if (onHeaderClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onHeaderClickListener != null) {
                                onHeaderClickListener.onHeaderClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                        }
                    });
                }
                onBindHeaderViewHolder((BaseViewHolder) holder, groupPosition);
                break;
            case AdapterType.FOOTER:
                if (onFooterClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onFooterClickListener != null) {
                                onFooterClickListener.onFooterClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                        }
                    });
                }
                onBindFooterViewHolder((BaseViewHolder) holder, groupPosition);
                break;
            case AdapterType.CHILDREN:
                final int groupChildPosition = getGroupChildPosition(groupPosition, position);
                if (onChildClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChildClickListener != null) {
                                onChildClickListener.onChildClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition, groupChildPosition);
                            }
                        }
                    });
                }
                onBindChildViewHolder((BaseViewHolder) holder, groupPosition, groupChildPosition);
                break;
            default:
                break;
        }
    }

    /******************************************** 私有方法 ****************************************/

    /**
     * 重置组列表
     */
    private void resetGroupList() {
        groupList.clear();
        int groupCount = getGroupCount();
        for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
            groupList.add(new Group(hasHeader(groupPosition), hasFooter(groupPosition), getChildrenCount(groupPosition)));
        }
        isDataChanged = false;
    }

    private int getLayoutId(int position, int viewType) {
        int itemType = getItemType(position);
        if (itemType == AdapterType.HEADER) {
            return getHeaderLayout(viewType);
        } else if (itemType == AdapterType.FOOTER) {
            return getFooterLayout(viewType);
        } else if (itemType == AdapterType.CHILDREN) {
            return getChildLayout(viewType);
        }
        return 0;
    }

    /**
     * 根据position获取item类型
     */
    private int getItemType(int position) {
        int itemCount = 0;
        for (Group group : groupList) {
            if (group.hasHeader()) {
                itemCount += 1;
                if (position < itemCount) {
                    return AdapterType.HEADER;
                }
            }
            itemCount += group.getChildrenCount();
            if (position < itemCount) {
                return AdapterType.CHILDREN;
            }
            if (group.hasFooter()) {
                itemCount += 1;
                if (position < itemCount) {
                    return AdapterType.FOOTER;
                }
            }
        }
        throw new IndexOutOfBoundsException("获取类型失败, " + "position: " + position + ", itemCount: " + getItemCount());
    }

    /******************************************** 公开方法 ****************************************/

    /**
     * 根据坐标(position)获取该坐标所在组的坐标
     */
    public int getGroupPosition(int position) {
        // 当前组的最后一个position
        int groupLastPosition = 0;
        int groupCount = groupList.size();
        for (int groupPosition = 0; groupPosition < groupCount; groupPosition++) {
            // 加上该组子项个数
            groupLastPosition += getGroupItemCount(groupPosition);
            // 当position小于当前组的最后一个position, 则为该组
            if (position < groupLastPosition) {
                return groupPosition;
            }
        }
        return -1;
    }

    /**
     * 根据组坐标(groupPosition)获取该组Item数量（头+尾+子项）
     */
    public int getGroupItemCount(int groupPosition) {
        int itemCount = 0;
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            itemCount = groupList.get(groupPosition).getItemCount();
        }
        return itemCount;
    }

    /**
     * 根据组坐标以及当前坐标, 获取当前坐标在该组的位置
     *
     * @param groupPosition 组的位置
     * @param position      当前位置
     * @return 当前坐标在指定组的位置
     */
    public int getGroupChildPosition(int groupPosition, int position) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            int itemCount = getRangeGroupItemCount(0, groupPosition + 1);
            Group group = groupList.get(groupPosition);
            int childPosition = group.getChildrenCount() - (itemCount - position) + (group.hasFooter() ? 1 : 0);
            if (childPosition >= 0) {
                return childPosition;
            }
        }
        return -1;
    }

    /**
     * 获取指定范围组的item总和
     */
    public int getRangeGroupItemCount(int start, int end) {
        int itemCount = 0;
        int size = groupList.size();
        for (int i = start; i < size && i < start + end; i++) {
            itemCount += getGroupItemCount(i);
        }
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        tempPosition = position;
        int groupPosition = getGroupPosition(position);
        int type = getItemType(position);
        if (type == AdapterType.HEADER) {
            return getHeaderViewType(groupPosition);
        } else if (type == AdapterType.FOOTER) {
            return getFooterViewType(groupPosition);
        } else if (type == AdapterType.CHILDREN) {
            int childPosition = getGroupChildPosition(groupPosition, position);
            return getChildViewType(groupPosition, childPosition);
        }
        return super.getItemViewType(position);
    }

    public int getHeaderViewType(int groupPosition) {
        return AdapterType.HEADER;
    }

    public int getFooterViewType(int groupPosition) {
        return AdapterType.FOOTER;
    }

    public int getChildViewType(int groupPosition, int childPosition) {
        return AdapterType.CHILDREN;
    }

    /**
     * 刷新数据列表
     */
    public void notifyDataChanged() {
        isDataChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 根据组坐标(groupPosition)刷新该组(包括头、尾、子项)
     */
    public void notifyGroupChanged(int groupPosition) {
        int index = getGroupHeaderPosition(groupPosition);
        int itemCount = getGroupItemCount(groupPosition);
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount);
        }
    }

    /**
     * 根据组坐标获取该组头布局坐标, 如果该组没有头布局则返回-1
     * @param groupPosition 组坐标
     * @return 指定组坐标的头布局坐标
     */
    public int getGroupHeaderPosition(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            if (groupList.get(groupPosition).hasHeader()) {
                return getRangeGroupItemCount(0, groupPosition);
            }else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 刷新多组(包括头、尾、子项)
     */
    public void notifyGroupRangeChanged(int groupPosition, int count) {
        int index = getGroupHeaderPosition(groupPosition);
        int itemCount = 0;
        if (groupPosition + count <= groupList.size()) {
            itemCount = getRangeGroupItemCount(groupPosition, groupPosition + count);
        } else {
            itemCount = getRangeGroupItemCount(groupPosition, groupList.size());
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeChanged(index, itemCount);
        }
    }

    /**
     * 根据组坐标(groupPosition)刷该组头
     */
    public void notifyHeaderChanged(int groupPosition) {
        int index = getGroupHeaderPosition(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * 根据组坐标(groupPosition)刷新该组尾
     */
    public void notifyFooterChanged(int groupPosition) {
        int index = getGroupFooterPosition(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * 根据组坐标(groupPosition)获取该组尾下标, 如果该组没有组尾返回-1
     */
    public int getGroupFooterPosition(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            Group group = groupList.get(groupPosition);
            if (!group.hasFooter()) {
                return -1;
            }
            return getRangeGroupItemCount(0, groupPosition + 1) - 1;
        }
        return -1;
    }

    /**
     * 根据组坐标(groupPosition)和子项组内坐标(childrenGroupPosition)刷新指定子项
     */
    public void notifyChildrenChanged(int groupPosition, int childrenGroupPosition) {
        int index = getChildrenPosition(groupPosition, childrenGroupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * 根据组坐标(groupPosition)和子项组内坐标(childrenGroupPosition)获取组指定的子项下标,如果没有返回-1
     */
    public int getChildrenPosition(int groupPosition, int childrenGroupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            Group group = groupList.get(groupPosition);
            if (group.getChildrenCount() > childrenGroupPosition) {
                int itemCount = getRangeGroupItemCount(0, groupPosition);
                return itemCount + childrenGroupPosition + (group.hasHeader() ? 1 : 0);
            }
        }
        return -1;
    }


    /**
     * 刷新指定组指定范围子项
     */
    public void notifyRangeChildrenChanged(int groupPosition, int childrenGroupPosition, int count) {
        if (groupPosition < groupList.size()) {
            int index = getChildrenPosition(groupPosition, childrenGroupPosition);
            if (index >= 0) {
                Group group = groupList.get(groupPosition);
                if (group.getChildrenCount() >= childrenGroupPosition + count) {
                    notifyItemRangeChanged(index, count);
                } else {
                    notifyItemRangeChanged(index, (group.getChildrenCount() - childrenGroupPosition));
                }
            }
        }
    }

    /**
     * 刷新指定组子项
     */
    public void notifyChildrenChanged(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            int index = getChildrenPosition(groupPosition, 0);
            if (index >= 0) {
                Group group = groupList.get(groupPosition);
                notifyItemRangeChanged(index, group.getChildrenCount());
            }
        }
    }

    /**
     * 删除所有数据
     */
    public void notifyDataRemoved() {
        notifyItemRangeRemoved(0, getItemCount());
        groupList.clear();
    }

    /**
     * 删除某一组数据(包含头、尾、子项)
     */
    public void notifyGroupRemoved(int groupPosition) {
        int index = getGroupHeaderPosition(groupPosition);
        int itemCount = getGroupItemCount(groupPosition);
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount);
            notifyItemRangeChanged(index, getItemCount() - itemCount);
            groupList.remove(groupPosition);
        }
    }

    /**
     * 删除一定范围的组数据(包含头、尾、子项)
     */
    public void notifyGroupRangeRemoved(int groupPosition, int count) {
        int index = getGroupHeaderPosition(groupPosition);
        int itemCount = 0;
        if (groupPosition + count <= groupList.size()) {
            itemCount = getRangeGroupItemCount(groupPosition, groupPosition + count);
        } else {
            itemCount = getRangeGroupItemCount(groupPosition, groupList.size());
        }
        if (index >= 0 && itemCount > 0) {
            notifyItemRangeRemoved(index, itemCount);
            notifyItemRangeChanged(index, getItemCount() - itemCount);
            groupList.remove(groupPosition);
        }
    }

    /**
     * 根据组坐标(groupPosition)删除指定组头
     */
    public void notifyGroupHeaderRemoved(int groupPosition) {
        int index = getGroupHeaderPosition(groupPosition);
        if (index >= 0) {
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, (getItemCount() - index));
            groupList.get(groupPosition).setHasHeader(false);
        }
    }


    /**
     * 根据组坐标(groupPosition)删除指定组尾
     */
    public void notifyGroupFooterRemoved(int groupPosition) {
        int index = getGroupFooterPosition(groupPosition);
        if (index >= 0) {
            Group group = groupList.get(groupPosition);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, getItemCount() - index);
            group.setHasFooter(false);
        }
    }

    /**
     * 根据组坐标(groupPosition)删除指定组内子项坐标(childPosition)的子项
     */
    public void notifyChildrenRemoved(int groupPosition, int childPosition) {
        int index = getChildrenPosition(groupPosition, childPosition);
        if (index >= 0) {
            Group group = groupList.get(groupPosition);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, getItemCount() - index);
            group.setChildrenCount(group.getChildrenCount() - 1);
        }
    }

    /**
     * 根据组坐标(groupPosition)删除指定组内一定范围的子项
     */
    public void notifyChildrenRangeRemoved(int groupPosition, int childPosition, int count) {
        if (groupPosition < groupList.size()) {
            int index = getChildrenPosition(groupPosition, childPosition);
            if (index >= 0) {
                Group group = groupList.get(groupPosition);
                int childCount = group.getChildrenCount();
                int removeCount = count;
                if (childCount < childPosition + count) {
                    removeCount = childCount - childPosition;
                }
                notifyItemRangeRemoved(index, removeCount);
                notifyItemRangeChanged(index, getItemCount() - removeCount);
                group.setChildrenCount(childCount - removeCount);
            }
        }
    }

    /**
     * 根据组坐标(groupPosition)删除指定组所有子项
     */
    public void notifyChildrenRemoved(int groupPosition) {
        if (groupPosition < groupList.size()) {
            int index = getChildrenPosition(groupPosition, 0);
            if (index >= 0) {
                Group group = groupList.get(groupPosition);
                int itemCount = group.getChildrenCount();
                notifyItemRangeRemoved(index, itemCount);
                notifyItemRangeChanged(index, getItemCount() - itemCount);
                group.setChildrenCount(0);
            }
        }
    }

    /**
     * 根据组坐标(groupPosition)插入指定组
     */
    public void notifyGroupInserted(int groupPosition) {
        Group group = new Group(hasHeader(groupPosition), hasFooter(groupPosition), getChildrenCount(groupPosition));
        if (groupPosition < groupList.size()) {
            groupList.add(groupPosition, group);
        } else {
            groupList.add(group);
            groupPosition = groupList.size() - 1;
        }

        int index = getRangeGroupItemCount(0, groupPosition);
        int itemCount = getGroupItemCount(groupPosition);
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount);
            notifyItemRangeChanged(index + itemCount, getItemCount() - index);
        }
    }

    /**
     * 根据组坐标(groupPosition)插入多组
     */
    public void notifyGroupRangeInserted(int groupPosition, int count) {
        ArrayList<Group> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Group group = new Group(hasHeader(i), hasFooter(i), getChildrenCount(i));
            list.add(group);
        }

        if (groupPosition < groupList.size()) {
            groupList.addAll(groupPosition, list);
        } else {
            groupList.addAll(list);
            groupPosition = groupList.size() - list.size();
        }

        int index = getRangeGroupItemCount(0, groupPosition);
        int itemCount = getRangeGroupItemCount(groupPosition, count);
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount);
            notifyItemRangeChanged(index + itemCount, getItemCount() - index);
        }
    }


    /**
     * 根据组坐标(groupPosition)插入组头
     */
    public void notifyHeaderInserted(int groupPosition) {
        if (groupPosition < groupList.size() && 0 > getGroupHeaderPosition(groupPosition)) {
            groupList.get(groupPosition).setHasHeader(true);
            int index = getRangeGroupItemCount(0, groupPosition);
            notifyItemInserted(index);
            notifyItemRangeChanged(index + 1, getItemCount() - index);
        }
    }

    /**
     * 根据组坐标(groupPosition)插入组尾
     */
    public void notifyFooterInserted(int groupPosition) {
        if (groupPosition < groupList.size() && 0 > getGroupFooterPosition(groupPosition)) {
            Group group = groupList.get(groupPosition);
            group.setHasFooter(true);
            int index = getRangeGroupItemCount(0, groupPosition + 1);
            notifyItemInserted(index);
            notifyItemRangeChanged(index + 1, getItemCount() - index);
        }
    }

    /**
     * 根据组坐标(groupPosition)插入指定数据
     */
    public void notifyChildInserted(int groupPosition, int childPosition) {
        if (groupPosition < groupList.size()) {
            Group group = groupList.get(groupPosition);
            int index = getChildrenPosition(groupPosition, childPosition);
            if (index < 0) {
                index = getRangeGroupItemCount(0, groupPosition);
                index += group.hasHeader() ? 1 : 0;
                index += group.getChildrenCount();
            }
            group.setChildrenCount(group.getChildrenCount() + 1);
            notifyItemInserted(index);
            notifyItemRangeChanged(index + 1, getItemCount() - index);
        }
    }

    /**
     * 根据组坐标(groupPosition)插入多个数据
     */
    public void notifyChildRangeInserted(int groupPosition, int childPosition, int count) {
        if (groupPosition < groupList.size()) {
            int index = getRangeGroupItemCount(0, groupPosition);
            Group group = groupList.get(groupPosition);
            if (group.hasHeader()) {
                index++;
            }
            if (childPosition < group.getChildrenCount()) {
                index += childPosition;
            } else {
                index += group.getChildrenCount();
            }
            if (count > 0) {
                group.setChildrenCount(group.getChildrenCount() + count);
                notifyItemRangeInserted(index, count);
                notifyItemRangeChanged(index + count, getItemCount() - index);
            }
        }
    }

    /**
     * 根据组坐标(groupPosition)插入一组数据
     */
    public void notifyChildrenInserted(int groupPosition) {
        if (groupPosition < groupList.size()) {
            int index = getRangeGroupItemCount(0, groupPosition);
            Group group = groupList.get(groupPosition);
            if (group.hasHeader()) {
                index++;
            }
            int itemCount = getChildrenCount(groupPosition);
            if (itemCount > 0) {
                group.setChildrenCount(itemCount);
                notifyItemRangeInserted(index, itemCount);
                notifyItemRangeChanged(index + itemCount, getItemCount() - index);
            }
        }
    }

    /**
     * 设置组头点击事件
     */
    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }

    /**
     * 组头点击事件
     */
    public interface OnHeaderClickListener {
        void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }

    /**
     * 设置组尾点击事件
     */
    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener) {
        this.onFooterClickListener = onFooterClickListener;
    }

    /**
     * 组尾点击事件
     */
    public interface OnFooterClickListener {
        void onFooterClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }


    /**
     * 设置子项点击事件
     */
    public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
        this.onChildClickListener = onChildClickListener;
    }

    /**
     * 子项点击事件
     */
    public interface OnChildClickListener {
        void onChildClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder,
                          int groupPosition, int childPosition);
    }

    public abstract List<T> getDatas();

    public abstract int getGroupCount();

    public abstract int getChildrenCount(int groupPosition);

    public abstract boolean hasHeader(int groupPosition);

    public abstract boolean hasFooter(int groupPosition);

    public abstract int getHeaderLayout(int viewType);

    public abstract int getFooterLayout(int viewType);

    public abstract int getChildLayout(int viewType);

    public abstract void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int groupChildPosition);

    class DataObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onChanged() {
            isDataChanged = true;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            isDataChanged = true;
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            isDataChanged = true;
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            isDataChanged = true;
        }
    }
}
