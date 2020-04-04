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
import ysn.com.recyclerview.listener.OnFooterClickListener;
import ysn.com.recyclerview.listener.OnHeaderClickListener;
import ysn.com.recyclerview.listener.OnItemClickListener;
import ysn.com.recyclerview.listener.OnMultiClickListener;
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

    private OnMultiClickListener onMultiClickListener;
    private OnHeaderClickListener onHeaderClickListener;
    private OnItemClickListener onItemClickListener;
    private OnFooterClickListener onFooterClickListener;

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

    @Override
    public int getItemViewType(int position) {
        tempPosition = position;
        return getItemType(position);
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
                if (onMultiClickListener != null || onHeaderClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onMultiClickListener != null) {
                                onMultiClickListener.onHeaderClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                            if (onHeaderClickListener != null) {
                                onHeaderClickListener.onHeaderClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                        }
                    });
                }
                onBindHeaderViewHolder((BaseViewHolder) holder, groupPosition);
                break;
            case AdapterType.ITEM:
                final int groupItemPosition = getGroupItemPosition(groupPosition, position);
                if (onMultiClickListener != null || onItemClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onMultiClickListener != null) {
                                onMultiClickListener.onItemClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition, groupItemPosition);
                            }
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition, groupItemPosition);
                            }
                        }
                    });
                }
                onBindItemViewHolder((BaseViewHolder) holder, groupPosition, groupItemPosition);
                break;
            case AdapterType.FOOTER:
                if (onMultiClickListener != null || onFooterClickListener != null) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onMultiClickListener != null) {
                                onMultiClickListener.onFooterClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                            if (onFooterClickListener != null) {
                                onFooterClickListener.onFooterClick((BaseRecyclerViewAdapter.this),
                                        (BaseViewHolder) holder, groupPosition);
                            }
                        }
                    });
                }
                onBindFooterViewHolder((BaseViewHolder) holder, groupPosition);
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
            groupList.add(new Group(hasHeader(groupPosition), hasFooter(groupPosition), getItemCount(groupPosition)));
        }
        isDataChanged = false;
    }

    private int getLayoutId(int position, int viewType) {
        int itemType = getItemType(position);
        if (itemType == AdapterType.HEADER) {
            return getHeaderLayout(viewType);
        } else if (itemType == AdapterType.ITEM) {
            return getItemLayout(viewType);
        } else if (itemType == AdapterType.FOOTER) {
            return getFooterLayout(viewType);
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
            itemCount += group.getItemCount();
            if (position < itemCount) {
                return AdapterType.ITEM;
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

    /**
     * 根据组坐标获取该组Item数量（头+尾+子项）
     *
     * @param groupPosition 组坐标
     * @return 指定组坐标的Item数量（头+尾+子项）
     */
    private int getGroupItemCount(int groupPosition) {
        int itemCount = 0;
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            itemCount = groupList.get(groupPosition).getRealItemCount();
        }
        return itemCount;
    }

    /******************************************** 公开方法 ****************************************/

    /**
     * 根据坐标获取该坐标所在组的坐标
     *
     * @param position 坐标
     * @return 指定坐标所在组的坐标
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
     * 根据组坐标获取该组头布局坐标, 如果该组没有头布局则返回-1
     *
     * @param groupPosition 组坐标
     * @return 指定组坐标的头布局坐标
     */
    public int getGroupHeaderPosition(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            if (groupList.get(groupPosition).hasHeader()) {
                // 指定组的头布局前面个数总和即为指定组头布局的坐标
                int itemCount = 0;
                for (int position = 0; position < groupPosition; position++) {
                    itemCount += getGroupItemCount(position);
                }
                return itemCount;
            }
        }
        return -1;
    }

    /**
     * 根据组坐标以及当前坐标, 获取当前坐标在该组的位置
     *
     * @param groupPosition 组的位置
     * @param position      当前位置
     * @return 当前坐标在指定组的位置
     */
    public int getGroupItemPosition(int groupPosition, int position) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            int itemCount = getRangeGroupItemCount(0, groupPosition + 1);
            Group group = groupList.get(groupPosition);
            int itemPosition = group.getItemCount() - (itemCount - position) + (group.hasFooter() ? 1 : 0);
            if (itemPosition >= 0) {
                return itemPosition;
            }
        }
        return -1;
    }

    /**
     * 根据组坐标和子项组内坐标获取指定组的子项坐标, 如果没有返回-1
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     * @return 指定组的子项坐标
     */
    public int getGroupItemPositionByIndex(int groupPosition, int groupItemIndex) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            Group group = groupList.get(groupPosition);
            if (group.getItemCount() > groupItemIndex) {
                // 指定组的头布局前面item总和
                int itemCount = 0;
                for (int position = 0; position < groupPosition; position++) {
                    itemCount += getGroupItemCount(position);
                }
                return itemCount + groupItemIndex + (group.hasHeader() ? 1 : 0);
            }
        }
        return -1;
    }

    /**
     * 根据组坐标获取该组脚坐标, 如果该组没有组尾返回-1
     *
     * @param groupPosition 组坐标
     * @return 指定组坐标的脚布局坐标
     */
    public int getGroupFooterPosition(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            if (groupList.get(groupPosition).hasFooter()) {
                // 从第一组到指定组总和-1即为指定组脚部局坐标
                int itemCount = 0;
                for (int position = 0; position <= groupPosition; position++) {
                    itemCount += getGroupItemCount(position);
                }
                return itemCount - 1;
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

    /**
     * 刷新数据列表
     */
    public void notifyDataChanged() {
        isDataChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 根据组坐标刷新该组布局(头、子项、尾)
     *
     * @param groupPosition 组坐标
     */
    public void notifyGroupChanged(int groupPosition) {
        int groupHeaderPosition = getGroupHeaderPosition(groupPosition);
        int groupItemCount = getGroupItemCount(groupPosition);
        if (groupHeaderPosition >= 0 && groupItemCount > 0) {
            notifyItemRangeChanged(groupHeaderPosition, groupItemCount);
        }
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
     * 根据组坐标刷该组头布局
     *
     * @param groupPosition 组坐标
     */
    public void notifyHeaderChanged(int groupPosition) {
        int index = getGroupHeaderPosition(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * 根据组坐标刷该指定组子项
     *
     * @param groupPosition 组坐标
     */
    public void notifyItemChange(int groupPosition) {
        if (groupPosition >= 0 && groupPosition < groupList.size()) {
            int itemPosition = getGroupItemPositionByIndex(groupPosition, 0);
            if (itemPosition >= 0) {
                notifyItemRangeChanged(itemPosition, groupList.get(groupPosition).getItemCount());
            }
        }
    }

    /**
     * 根据组坐标和子项组内坐标刷新指定子项
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     */
    public void notifyItemChange(int groupPosition, int groupItemIndex) {
        int itemPosition = getGroupItemPositionByIndex(groupPosition, groupItemIndex);
        if (itemPosition >= 0) {
            notifyItemChanged(itemPosition);
        }
    }

    /**
     * 刷新指定组指定范围子项
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     * @param refreshCount       刷新个数
     */
    public void notifyItemChange(int groupPosition, int groupItemIndex, int refreshCount) {
        if (groupPosition < groupList.size()) {
            int itemPosition = getGroupItemPositionByIndex(groupPosition, groupItemIndex);
            if (itemPosition >= 0) {
                Group group = groupList.get(groupPosition);
                int itemCount = group.getItemCount();
                if (itemCount >= groupItemIndex + refreshCount) {
                    notifyItemRangeChanged(itemPosition, refreshCount);
                } else {
                    notifyItemRangeChanged(itemPosition, (itemCount - groupItemIndex));
                }
            }
        }
    }

    /**
     * 根据组坐标刷该组脚布局
     *
     * @param groupPosition 组坐标
     */
    public void notifyFooterChanged(int groupPosition) {
        int index = getGroupFooterPosition(groupPosition);
        if (index >= 0) {
            notifyItemChanged(index);
        }
    }

    /**
     * 删除所有数据
     * 注意: 仅刷新
     */
    public void notifyDataRemoved() {
        notifyItemRangeRemoved(0, getItemCount());
        groupList.clear();
    }

    /**
     * 根据组坐标删除该组数据(包含头、尾、子项)
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
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
     * 删除一定范围的组布局(包含头、尾、子项)
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
     * @param refreshCount  刷新个数
     */
    public void notifyGroupRemoved(int groupPosition, int refreshCount) {
        int index = getGroupHeaderPosition(groupPosition);
        int itemCount = 0;
        if (groupPosition + refreshCount <= groupList.size()) {
            itemCount = getRangeGroupItemCount(groupPosition, groupPosition + refreshCount);
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
     * 根据组坐标删除指定组头布局
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
     */
    public void notifyGroupHeaderRemoved(int groupPosition) {
        int headerPosition = getGroupHeaderPosition(groupPosition);
        if (headerPosition >= 0) {
            notifyItemRemoved(headerPosition);
            notifyItemRangeChanged(headerPosition, (getItemCount() - headerPosition));
            groupList.get(groupPosition).setHasHeader(false);
        }
    }

    /**
     * 根据组坐标删除指定子项组内坐标的子项
     * 注意: 仅刷新
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     */
    public void notifyItemRemove(int groupPosition, int groupItemIndex) {
        int itemPosition = getGroupItemPositionByIndex(groupPosition, groupItemIndex);
        if (itemPosition >= 0) {
            Group group = groupList.get(groupPosition);
            notifyItemRemoved(itemPosition);
            notifyItemRangeChanged(itemPosition, getItemCount() - itemPosition);
            group.setItemCount(group.getItemCount() - 1);
        }
    }


    /**
     * 根据组坐标删除指定组内一定范围的子项
     * 注意: 仅刷新
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     * @param removedCount       刷新个数
     */
    public void notifyItemRemove(int groupPosition, int groupItemIndex, int removedCount) {
        if (groupPosition < groupList.size()) {
            int itemPosition = getGroupItemPositionByIndex(groupPosition, groupItemIndex);
            if (itemPosition >= 0) {
                Group group = groupList.get(groupPosition);
                int itemCount = group.getItemCount();
                int removeCount = removedCount;
                if (itemCount < groupItemIndex + removedCount) {
                    removeCount = itemCount - groupItemIndex;
                }
                notifyItemRangeRemoved(itemPosition, removeCount);
                notifyItemRangeChanged(itemPosition, getItemCount() - removeCount);
                group.setItemCount(itemCount - removeCount);
            }
        }
    }

    /**
     * 根据组坐标删除指定组所有子项
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
     */
    public void notifyItemRemove(int groupPosition) {
        if (groupPosition < groupList.size()) {
            int itemPosition = getGroupItemPositionByIndex(groupPosition, 0);
            if (itemPosition >= 0) {
                Group group = groupList.get(groupPosition);
                int itemCount = group.getItemCount();
                notifyItemRangeRemoved(itemPosition, itemCount);
                notifyItemRangeChanged(itemPosition, getItemCount() - itemCount);
                group.setItemCount(0);
            }
        }
    }

    /**
     * 根据组坐标删除指定组脚布局
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
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
     * 根据指定组坐标插入指定组
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
     */
    public void notifyGroupInserted(int groupPosition) {
        Group group = new Group(hasHeader(groupPosition), hasFooter(groupPosition), getItemCount(groupPosition));
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
     * 根据指定组坐标插入多组
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
     * @param groupCount    组数
     */
    public void notifyGroupInserted(int groupPosition, int groupCount) {
        ArrayList<Group> list = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            Group group = new Group(hasHeader(i), hasFooter(i), getItemCount(i));
            list.add(group);
        }

        if (groupPosition < groupList.size()) {
            groupList.addAll(groupPosition, list);
        } else {
            groupList.addAll(list);
            groupPosition = groupList.size() - list.size();
        }

        int index = getRangeGroupItemCount(0, groupPosition);
        int itemCount = getRangeGroupItemCount(groupPosition, groupCount);
        if (itemCount > 0) {
            notifyItemRangeInserted(index, itemCount);
            notifyItemRangeChanged(index + itemCount, getItemCount() - index);
        }
    }

    /**
     * 根据指定组坐标插入头布局
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
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
     * 根据组坐标插入一组数据
     *
     * @param groupPosition 组坐标
     */
    public void notifyChildrenInserted(int groupPosition) {
        if (groupPosition < groupList.size()) {
            int index = getRangeGroupItemCount(0, groupPosition);
            Group group = groupList.get(groupPosition);
            if (group.hasHeader()) {
                index++;
            }
            int itemCount = getItemCount(groupPosition);
            if (itemCount > 0) {
                group.setItemCount(itemCount);
                notifyItemRangeInserted(index, itemCount);
                notifyItemRangeChanged(index + itemCount, getItemCount() - index);
            }
        }
    }

    /**
     * 根据组坐标(groupPosition)插入指定数据
     * 注意: 仅刷新
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     */
    public void notifyChildrenInserted(int groupPosition, int groupItemIndex) {
        if (groupPosition < groupList.size()) {
            Group group = groupList.get(groupPosition);
            int index = getGroupItemPositionByIndex(groupPosition, groupItemIndex);
            if (index < 0) {
                index = getRangeGroupItemCount(0, groupPosition);
                index += group.hasHeader() ? 1 : 0;
                index += group.getItemCount();
            }
            group.setItemCount(group.getItemCount() + 1);
            notifyItemInserted(index);
            notifyItemRangeChanged(index + 1, getItemCount() - index);
        }
    }

    /**
     * 根据组坐标(groupPosition)插入多个数据
     * 注意: 仅刷新
     *
     * @param groupPosition      组坐标
     * @param groupItemIndex 子项组内坐标
     * @param insertedCount      插入个数
     */
    public void notifyChildrenInserted(int groupPosition, int groupItemIndex, int insertedCount) {
        if (groupPosition < groupList.size()) {
            int index = getRangeGroupItemCount(0, groupPosition);
            Group group = groupList.get(groupPosition);
            if (group.hasHeader()) {
                index++;
            }
            if (groupItemIndex < group.getItemCount()) {
                index += groupItemIndex;
            } else {
                index += group.getItemCount();
            }
            if (insertedCount > 0) {
                group.setItemCount(group.getItemCount() + insertedCount);
                notifyItemRangeInserted(index, insertedCount);
                notifyItemRangeChanged(index + insertedCount, getItemCount() - index);
            }
        }
    }

    /**
     * 根据指定组坐标插入脚布局
     * 注意: 仅刷新
     *
     * @param groupPosition 组坐标
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
     * 所有点击事件的汇总
     */
    public void setOnMultiClickListener(OnMultiClickListener onMultiClickListener) {
        this.onMultiClickListener = onMultiClickListener;
    }

    /**
     * 设置组头点击事件
     */
    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        this.onHeaderClickListener = onHeaderClickListener;
    }

    /**
     * 设置子项点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置组尾点击事件
     */
    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener) {
        this.onFooterClickListener = onFooterClickListener;
    }

    public abstract List<T> getDatas();

    public abstract int getGroupCount();

    public abstract int getItemCount(int groupPosition);

    public abstract boolean hasHeader(int groupPosition);

    public abstract boolean hasFooter(int groupPosition);

    public abstract int getHeaderLayout(int viewType);

    public abstract int getFooterLayout(int viewType);

    public abstract int getItemLayout(int viewType);

    public abstract void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition);

    public abstract void onBindItemViewHolder(BaseViewHolder holder, int groupPosition, int groupItemPosition);

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
