package ysn.com.baserecyclerviewadapter.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

import ysn.com.baserecyclerviewadapter.adapter.BaseRecyclerViewAdapter;
import ysn.com.baserecyclerviewadapter.holder.BaseViewHolder;
import ysn.com.baserecyclerviewadapter.utils.LayoutManagerUtils;

/**
 * @Author yangsanning
 * @ClassName StickyHeaderLayout
 * @Description 头部吸顶控件, 需结合BaseRecyclerViewAdapter使用，直接包裹RecyclerView即可
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class StickyHeaderLayout extends FrameLayout {

    private Context context;
    private RecyclerView recyclerView;

    /**
     * 吸顶容器
     */
    private FrameLayout stickyLayout;

    /**
     * 吸顶布局列表
     * SparseArray<E>去替换HashMap<Integer,E>更节省内存
     */
    private final SparseArray<BaseViewHolder> stickyViews = new SparseArray<>();

    /**
     * VIEW_TAG_TYPE: 吸顶布局中保存viewType的key
     * VIEW_TAG_HOLDER: 吸顶布局中保存ViewHolder的key
     */
    private final int VIEW_TAG_TYPE = -2020;
    private final int VIEW_TAG_HOLDER = -2010;

    /**
     * 当前吸顶的组坐标
     */
    private int stickyGroupPosition = -1;

    /**
     * 是否启用吸顶
     */
    private boolean isEnabledSticky = true;

    /**
     * 是否已经注册了adapter刷新监听
     */
    private boolean isRegisterDataObserver = false;

    public StickyHeaderLayout(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public StickyHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public StickyHeaderLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0 || !(child instanceof RecyclerView)) {
            throw new IllegalArgumentException("仅支持一个子类且仅支持RecyclerView");
        }
        super.addView(child, index, params);
        recyclerView = (RecyclerView) child;
        addOnScrollListener();
        addStickyLayout();
    }

    /**
     * 添加滚动监听, 用于刷新吸顶布局
     */
    private void addOnScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isEnabledSticky) {
                    updateStickyView(false);
                }
            }
        });
    }

    /**
     * 添加吸顶容器
     */
    private void addStickyLayout() {
        stickyLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        stickyLayout.setLayoutParams(params);
        super.addView(stickyLayout, 1, params);
    }

    /**
     * 刷新吸顶布局
     *
     * @param isForceUpdate 是否强制刷新。
     */
    private void updateStickyView(boolean isForceUpdate) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        // 仅支持BaseRecyclerViewAdapter
        if (adapter instanceof BaseRecyclerViewAdapter) {
            BaseRecyclerViewAdapter baseRecyclerViewAdapter = (BaseRecyclerViewAdapter) adapter;
            registerAdapterDataObserver(baseRecyclerViewAdapter);
            //获取列表显示的第一个项。
            int firstVisibleItem = LayoutManagerUtils.getFirstVisibleItem(recyclerView);
            // 获取坐标
            int groupPosition = baseRecyclerViewAdapter.getGroupPosition(firstVisibleItem);

            // 当吸顶布局不是要显示的组头时更新吸顶布局
            if (isForceUpdate || stickyGroupPosition != groupPosition) {
                stickyGroupPosition = groupPosition;

                // 获取组头坐标
                int groupHeaderPosition = baseRecyclerViewAdapter.getGroupHeaderPosition(groupPosition);
                if (groupHeaderPosition != -1) {
                    //获取吸顶布局的类型
                    int viewType = baseRecyclerViewAdapter.getItemViewType(groupHeaderPosition);

                    // 判断当前吸顶布局类型是否一样, 一样则返回吸顶布局的ViewHolder, 否则释放当前吸顶布局并返回null
                    BaseViewHolder holder = recycleStickyView(viewType);

                    // 标志holder是否是从当前吸顶布局取出来的
                    boolean flag = holder != null;

                    if (holder == null) {
                        // 从吸顶布局列表(stickyViews)中获取吸顶布局
                        holder = getStickyViewByType(viewType);
                    }

                    if (holder == null) {
                        // 如果列表中没有，则重新创建。
                        holder = (BaseViewHolder) baseRecyclerViewAdapter.onCreateViewHolder(stickyLayout, viewType);
                        holder.itemView.setTag(VIEW_TAG_TYPE, viewType);
                        holder.itemView.setTag(VIEW_TAG_HOLDER, holder);
                    }

                    // 通过BaseRecyclerViewAdapter更新吸顶布局,从而保证吸顶布局的显示效果跟列表中的组头保持一致
                    baseRecyclerViewAdapter.onBindViewHolder(holder, groupHeaderPosition);

                    // 如果holder不是从当前吸顶布局取出来的，就需要把吸顶布局添加到容器里
                    if (!flag) {
                        stickyLayout.addView(holder.itemView);
                    }
                } else {
                    // 如果当前组没有组头，则不显示吸顶布局且回收旧的吸顶布局
                    recycle();
                }
            }

            // 处理第一次打开时,吸顶布局已经添加到StickyLayout,但StickyLayout的高依然为0的情况。
            if (stickyLayout.getChildCount() > 0 && stickyLayout.getHeight() == 0) {
                stickyLayout.requestLayout();
            }

            //设置stickyLayout的Y偏移量。
            stickyLayout.setTranslationY(calculateOffset(baseRecyclerViewAdapter, firstVisibleItem, (groupPosition + 1)));
        }
    }

    /**
     * 注册adapter刷新监听
     */
    private void registerAdapterDataObserver(BaseRecyclerViewAdapter adapter) {
        if (!isRegisterDataObserver) {
            isRegisterDataObserver = true;
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    updateStickyViewDelayed();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    updateStickyViewDelayed();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    updateStickyViewDelayed();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    updateStickyViewDelayed();
                }

            });
        }
    }

    private void updateStickyViewDelayed() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                updateStickyView(true);
            }
        }, 100);
    }

    /**
     * 判断当前吸顶布局类型是否一样, 一样则返回吸顶布局的ViewHolder, 否则释放当前吸顶布局并返回null
     *
     * @param viewType 吸顶布局的viewType
     */
    private BaseViewHolder recycleStickyView(int viewType) {
        if (stickyLayout.getChildCount() > 0) {
            View view = stickyLayout.getChildAt(0);
            int type = (int) view.getTag(VIEW_TAG_TYPE);
            if (type == viewType) {
                return (BaseViewHolder) view.getTag(VIEW_TAG_HOLDER);
            } else {
                recycle();
            }
        }
        return null;
    }

    /**
     * 保存当前吸顶布局到吸顶布局列表(stickyViews), 并从吸顶容器中(stickyLayout)移除
     */
    private void recycle() {
        if (stickyLayout.getChildCount() > 0) {
            View view = stickyLayout.getChildAt(0);
            stickyViews.put((int) (view.getTag(VIEW_TAG_TYPE)), (BaseViewHolder) (view.getTag(VIEW_TAG_HOLDER)));
            stickyLayout.removeAllViews();
        }
    }

    /**
     * 从吸顶布局列表(stickyViews)中获取吸顶布局
     *
     * @param viewType 吸顶布局的viewType
     */
    private BaseViewHolder getStickyViewByType(int viewType) {
        return stickyViews.get(viewType);
    }

    /**
     * 计算StickyLayout的偏移量。因为如果下一个组的组头顶到了StickyLayout，
     * 就要把StickyLayout顶上去，直到下一个组的组头变成吸顶布局。否则会发生两个组头重叠的情况。
     *
     * @param firstVisibleItem  当前列表显示的第一个项。
     * @param nextGroupPosition 下一个组的组下标。
     * @return 返回偏移量
     */
    private float calculateOffset(BaseRecyclerViewAdapter baseRecyclerViewAdapter, int firstVisibleItem, int nextGroupPosition) {
        int groupHeaderPosition = baseRecyclerViewAdapter.getGroupHeaderPosition(nextGroupPosition);
        if (groupHeaderPosition != -1) {
            int index = groupHeaderPosition - firstVisibleItem;
            if (recyclerView.getChildCount() > index) {
                //获取下一个组的组头的itemView。
                View view = recyclerView.getChildAt(index);
                float off = view.getY() - stickyLayout.getHeight();
                if (off < 0) {
                    return off;
                }
            }
        }
        return 0;
    }

    /**
     * 是否启用吸顶
     */
    public boolean isEnabledSticky() {
        return isEnabledSticky;
    }

    /**
     * 是否启用吸顶
     */
    public void setEnabledSticky(boolean enabledSticky) {
        if (isEnabledSticky != enabledSticky) {
            isEnabledSticky = enabledSticky;
            if (stickyLayout != null) {
                if (isEnabledSticky) {
                    stickyLayout.setVisibility(VISIBLE);
                    updateStickyView(false);
                } else {
                    recycle();
                    stickyLayout.setVisibility(GONE);
                }
            }
        }
    }

    @Override
    protected int computeVerticalScrollOffset() {
        if (recyclerView != null) {
            try {
                Method method = View.class.getDeclaredMethod("computeVerticalScrollOffset");
                method.setAccessible(true);
                return (int) method.invoke(recyclerView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.computeVerticalScrollOffset();
    }


    @Override
    protected int computeVerticalScrollRange() {
        if (recyclerView != null) {
            try {
                Method method = View.class.getDeclaredMethod("computeVerticalScrollRange");
                method.setAccessible(true);
                return (int) method.invoke(recyclerView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.computeVerticalScrollRange();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        if (recyclerView != null) {
            try {
                Method method = View.class.getDeclaredMethod("computeVerticalScrollExtent");
                method.setAccessible(true);
                return (int) method.invoke(recyclerView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.computeVerticalScrollExtent();
    }

    @Override
    public void scrollBy(int x, int y) {
        if (recyclerView != null) {
            recyclerView.scrollBy(x, y);
        } else {
            super.scrollBy(x, y);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (recyclerView != null) {
            recyclerView.scrollTo(x, y);
        } else {
            super.scrollTo(x, y);
        }
    }
}
