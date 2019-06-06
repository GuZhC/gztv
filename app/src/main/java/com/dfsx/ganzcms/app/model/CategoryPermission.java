package com.dfsx.ganzcms.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 分类权限
 * Created by liuwb on 2016/10/25.
 */
public class CategoryPermission implements Serializable {

    /**
     * view : false
     * create_channel : false
     * create_playback : false
     * moderate_room : false
     * participate_room : false
     * visit_room : false
     */

    @SerializedName("visit_category")
    private VisitCategoryPermission visitCategory;
    /**
     * terminate_room : false
     * close_channel : false
     * close_playback : false
     */

    @SerializedName("admin_category")
    private AdminCategoryPermission adminRategory;

    public VisitCategoryPermission getVisitCategory() {
        return visitCategory;
    }

    public void setVisitCategory(VisitCategoryPermission visitCategory) {
        this.visitCategory = visitCategory;
    }

    public AdminCategoryPermission getAdminRategory() {
        return adminRategory;
    }

    public void setAdminRategory(AdminCategoryPermission adminRategory) {
        this.adminRategory = adminRategory;
    }

    public static class VisitCategoryPermission implements Serializable {
        @SerializedName("category_id")
        private long categoryId;
        private boolean view;
        @SerializedName("create_channel")
        private boolean createChannel;
        @SerializedName("create_playback")
        private boolean createPlayback;
        @SerializedName("moderate_room")
        private boolean moderateRoom;
        @SerializedName("participate_room")
        private boolean participateRoom;
        @SerializedName("visit_room")
        private boolean visitRoom;

        public boolean isView() {
            return view;
        }

        public void setView(boolean view) {
            this.view = view;
        }

        public boolean isCreateChannel() {
            return createChannel;
        }

        public void setCreateChannel(boolean createChannel) {
            this.createChannel = createChannel;
        }

        public boolean isCreatePlayback() {
            return createPlayback;
        }

        public void setCreatePlayback(boolean createPlayback) {
            this.createPlayback = createPlayback;
        }

        public boolean isModerateRoom() {
            return moderateRoom;
        }

        public void setModerateRoom(boolean moderateRoom) {
            this.moderateRoom = moderateRoom;
        }

        public boolean isParticipateRoom() {
            return participateRoom;
        }

        public void setParticipateRoom(boolean participateRoom) {
            this.participateRoom = participateRoom;
        }

        public boolean isVisitRoom() {
            return visitRoom;
        }

        public void setVisitRoom(boolean visitRoom) {
            this.visitRoom = visitRoom;
        }

        public long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(long categoryId) {
            this.categoryId = categoryId;
        }
    }

    public static class AdminCategoryPermission implements Serializable {
        @SerializedName("terminate_room")
        private boolean terminateRoom;
        @SerializedName("close_channel")
        private boolean closeChannel;
        @SerializedName("close_playback")
        private boolean closePlayback;

        public boolean isTerminateRoom() {
            return terminateRoom;
        }

        public void setTerminateRoom(boolean terminateRoom) {
            this.terminateRoom = terminateRoom;
        }

        public boolean isCloseChannel() {
            return closeChannel;
        }

        public void setCloseChannel(boolean closeChannel) {
            this.closeChannel = closeChannel;
        }

        public boolean isClosePlayback() {
            return closePlayback;
        }

        public void setClosePlayback(boolean closePlayback) {
            this.closePlayback = closePlayback;
        }
    }
}
