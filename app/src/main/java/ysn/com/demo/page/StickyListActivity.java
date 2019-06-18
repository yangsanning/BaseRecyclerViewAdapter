package ysn.com.demo.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import ysn.com.baserecyclerviewadapter.adapter.BaseRecyclerViewAdapter;
import ysn.com.baserecyclerviewadapter.holder.BaseViewHolder;
import ysn.com.demo.R;
import ysn.com.demo.adapter.GroupedListAdapter;
import ysn.com.demo.utils.DataUtils;

/**
 * @Author yangsanning
 * @ClassName StickyListActivity
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class StickyListActivity extends AppCompatActivity {

    private GroupedListAdapter groupedListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_list);
        setTitle(R.string.main_activity_sticky_list);

        RecyclerView recyclerView = findViewById(R.id.sticky_list_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupedListAdapter = new GroupedListAdapter(this, DataUtils.getGroupList(8, 3));

        groupedListAdapter.setOnHeaderClickListener(new BaseRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder,
                                      int groupPosition) {
                Toast.makeText(StickyListActivity.this,
                        groupedListAdapter.getDatas().get(groupPosition).getHeader(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        groupedListAdapter.setOnChildClickListener(new BaseRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                Toast.makeText(StickyListActivity.this,
                        groupedListAdapter.getDatas().get(groupPosition).getChildrenList().get(childPosition).getChild(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        groupedListAdapter.setOnFooterClickListener(new BaseRecyclerViewAdapter.OnFooterClickListener() {
            @Override
            public void onFooterClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
                Toast.makeText(StickyListActivity.this,
                        groupedListAdapter.getDatas().get(groupPosition).getFooter(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(groupedListAdapter);
    }
}
