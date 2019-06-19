# BaseRecyclerViewAdapter
[![](https://jitpack.io/v/yangsanning/BaseRecyclerViewAdapter.svg)](https://jitpack.io/#yangsanning/BaseRecyclerViewAdapter)


## 效果预览

| 吸顶列表                      | 收起展开                     | 
| ------------------------------- | ------------------------------- |
| <img src="images/image1.gif" height="512" /> | <img src="images/image2.gif" height="512"/> | <height="512"/> |


## 主要文件
| 名字             | 摘要           |
| ---------------- | -------------- |
|BaseRecyclerViewAdapter | 基础Adapter |
|StickyHeaderLayout | 头部吸顶控件  |

### 1. 基本用法

#### 1.1 吸顶列表
StickyHeaderLayout需结合BaseRecyclerViewAdapter使用，直接包裹RecyclerView即可
```android
<ysn.com.baserecyclerviewadapter.view.StickyHeaderLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/sticky_list_activity_recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</ysn.com.baserecyclerviewadapter.view.StickyHeaderLayout>
```

#### 1.2 其他使用
```android
  直接继承BaseRecyclerViewAdapter
```

### 2.添加方法

#### 2.1 添加仓库

在项目的 `build.gradle` 文件中配置仓库地址。

```android
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

#### 2.2 添加项目依赖

在需要添加依赖的 Module 下添加以下信息，使用方式和普通的远程仓库一样。

```android
implementation 'com.github.yangsanning:BaseRecyclerViewAdapter:1.0.0'
```
