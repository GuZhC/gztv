package com.dfsx.core.file.upload;

import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.upload.UploadFileData;

import java.util.ArrayList;

public class PublishData<D> {
    /**
     * 没有错误
     */
    public static final int ERROR_NONE = 0;
    /**
     * 上传文件错误
     */
    public static final int ERROR_UPLOAD_FILE = 1;
    /**
     * 添加内容错误
     */
    public static final int ERROR_PUBLISH_DATA = 3;

    /**
     * 待发布的文件
     */
    private ArrayList<UploadFileData> uploadFileDataList;

    /**
     * 错误进度类型
     */
    private int errorProgressType = ERROR_NONE;


    /**
     * 错误消息描述
     */
    private String errorMessage ="";


    /**
     * 发布结果
     */
    private boolean postSuccess;

    /**
     * 发布的实体类型
     */
    private D data;

    /**
     * 发布的动作。 通常是网络请求调用发布接口
     */
    private OnPublishDataAction<D> pushAction;

    public PublishData() {

    }

    public ArrayList<UploadFileData> getUploadFileDataList() {
        return uploadFileDataList;
    }

    public void setUploadFileDataList(ArrayList<UploadFileData> uploadFileDataList) {
        this.uploadFileDataList = uploadFileDataList;
    }

    public int getErrorProgressType() {
        return errorProgressType;
    }

    public void setErrorProgressType(int errorProgressType) {
        this.errorProgressType = errorProgressType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isPostSuccess() {
        return postSuccess;
    }

    public void setPostSuccess(boolean postSuccess) {
        this.postSuccess = postSuccess;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public OnPublishDataAction<D> getPushAction() {
        return pushAction;
    }

    public void setPushAction(OnPublishDataAction<D> pushAction) {
        this.pushAction = pushAction;
    }

    /**
     * 具体实现发布动作
     */
    public interface OnPublishDataAction<D> {
        /**
         * 在文件上传成功知乎之后调用。
         * 且运行在在子线程中
         * <p>
         * 这里实现上传接口调用。并同步返回上传结果
         *
         * @param uploadFileResultList
         * @param data
         * @return
         */
        boolean onPublishData(ArrayList<UploadFileData> uploadFileResultList, D data)  throws ApiException;
    }
}
