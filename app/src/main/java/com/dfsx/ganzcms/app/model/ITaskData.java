package com.dfsx.ganzcms.app.model;

/**
 * Created by liuwb on 2017/7/20.
 */
public interface ITaskData {
    long getTaskId();

    String getTaskName();

    /**
     * 任务的完成度
     *
     * @return
     */
    String getTaskFinishProgressText();

    /**
     * 任务报酬
     *
     * @return
     */
    double getTaskWage();

    /**
     * 是否完成
     *
     * @return
     */
    boolean isTaskFinish();

    /**
     * 任务背景图片路径
     *
     * @return
     */
    String getTaskBkgImagePath();

    /**
     * 任务背景图片res
     *
     * @return
     */
    int getTaskBkgImageRes();
}
