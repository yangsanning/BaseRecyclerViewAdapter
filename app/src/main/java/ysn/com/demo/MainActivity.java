package ysn.com.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ysn.com.demo.page.ExpandableListActivity;
import ysn.com.demo.page.StickyListActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_activity_sticky_list).setOnClickListener(this);
        findViewById(R.id.main_activity_expandable_list).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_sticky_list:
                startActivity(StickyListActivity.class);
                break;
            case R.id.main_activity_expandable_list:
                startActivity(ExpandableListActivity.class);
                break;
            default:
                break;
        }
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }
}
