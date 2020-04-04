package ysn.com.recyclerview.mode.bean;

/**
 * @Author yangsanning
 * @ClassName Group
 * @Description 记录头部、尾部、子项个数
 * @Date 2019/6/17
 * @History 2019/6/17 author: description:
 */
public class Group {

    private boolean hasHeader;
    private boolean hasFooter;
    private int childrenCount;

    public Group(boolean hasHeader, boolean hasFooter, int childrenCount) {
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
        this.childrenCount = childrenCount;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public int getItemCount() {
        int itemCount = childrenCount;
        if (hasHeader()) {
            itemCount += 1;
        }
        if (hasFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }
}