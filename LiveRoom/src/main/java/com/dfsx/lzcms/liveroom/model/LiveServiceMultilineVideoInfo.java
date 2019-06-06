package com.dfsx.lzcms.liveroom.model;

import com.dfsx.lzcms.liveroom.view.IMultilineVideo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于活动直播回顾
 * 活动直播的多线路信息
 * Created by liuwb on 2017/7/6.
 */
public class LiveServiceMultilineVideoInfo implements Serializable, IMultilineVideo {

    /**
     * files : [{"start_time":1499250755,"file_id":13222640,"stop_time":1499251105,"file_url":"http://192.168.6.32:8101/live/videos/live117/1065536/104751/hls/playlist.m3u8","file_extra_urls":{"custom":"http://192.168.6.32:8101/live/videos/live117/1065536/104751/flv/1.flv"}}]
     * name : 顶棚
     * id : 104751
     */

    private String name;
    private long id;
    private List<VideoFileInfo> files;
    private boolean isSelected;

    public String getName() {
        return name;
    }

    @Override
    public String getLineTitle() {
        return "线路";
    }

    @Override
    public List<String> getVideoUrlList() {
        if (files != null && !files.isEmpty()) {
            ArrayList<String> list = new ArrayList<>();
            for (VideoFileInfo fileInfo : files) {
                list.add(fileInfo.getFile_url());
            }
            return list;
        }
        return null;
    }

    @Override
    public List<Long> getVideoDurationList() {
        if (files != null && !files.isEmpty()) {
            ArrayList<Long> list = new ArrayList<>();
            for (VideoFileInfo fileInfo : files) {
                long time = fileInfo.getStop_time() - fileInfo.getStart_time();
                if (time < 0) {
                    time = 0;
                }
                list.add(time * 1000);
            }
            return list;
        }
        return null;
    }

    @Override
    public void setVideoUrlList(List<String> urlList) {
        if (urlList != null && !urlList.isEmpty()) {
            files = new ArrayList<>();
            for (String url : urlList) {
                VideoFileInfo fileInfo = new VideoFileInfo();
                fileInfo.setFile_url(url);
                files.add(fileInfo);
            }
        }
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<VideoFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<VideoFileInfo> files) {
        this.files = files;
    }

    public static class VideoFileInfo implements Serializable {
        /**
         * start_time : 1499250755
         * file_id : 13222640
         * stop_time : 1499251105
         * file_url : http://192.168.6.32:8101/live/videos/live117/1065536/104751/hls/playlist.m3u8
         * file_extra_urls : {"custom":"http://192.168.6.32:8101/live/videos/live117/1065536/104751/flv/1.flv"}
         */

        private long start_time;
        private long file_id;
        private long stop_time;
        private String file_url;
        private FileExtraUrlsData file_extra_urls;

        public long getStart_time() {
            return start_time;
        }

        public void setStart_time(long start_time) {
            this.start_time = start_time;
        }

        public long getFile_id() {
            return file_id;
        }

        public void setFile_id(long file_id) {
            this.file_id = file_id;
        }

        public long getStop_time() {
            return stop_time;
        }

        public void setStop_time(long stop_time) {
            this.stop_time = stop_time;
        }

        public String getFile_url() {
            return file_url;
        }

        public void setFile_url(String file_url) {
            this.file_url = file_url;
        }

        public FileExtraUrlsData getFile_extra_urls() {
            return file_extra_urls;
        }

        public void setFile_extra_urls(FileExtraUrlsData file_extra_urls) {
            this.file_extra_urls = file_extra_urls;
        }

        public static class FileExtraUrlsData implements Serializable {
            /**
             * custom : http://192.168.6.32:8101/live/videos/live117/1065536/104751/flv/1.flv
             */

            private String custom;

            public String getCustom() {
                return custom;
            }

            public void setCustom(String custom) {
                this.custom = custom;
            }
        }
    }
}
