package com.dfsx.ganzcms.app.model;

import com.dfsx.core.file.upload.UploadFileData;

import java.util.ArrayList;

public class WordData {

    /**
     * 没有错误
     */
    public static final int ERROR_NONE = 0;
    /**
     * 上传文件错误
     */
    public static final int ERROR_UPLOAD_FILE = 1;
    /**
     * 设置VMS 的视频文件错误
     */
    public static final int ERROR_POST_VMS_VIDEO = 2;
    /**
     * 添加内容错误
     */
    public static final int ERROR_POST_WORD_DATA = 3;

    private String title;
    private String content;

    /**
     * 设置上传文件之后的content
     */
    private String textAndFileContent;
    private ArrayList<UploadFileData> uploadFileDataList;
    private ArrayList<Long> columnIdList;

    /**
     * 错误进度类型
     */
    private int errorProgressType = ERROR_NONE;

    private boolean postSuccess;

    public WordData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public WordData(String title, String content, ArrayList<Long> columnList) {
        this.title = title;
        this.content = content;
        this.columnIdList = columnList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<UploadFileData> getUploadFileDataList() {
        return uploadFileDataList;
    }

    public void setUploadFileDataList(ArrayList<UploadFileData> uploadFileDataList) {
        this.uploadFileDataList = uploadFileDataList;
    }

    public boolean isPostSuccess() {
        return postSuccess;
    }

    public void setPostSuccess(boolean postSuccess) {
        this.postSuccess = postSuccess;
    }

    public ArrayList<Long> getColumnIdList() {
        return columnIdList;
    }

    public void setColumnIdList(ArrayList<Long> columnIdList) {
        this.columnIdList = columnIdList;
    }

    public int getErrorProgressType() {
        return errorProgressType;
    }

    public void setErrorProgressType(int errorProgressType) {
        this.errorProgressType = errorProgressType;
    }

    public String getTextAndFileContent() {
        return textAndFileContent;
    }

    public void setTextAndFileContent(String textAndFileContent) {
        this.textAndFileContent = textAndFileContent;
    }
}
