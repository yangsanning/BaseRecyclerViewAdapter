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
import ysn.com.demo.adapter.ExpandableAdapter;
import ysn.com.demo.utils.DataUtils;

/**
 * @Author yangsanning
 * @ClassName ExpandableListActivity
 * @Description 一句话概括作用
 * @Date 2019/6/18
 * @History 2019/6/18 author: description:
 */
public class ExpandableListActivity extends AppCompatActivity {

    private ExpandableAdapter expandableAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list);
        setTitle(R.string.main_activity_expandable_list);

        RecyclerView recyclerView = findViewById(R.id.expandable_list_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expandableAdapter = new ExpandableAdapter(this,
                DataUtils.getExpandableGroupList(8, 3));

        expandableAdapter.setOnHeaderClickListener(new BaseRecyclerViewAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
                ExpandableAdapter expandableAdapter = (ExpandableAdapter) adapter;
                if (expandableAdapter.isExpand(groupPosition)) {
                    expandableAdapter.collapseGroup(groupPosition);
                } else {
                    expandableAdapter.expandGroup(groupPosition);
                }
            }
        });

        expandableAdapter.setOnChildClickListener(new BaseRecyclerViewAdapter.OnChildClickListener() {
            @Override
            public void onChildClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder,
                                     int groupPosition, int childPosition) {
                Toast.makeText(ExpandableListActivity.this,
                        expandableAdapter.getDatas().get(groupPosition).getChildrenList().get(childPosition).getChild(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableAdapter.setOnFooterClickListener(new BaseRecyclerViewAdapter.OnFooterClickListener() {
            @Override
            public void onFooterClick(BaseRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
                Toast.makeText(ExpandableListActivity.this,
                        expandableAdapter.getDatas().get(groupPosition).getFooter(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(expandableAdapter);
    }
}