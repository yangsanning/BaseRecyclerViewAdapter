package ysn.com.demo.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import ysn.com.demo.R;
import ysn.com.demo.adapter.GroupedListAdapter;
import ysn.com.demo.utils.DataUtils;
import ysn.com.recyclerview.adapter.BaseRecyclerViewAdapter;
import ysn.com.recyclerview.holder.BaseViewHolder;

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
            public void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
                showMsg(groupedListAdapter.getDatas().get(groupPosition).getHeader());
            }
        });

        groupedListAdapter.setOnChildrenClickListener(new BaseRecyclerViewAdapter.OnChildrenClickListener() {
            @Override
            public void onChildrenClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder,
                                        int groupPosition, int childPosition) {
                showMsg(groupedListAdapter.getDatas().get(groupPosition).getChildrenList().get(childPosition).getChild());
            }
        });

        groupedListAdapter.setOnFooterClickListener(new BaseRecyclerViewAdapter.OnFooterClickListener() {
            @Override
            public void onFooterClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
                showMsg(groupedListAdapter.getDatas().get(groupPosition).getFooter());
            }
        });

        recyclerView.setAdapter(groupedListAdapter);
    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
