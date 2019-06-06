package com.dfsx.ganzcms.app.model;

import com.dfsx.ganzcms.app.adapter.FileUploadAdapter;

public class UploadFile implements IUploadFile {

    public static final String FILE_VIDEO = "_video";
    public static final String FILE_IMAGE = "_image";

    private String filePath;
    private String fileThumbPath;
    private String fileType;
    private int showType;
    private String showTypeText;


    public UploadFile() {

    }

    public UploadFile(String filePath, String fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
        this.showType = FileUploadAdapter.TYPE_GRID;
    }

    public UploadFile(int showType, String showTypeText) {
        this.showType = showType;
        this.showTypeText = showTypeText;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileThumbPath() {
        return fileThumbPath;
    }

    public void setFileThumbPath(String fileThumbPath) {
        this.fileThumbPath = fileThumbPath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public void setShowTypeText(String showTypeText) {
        this.showTypeText = showTypeText;
    }

    @Override
    public String getFileUrl() {
        return filePath;
    }

    @Override
    public String getFileThumbImage() {
        return fileThumbPath;
    }

    @Override
    public String getFileType() {
        return fileType;
    }

    @Override
    public int getShowType() {
        return showType;
    }

    @Override
    public String getShowTypeText() {
        return showTypeText;
    }
}