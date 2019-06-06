package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.text.TextUtils;
import com.dfsx.core.network.HttpParameters;
import com.dfsx.core.network.HttpUtil;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.model.*;
import com.dfsx.lzcms.liveroom.business.ICallBack;
import com.dfsx.lzcms.liveroom.util.StringUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.HashMap;

public class HostDetailsHelper implements IGetHostDetails {

    private Context context;
    private StringReplaceHelper replaceHelper;

    public HostDetailsHelper(Context context) {
        this.context = context;
        replaceHelper = new StringReplaceHelper();
    }

    @Override
    public void getHostDetails(long contentId, final ICallBack<IHostDetails> callBack) {
        Observable.just(contentId)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Long, Observable<HostDetailsInfo>>() {
                    @Override
                    public Observable<HostDetailsInfo> call(Long id) {
                        try {
                            String url = App.getInstance().getmSession().getContentcmsServerUrl()
                                    + "/public/contents/" + id;
                            String res = HttpUtil.executeGet(url, new HttpParameters(),
                                    App.getInstance().getCurrentToken());
                            StringUtil.checkHttpResponseError(res);
                            Gson gson = new Gson();
                            HostDetailsInfo detailsInfo = gson.fromJson(res, HostDetailsInfo.class);
                            if (detailsInfo != null && detailsInfo.getId() != 0) {
                                //获取组件
                                HostDetailsInfo.BodyComponent bodyComponent = detailsInfo.getBody_components();
                                if (bodyComponent != null && !bodyComponent.isEmpty()) {
                                    setBodyComponent(bodyComponent);
                                }
                                //set  fields
                                if (detailsInfo.getFields() != null) {
                                    setFields(detailsInfo.getFields());
                                }
                                //设置显示的内容
                                String content = detailsInfo.getBody();
                                content = replaceHelper.replaceAllContentString(content,
                                        new HostBodyStringReplace(detailsInfo));
                                detailsInfo.setHostPersonIntroduce(getHtmlContent(content));
                                return Observable.just(detailsInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HostDetailsInfo>() {
                    @Override
                    public void call(HostDetailsInfo detailsInfo) {
                        if (callBack != null) {
                            callBack.callBack(detailsInfo);
                        }
                    }
                });
    }

    private String getHtmlContent(String body) {
        String txtWeb = "<html>\n" +
                "<meta charset=\"utf-8\" />\n" +
                //                "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no\">\n" +
                //                  "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +

                "<link rel=\"stylesheet\" type=\"text/css\" href=\"zy.media.min.css\" />\n" +
                //                "<script  src=\"zy.media.min.js\" />\n" +   这儿格式错误很重，导致其他都js没有效果
                "<script src=\"zy.media.min.js\"></script>\n" +
                "<script src=\"jquery-1.8.3.min.js\"></script>" +
                "<style>.zy_media{min-height: 56vw;}" +
                "  video{\n" +
                "            background: #000;\n" +
                "        }\n" +
                ".play-video-btn{\n" +
                "            display:block;\n" +
                "            position:absolute;\n" +
                "            top:0;\n" +
                "            bottom:0;\n" +
                "            width:100%;\n" +
                "            cursor: pointer;\n" +
                "            background: rgba(0,0,0,0.5) url('play.png') no-repeat center;\n" +
                "            background-size: 60px;\n" +
                "        }</style>\n" +
                "<style>video{height: auto;width:100%;width:100vw;}\n" +
                "img{max-width:100%}\n" +
                "body * {\n" +
                "    max-width: 100vw!important;\n" +
                "}</style>\n" +
                //                "rgba(255,255,255,0.3)"
                //                "<style>\n" +
                //                "a{poorfish:expression(this.onclick=function kill(){return false})}\n" +
                //                "</style>\n"+
                //                "<style type=\"text/css\">\n" +
                //                "#modelView{background-color:#DDDDDD;z-index:0;opacity:0.7;height: 100%;width: 100%;position: relative;}\n" +
                //                ".playvideo{padding-top: auto;z-index: 9999;position: relative;}\n" +
                //                ".zy_media{z-index: 999999999}\n" +
                //                "</style>"+

                "<script type=\"text/javascript\"  charset=\"GB2312\">\n" +

                "var canPlay = false;\n" +
                //                "$(function(){\n" +
                //                "    $('video').on('play', function(){\n" +
                //                "if(canPlay){\n" +
                //                "return;\n" +
                //                "}\n" +
                //                "  this.pause();   \n" +
                //                "if(window.imagelistner.isWifiStatus()){\n" +
                //                "canPlay = true;\n" +
                //                "this.play();\n" +
                //                "}else{\n" +
                //                "if(confirm('当前使用的是移动流量,确认继续播放？') ){\n" +
                ////                "if(window.imagelistner.showPlayDialog()){\n" +
                //                "canPlay = true;\n" +
                //                "this.play()\n" +
                //                "}\n" +
                //                "}\n" +
                //                "\t});\n" +
                //                "});\n" +
                "var playvideo=null;\n" +
                "var postimg=null;\n" +
                "$(function(){\n" +
                "    $('.play-video-btn').on('click', function(){\n" +
                "playvideo=$(this).closest('div').find('video')[0];\n" +
                "postimg=$(this);\n" +
                //     "alert(1);\n" +
                "if(canPlay){\n" +
                "playvideo.play();$(this).hide();\n" +
                "return;\n" +
                "}\n" +
                "if(window.imagelistner.isWifiStatus()){\n" +
                //                "canPlay = true;\n" +
                "playvideo.play();$(this).hide();\n" +
                "}else{\n" +
                //                "if(confirm('当前使用的是移动流量,确认继续播放？') ){\n" +
                //                "if(window.imagelistner.showPlayDialog()){\n" +
                "window.imagelistner.showPlayDialog()\n" +
                //                "canPlay = true;\n" +
                //                "this.play()\n" +
                //                "}\n" +
                "}\n" +
                "});\n" +
                "});\n" +

                "$(function(){\n" +
                "    $('body').on('click', 'a', function(e){\n" +
                //                "        e.preventDefault();  alert($(this).attr('href'));\n" +
                "        e.preventDefault();\n" +
                "        return false;\n" +
                "    });\n" +
                "});\n" +

                "function startvideo() {\n" +
                "if(playvideo!=null){\n" +
                //                " canPlay = true;\n" +
                "playvideo.play();\n" +
                "}\n" +
                "if(postimg!=null){\n" +
                "postimg.hide();\n" +
                "}\n" +
                "}\n" +

                "    function imgResize() {\n" +
                "        var imgs = document.getElementsByTagName(\"img\");\n" +
                "        var array = new Array();\n" +
                "        for (var j = 0; j < imgs.length; j++) {\n" +
                "         array[j] = imgs[j].attributes[\'src\'].value;\n" +
                "          }\n" +
                "        for (var i = 0; i < imgs.length; i++) {\n" +
                "            imgs[i].pos = i;\n" +
                "            imgs[i].onclick=function()" +
                "            {\n" +
                "              var pos = this.pos;\n" +
                "window.imagelistner.openImage(array.join(\",\"),pos);\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    </script>\n" +
                "<body>";

        txtWeb += body;
        if (android.os.Build.VERSION.SDK_INT > 20) {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'metadata'});</script>";
        } else {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'none'});</script>";
        }
        txtWeb += "</body></html>";

        return txtWeb;
    }

