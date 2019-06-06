package com.dfsx.ganzcms.app.business;

public abstract class AbsCMSVideoReplace implements StringReplaceHelper.ReplaceString {

    public static String REGEX = "<\\!--VIDEO#(\\d*),(\\d*),(\\d*)-->";

    @Override
    public String replace(String... replaceString) {
        if (replaceString != null && replaceString.length >= 2) {
            try {
                long id = Long.valueOf(replaceString[1]);
                WebVideoData videoData = getVideoPathById(id);
                if (videoData != null && !videoData.getVideoUrl().isEmpty()) {
                    String newLine = "";
                    newLine += "<div style=\"position:relative;overflow: hidden;\"><div><video  poster=\"" + videoData.getVideoCoverPath() + "\" />";
                    newLine += "<source src=\"" + videoData.getVideoUrl() + "\" type=\"video/mp4\" >";
                    newLine += "您的浏览器不支持HTML5视频";
                    newLine += "</video></div>";
                    newLine += "<span class=\"play-video-btn\"></span></div>";

                    return newLine;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public abstract WebVideoData getVideoPathById(long id);


    public class WebVideoData {
        private String videoUrl;

        private String videoCoverPath;

        public WebVideoData() {

        }

        public WebVideoData(String url, String coverPath) {
            this.videoUrl = url;
            this.videoCoverPath = coverPath;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getVideoCoverPath() {
            return videoCoverPath;
        }

        public void setVideoCoverPath(String videoCoverPath) {
            this.videoCoverPath = videoCoverPath;
        }
    }
}
