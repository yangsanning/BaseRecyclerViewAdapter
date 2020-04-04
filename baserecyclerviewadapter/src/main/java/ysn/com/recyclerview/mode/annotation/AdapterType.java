package ysn.com.recyclerview.mode.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author yangsanning
 * @ClassName AdapterType
 * @Description Adapter类型
 * @Date 2019/6/18
 * @History 2019/6/18 author: description:
 */
@IntDef({AdapterType.HEADER, AdapterType.ITEM, AdapterType.FOOTER})
@Retention(RetentionPolicy.SOURCE)
public @interface AdapterType {

    /**
     * 头布局
     */
    int HEADER = 1;

    /**
     * 子项
     */
    int ITEM = 2;

    /**
     * 脚布局
     */
    int FOOTER = 3;
}
