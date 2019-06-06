package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

public class DayTask implements ITaskData {

    private long id;
    private String source;
    private String type;
    private String name;
    @SerializedName("bg_img_url")
    private String taskBkgUrl;
    @SerializedName("max_count")
    private int maxCount;
    @SerializedName("done_count")
    private int doneCount;
    @SerializedName("competed_time")
    private long competedTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskBkgUrl() {
        return taskBkgUrl;
    }

    public void setTaskBkgUrl(String taskBkgUrl) {
        this.taskBkgUrl = taskBkgUrl;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public void setDoneCount(int doneCount) {
        this.doneCount = doneCount;
    }

    public long getCompetedTime() {
        return competedTime;
    }

    public void setCompetedTime(long competedTime) {
        this.competedTime = competedTime;
    }

    @Override
    public long getTaskId() {
        return id;
    }

    @Override
    public String getTaskName() {
        return name;
    }

    @Override
    public String getTaskFinishProgressText() {
        return doneCount + "/" + maxCount;
    }

    @Override
    public double getTaskWage() {
        return 0;
    }

    @Override
    public boolean isTaskFinish() {
        return doneCount == maxCount;
    }

    @Override
    public String getTaskBkgImagePath() {
        return taskBkgUrl;
    }

    @Override
    public int getTaskBkgImageRes() {
        return 0;
    }
}
