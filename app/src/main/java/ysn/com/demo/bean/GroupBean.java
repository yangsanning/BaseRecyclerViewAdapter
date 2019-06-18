package ysn.com.demo.bean;

import java.util.ArrayList;

/**
 * @Author yangsanning
 * @ClassName GroupBean
 * @Description 一句话概括作用
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class GroupBean {

    private String header;
    private String footer;
    private ArrayList<ChildrenBean> childrenList;

    public GroupBean(String header, String footer, ArrayList<ChildrenBean> childrenList) {
        this.header = header;
        this.footer = footer;
        this.childrenList = childrenList;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public ArrayList<ChildrenBean> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(ArrayList<ChildrenBean> childrenList) {
        this.childrenList = childrenList;
    }
}
