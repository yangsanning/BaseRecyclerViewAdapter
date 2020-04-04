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
    private int itemCount;

    public Group(boolean hasHeader, boolean hasFooter, int itemCount) {
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
        this.itemCount = itemCount;
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

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getRealItemCount() {
        int itemCount = this.itemCount;
        if (hasHeader()) {
            itemCount += 1;
        }
        if (hasFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }
}