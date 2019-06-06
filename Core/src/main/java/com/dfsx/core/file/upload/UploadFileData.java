package com.dfsx.core.file.upload;

import java.io.File;

public class UploadFileData {

    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_IMAGE = "image";

    private File file;
    private String uploadServiceUrl;
    private String fileType;

    private UploadResult result;

    public UploadFileData() {
    }

    public UploadFileData(File file, String uploadUrl) {
        this.file = file;
        this.uploadServiceUrl = uploadUrl;
    }

    public static UploadFileData imageUploadFile(String uploadUrl, File imageFile) {
        UploadFileData fileData = new UploadFileData(imageFile, uploadUrl);
        fileData.setFileType(TYPE_IMAGE);
        return fileData;
    }

    public static UploadFileData videoUploadFile(String uploadUrl, File videoFile) {
        UploadFileData fileData = new UploadFileData(videoFile, uploadUrl);
        fileData.setFileType(TYPE_VIDEO);
        return fileData;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getUploadServiceUrl() {
        return uploadServiceUrl;
    }

    public void setUploadServiceUrl(String uploadServiceUrl) {
        this.uploadServiceUrl = uploadServiceUrl;
    }

    public void setSuccessResult(String servicePath) {
        result = new UploadResult(true, servicePath);
    }

    public void setFailResult() {
        result = new UploadResult(false, null);
    }

    public UploadResult getResult() {
        return result;
    }

    public void setResult(UploadResult result) {
        this.result = result;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public static class UploadResult {
        private boolean isSuccess;
        private String servicePath;
        private String serverName;

        public UploadResult() {

        }


        public UploadResult(boolean isOk, String servicePath) {
            this.isSuccess = isOk;
            this.servicePath = servicePath;
        }

        public UploadResult(boolean isOk, String servicePath,String  serverName) {
            this.isSuccess = isOk;
            this.servicePath = servicePath;
            this.serverName=serverName;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }

        public String getServicePath() {
            return servicePath;
        }

        public void setServicePath(String servicePath) {
            this.servicePath = servicePath;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }
    }
}
