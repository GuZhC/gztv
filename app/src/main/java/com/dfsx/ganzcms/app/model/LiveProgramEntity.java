package com.dfsx.ganzcms.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by heyang on 2017/1/5  内容发布  直播节目单
 */
public class LiveProgramEntity implements Serializable {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String name;

    public List<LiveProgram> getLivePrograms() {
        return livePrograms;
    }

    public void setLivePrograms(List<LiveProgram> livePrograms) {
        this.livePrograms = livePrograms;
    }

    List<LiveProgram> livePrograms;


    static public class LiveProgram implements Serializable {

        /**
         * name :
         * description :
         * start_time : 0
         * stop_time : 0
         */

        private String name;
        private String description;
        private long start_time;
        private long stop_time;

        public String getM3u8_url() {
            return m3u8_url;
        }

        public void setM3u8_url(String m3u8_url) {
            this.m3u8_url = m3u8_url;
        }

        private String m3u8_url;

        public long getVideo_id() {
            return video_id;
        }

        public void setVideo_id(long video_id) {
            this.video_id = video_id;
        }

        private long  video_id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public long getStart_time() {
            return start_time;
        }

        public void setStart_time(long start_time) {
            this.start_time = start_time;
        }

        public long getStop_time() {
            return stop_time;
        }

        public void setStop_time(long stop_time) {
            this.stop_time = stop_time;
        }
    }
}

