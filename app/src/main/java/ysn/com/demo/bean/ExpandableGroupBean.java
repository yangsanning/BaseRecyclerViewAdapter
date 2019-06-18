package ysn.com.demo.bean;

import java.util.ArrayList;

/**
 * @Author yangsanning
 * @ClassName ExpandableGroupBean
 * @Description 收起展开组
 * @Date 2019/6/18
 * @History 2019/6/18 author: description:
 */
public class ExpandableGroupBean extends GroupBean {

    private boolean isExpand;

    public ExpandableGroupBean(String header, String footer, ArrayList<ChildrenBean> childrenList, boolean isExpand) {
        super(header, footer, childrenList);
        this.isExpand = isExpand;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }
}
