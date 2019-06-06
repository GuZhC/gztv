package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 包装数据类型
 * <p>
 * 主要用在同种数据结构表示不同意义
 *
 * @param <T> 被包装的数据
 */
public class BaseWrapContent<T> implements Serializable {

    protected T content;

    public BaseWrapContent() {

    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public interface ICreateWrapContent<F extends BaseWrapContent> {
        F createNewsInstance();
    }

    /**
     * 对象转换成对象
     *
     * @param data
     * @param creator
     * @param <D>
     * @return
     */
    public static <D extends BaseWrapContent<T>, T> D toWrapContent(T data, ICreateWrapContent<D> creator) {
        D wrapContent = null;
        if (creator != null) {
            wrapContent = creator.createNewsInstance();
            wrapContent.setContent(data);
        } else {
            try {
                wrapContent = (D) new BaseWrapContent<T>();
                wrapContent.setContent(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wrapContent;
    }

    /**
     * 对应的列表转换
     *
     * @param list
     * @param creator
     * @param <E>
     * @return
     */
    public static <D extends BaseWrapContent<E>, E> List<D> toWrapContentList(List<E> list, ICreateWrapContent<D> creator) {
        List<D> wrapContentList = null;
        if (list != null) {
            wrapContentList = new ArrayList<>();
            for (E item : list) {
                wrapContentList.add(toWrapContent(item, creator));
            }
        }
        return wrapContentList;
    }
}
