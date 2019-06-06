package com.dfsx.core.file.upload;

import java.util.ArrayList;

public interface IUpload {

    boolean upload(PublishData tagData,ArrayList<UploadFileData> uploadList);
}
