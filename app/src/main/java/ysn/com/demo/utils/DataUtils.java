package ysn.com.demo.utils;

import java.util.ArrayList;

import ysn.com.demo.bean.ChildrenBean;
import ysn.com.demo.bean.ExpandableGroupBean;
import ysn.com.demo.bean.GroupBean;

/**
 * @Author yangsanning
 * @ClassName DataUtils
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class DataUtils {

    /**
     * 获取组列表数据
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     */
    public static ArrayList<GroupBean> getGroupList(int groupCount, int childrenCount) {
        ArrayList<GroupBean> groupList = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildrenBean> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                if (i == 1 && j == 2) {
                    break;
                }
                children.add(new ChildrenBean((i + 1) + "组 - " + "子项" + (j + 1)));
            }
            groupList.add(new GroupBean(((i + 1) + "组 - 头部"), ((i + 1) + "组 - 尾部"), children));
        }
        return groupList;
    }

    /**
     * 获取收起展开组列表数据(默认展开)
     *
     * @param groupCount    组数量
     * @param childrenCount 每个组里的子项数量
     */
    public static ArrayList<ExpandableGroupBean> getExpandableGroupList(int groupCount, int childrenCount) {
        ArrayList<ExpandableGroupBean> expandableGroupList = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<ChildrenBean> children = new ArrayList<>();
            for (int j = 0; j < childrenCount; j++) {
                children.add(new ChildrenBean((i + 1) + "组 - " + "子项" + (j + 1)));
            }
            expandableGroupList.add(new ExpandableGroupBean(((i + 1) + "组 - 头部"), ((i + 1) + "组 - 尾部"), children, true));
        }
        return expandableGroupList;
    }
}
