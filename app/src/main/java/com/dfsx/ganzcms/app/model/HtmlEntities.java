package com.dfsx.ganzcms.app.model;

import java.io.Serializable;

/**
 * @author   heyang  2016/5/18
 */

public class HtmlEntities implements Serializable{

	private String  columnId;
	private String  title;

	public boolean isFvel() {
		return isFvel;
	}

	public void setIsFvel(boolean isFvel) {
		this.isFvel = isFvel;
	}

	private  boolean isFvel;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAver_url() {
		return aver_url;
	}

	public void setAver_url(String aver_url) {
		this.aver_url = aver_url;
	}


	private String  username;
	private String  aver_url;
	private String  soruceTx;
	private String  videoUrl;
	private String  videoThumb;
	private String filename;
	private String htmlContent;

	public long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(long replyCount) {
		this.replyCount = replyCount;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	private long replyCount;
	private long viewCount;

//	private String content;
//	private  GetFile_html_content(){}
//	private static GetFile_html_content Name_Content;
//
//	public static GetFile_html_content getName_Content() {
//		if(Name_Content==null){
//			Name_Content=new GetFile_html_content();
//		}
//		return Name_Content;
//	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
//	public String getContent() {
//		return content;
//	}
//	public void setContent(String content) {
//		this.content = content;
//	}
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getVideoThumb() {
		return videoThumb;
	}

	public void setVideoThumb(String videoThumb) {
		this.videoThumb = videoThumb;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getSoruceTx() {
		return soruceTx;
	}

	public void setSoruceTx(String soruceTx) {
		this.soruceTx = soruceTx;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
}