    class HostBodyStringReplace extends AbsCMSContentReplace {

        private HostDetailsInfo detailsInfo;

        public HostBodyStringReplace(HostDetailsInfo detailsInfo) {
            this.detailsInfo = detailsInfo;
        }

        @Override
        public String getPicturePath(long id) {
            boolean is = detailsInfo.getBody_components() != null &&
                    detailsInfo.getBody_components().getPictureComponentMap() != null;
            if (is) {
                PictureContent pictureContent = detailsInfo.getBody_components().getPictureComponentMap().get(id);
                if (pictureContent != null && !TextUtils.isEmpty(pictureContent.getUrl())) {
                    return pictureContent.getUrl();
                }
            }
            return "";
        }
    }

    private void setFields(HostDetailsInfo.CustomField customField) {
        try {
            if (customField.getSlideset_id() != 0) {
                String slideUrl = App.getInstance().getmSession().getContentcmsServerUrl()
                        + "/public/picturesets/" + customField.getSlideset_id();

                String slideRes = HttpUtil.executeGet(slideUrl, new HttpParameters(),
                        App.getInstance().getCurrentToken());
                StringUtil.checkHttpResponseError(slideRes);

                JSONArray array = new JSONArray(slideRes);
                if (array != null && array.length() > 0) {
                    JSONObject item = array.optJSONObject(0);
                    PictureSetContent content = new Gson().fromJson(item.toString(), PictureSetContent.class);
                    customField.setPictureSetContent(content);
                }
            }

            if (customField.getAudio_id() != 0) {
                String audioUrl = App.getInstance().getmSession().getContentcmsServerUrl()
                        + "/public/audios/" + customField.getAudio_id();

                String audioRes = HttpUtil.executeGet(audioUrl, new HttpParameters(),
                        App.getInstance().getCurrentToken());
                StringUtil.checkHttpResponseError(audioRes);

                JSONArray array = new JSONArray(audioRes);
                if (array != null && array.length() > 0) {
                    JSONObject item = array.optJSONObject(0);
                    AudioContent audioContent = new Gson().fromJson(item.toString(), AudioContent.class);
                    customField.setAudioContent(audioContent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBodyComponent(HostDetailsInfo.BodyComponent bodyComponent) {
        if (bodyComponent.getPictures() != null && bodyComponent.getPictures().size() > 0) {
            String picApi = App.getInstance().getmSession().getContentcmsServerUrl()
                    + "/public/pictures/";
            HashMap<Long, PictureContent> picMap =
                    new LotContentsGetHelper<PictureContent>()
                            .getContentMap(picApi, bodyComponent.getPictures(), PictureContent.class);
            bodyComponent.setPictureComponentMap(picMap);
        }

        if (bodyComponent.getVideos() != null && bodyComponent.getVideos().size() > 0) {
            String videApi = App.getInstance().getmSession().getContentcmsServerUrl()
                    + "/public/videos/";
            HashMap<Long, VideoContent> videoContentHashMap = new LotContentsGetHelper<VideoContent>()
                    .getContentMap(videApi, bodyComponent.getVideos(), VideoContent.class);
            bodyComponent.setVideoComponentMap(videoContentHashMap);
        }
        if (bodyComponent.getAudios() != null && bodyComponent.getAudios().size() > 0) {
            String audioApi = App.getInstance().getmSession().getContentcmsServerUrl()
                    + "/public/audios/";
            HashMap<Long, AudioContent> audioContentHashMap = new LotContentsGetHelper<AudioContent>()
                    .getContentMap(audioApi, bodyComponent.getVideos(), AudioContent.class);
            bodyComponent.setAudioComponentMap(audioContentHashMap);
        }
    }

}
