package com.dfsx.ganzcms.app.business;

import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.ganzcms.app.model.ContentComment;

import java.util.List;

public interface ICMSContentComment {

    void getCommentList(long contentId, int page, int pageSize,
                        DataRequest.DataCallback<List<ContentComment>> callback);

    void getRootCommentList(long contentId, int page, int pageSize,
                            int sub_comment_count, DataRequest.DataCallback<List<ContentComment>> callback);


    void addContentComment(long contentId, String text, long ref_commentId, DataRequest.DataCallback<Long> callback);
}
