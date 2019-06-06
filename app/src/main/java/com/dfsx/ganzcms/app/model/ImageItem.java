package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
//import com.dfsx.wscms.util.upfile.Bimp;


public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
//	private Bitmap bitmap;
	public boolean isSelected = false;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
//	public Bitmap getBitmap(Context contxt) {
//		if(bitmap == null){
//			try {
//				bitmap = Bimp.revitionImageSize(imagePath,contxt);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return bitmap;
//	}
//	public void setBitmap(Bitmap bitmap) {
//		this.bitmap = bitmap;
//	}
	
	
	
}
