package com.dfsx.ganzcms.app.business;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.dfsx.core.common.Util.Util;
import com.dfsx.core.common.act.WhiteTopBarActivity;
import com.dfsx.core.exception.ApiException;
import com.dfsx.core.file.FileUtil;
import com.dfsx.core.network.datarequest.DataRequest;
import com.dfsx.core.network.datarequest.DataReuqestType;
import com.dfsx.ganzcms.app.App;
import com.dfsx.ganzcms.app.BuildConfig;
import com.dfsx.ganzcms.app.R;
import com.dfsx.ganzcms.app.act.ActNewsImageDetails;
import com.dfsx.ganzcms.app.act.CmsVideoActivity;
import com.dfsx.ganzcms.app.act.CvideoPlayAct;
import com.dfsx.ganzcms.app.fragment.EditWordsFragment;
import com.dfsx.ganzcms.app.fragment.LiveTvFragment;
import com.dfsx.ganzcms.app.fragment.NewsWebVoteFragment;
import com.dfsx.ganzcms.app.fragment.QianDaoFragment;
import com.dfsx.ganzcms.app.model.CommendCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsEntry;
import com.dfsx.ganzcms.app.model.ContentCmsInfoEntry;
import com.dfsx.ganzcms.app.model.LiveInfo;
import com.dfsx.core.common.Util.EditChangedLister;
import com.dfsx.ganzcms.app.util.UtilHelp;
import com.dfsx.ganzcms.app.view.EditTextEx;
import com.dfsx.lzcms.liveroom.fragment.BaseAndroidWebFragment;
import com.dfsx.lzcms.liveroom.model.FullScreenRoomIntentData;
import com.dfsx.lzcms.liveroom.util.IsLoginCheck;
import com.dfsx.lzcms.liveroom.view.LiveServiceSharePopupwindow;
import com.dfsx.lzcms.liveroom.view.SharePopupwindow;
import com.dfsx.thirdloginandshare.share.AbsShare;
import com.dfsx.thirdloginandshare.share.ShareContent;
import com.dfsx.thirdloginandshare.share.ShareFactory;
import com.dfsx.thirdloginandshare.share.SharePlatform;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by  heyang on 2016/8/29.
 * 替换WebView中的图片 视频
 */
public class WebMediaReplacelHelper {
    private static final String TAG = "WebMediaReplacelHelper";
    private Context context;
    private int mScreenWidth, mHeight;
    public static final String KEYWORDSTART = "<!--";
    public static final String KEYWORDEND = "->";
    private ContentCmsInfoEntry _geComtenInfo;
    private static final int HTMLWEB_TYPE = 1;
    private static final int VIDEOENABLEWEB_TYPE = 0;
    private int meWebTYPE = 0;

    public WebMediaReplacelHelper(Context context) {
        this.context = context;
        mScreenWidth = UtilHelp.getScreenWidth(context);
        mHeight = UtilHelp.getScreenHeight(context);

        if (android.os.Build.VERSION.SDK_INT > 20) {
            meWebTYPE = HTMLWEB_TYPE;
        } else {
            meWebTYPE = VIDEOENABLEWEB_TYPE;
        }
    }

    public void set_geComtenInfo(ContentCmsInfoEntry _geComtenInfo) {
        this._geComtenInfo = _geComtenInfo;
    }

    public String getHtmlWeb(String body) {
        body = findReplaceString(body);

        String txtWeb = "<html>\n" +
                "<meta charset=\"utf-8\" />\n" +
//                "<meta name=\"viewport\" content=\"width=device-width\"/>\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no\">\n" +
//                  "<link rel=\"stylesheet\" type=\"text/css\" href=\"main.css\" />\n" +

                "<link rel=\"stylesheet\" type=\"text/css\" href=\"zy.media.min.css\" />\n" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />\n" +
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

//                "<style>video{height: auto;width:100%;width:100vw;}\n" +
//                "img{max-width:100%}\n" +
//                "body * {\n" +
//                "    max-width: 100vw!important;\n" +
//                "}</style>\n" +
                "<style>\n" +
                "    html,\n" +
                "    body {\n" +
//                "        width: 100%;\n" +
//                "        overflow-x: hidden;\n" +
                "        margin: 0;\n" +
                "        padding:: 0;\n" +
                "    }\n" +
//                "    video {\n" +
//                "        height: auto;\n" +
//                "        width: 100%;\n" +
//                "        width: 100vw;\n" +
//                "    }\n" +
//                "    audio {\n" +
//                "       display: block;\n" +
//                "       width: 100vw !important;\n" +
//                "       position: absolute;\n" +
//                "       right: -30px;\n" +
//                "   }\n"+
//                "audio::-internal-media-controls-download-button {\n"+
//                "       display:none;\n" +
//                "    }\n"+
                "audio::-webkit-media-controls {\n" +
                "       overflow: hidden !important;\n" +
                "    }\n"+
                "audio::-webkit-media-controls-enclosure {\n" +
                "       width: calc(100% + 32px);\n" +
                "       margin-left: auto;\n" +
                "    }\n"+
//                "    img {\n" +
//                "        max-width: 100%;\n" +
//                "        object-fit: contain;\n" +
//                "        height: auto!important;\n" +
//                "        display: block;\n" +
//                "    }\n" +
//                "    body * {\n" +
//                "        max-width: 100vw!important;\n" +
//                "    }\n" +
                "</style>"+

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

//                "$(function(){\n" +
//                "    $('body').on('click', 'a', function(e){\n" +
////                "        e.preventDefault();  alert($(this).attr('href'));\n" +
//                "        e.preventDefault();\n" +
//                "        return false;\n" +
//                "    });\n" +
//                "});\n" +


                // 详情页播放cvideo 打开另一个页面，暂停video播放
                "function stopAudio() {\n" +
//                "var audio = document.getElementsByTagName(\"audio\");\n" +
                "var audio = document.getElementById(\"audio\");\n" +
//                "var audio=$(this).closest('p').find('audio')[0];\n"+
                "if(audio!=null){\n" +
                "if(!audio.paused){\n" +
                     "audio.pause();\n" +
                   "}\n" +
                 "}\n" +
                "}\n" +

                // 详情页播放cvideo 打开另一个页面，暂停video播放
                "function stopvideo() {\n" +
                "if(playvideo!=null){\n" +
//                "if(playvideo.paused){\n" +
                "playvideo.pause();\n" +
//                "}\n" +
                "}\n" +
                "}\n" +

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
                "<body> <section class=\"content\">";
//        txtWeb+="<h3>"+mCotentInfoeny.getTitle()+"</h3>";
        ///      String  time=UtilHelp.getTimeString("yyyy-MM-dd",mCotentInfoeny.getPublish_time());
//        txtWeb+="<p><img  src=\""+mCotentInfoeny.getPublisher_avatar_url()+"\" height=\""+25+"\" width=\""+25+"\" />"+time+"</p>";
        //      txtWeb+="<p>"+mCotentInfoeny.getPublisher_name()+"    "+time+"</p>";
//                    txtWeb += "<p style=\"word-break:break-all;padding:5px\">";
        //     txtWeb += "<p style=\"text-align:justify;font-size:17px;line-height:180%;text-indent:2em;\">";
//                    txtWeb += "<font style=\"line-height:180%;font-size:17px\">";

        if (body != null) {
            Log.e("TAG", body.toString());
            txtWeb += body;
        }
//        txtWeb += "</font>";
        //       txtWeb += "</p>";
//        txtWeb += "<script>zymedia('video', {autoplay: true, preload: 'metadata'});</script>";
        //       txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'metadata'});</script>";
//        txtWeb += "<script>zymedia('video', {autoplay: true, preload: 'none'});</script>";
        if (meWebTYPE == HTMLWEB_TYPE) {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'metadata'});</script>";
        } else {
            txtWeb += "<script>zymedia('video', {autoplay: true,hideVideoControlsOnLoad:true, preload: 'none'});</script>";
        }
        txtWeb += "</section></body></html>";

        return txtWeb;
    }

    public String findReplaceString(String str) {
        if (str == null || str.length() == 0) {
            Log.e("TAG", "getLrcRows str null or empty");
            return null;
        }
        StringReader reader = new StringReader(str);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        String lineHeader = "";
        try {
            //循环地读取歌词的每一行
            do {
                line = br.readLine();
                System.out.println("line == " + line);
                //                Log.d("TAG", "str line: " + line);
                if (!"".equals(line) && line != null) {
                    String tempLine = lineHeader + line;
                    String[] tempArr = findAndReplace(tempLine);
                    stringBuffer.append(tempArr[0]);
                    lineHeader = tempArr[1];
                }
            } while (line != null);

            return stringBuffer.toString();
        } catch (Exception e) {
            //            Log.e("TAG", "parse exceptioned:" + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
    }

    public String[] findAndReplace(String lineStr) {
        String[] backStrArr = new String[2];
        String str = lineStr;
        String tempStr = "";
        while (isFind(str)) {
            int start = str.indexOf(KEYWORDSTART);
            int end = str.indexOf(KEYWORDEND, start);
//            System.out.println("start == " + start);
//            System.out.println("end == " + end);
            if (start < end) {
                String selectedStr = str.substring(start, end + KEYWORDEND.length());
//                System.out.println("selectedStr == " + selectedStr);
//                String replaceStr = getReplaceText(selectedStr);
//                if (!TextUtils.isEmpty(replaceStr))
                str = str.replace(selectedStr, getReplaceText(selectedStr));
            } else if (end == -1) {
                tempStr = str.substring(start, str.length());
                str = str.substring(0, start);
            }
        }
//        System.out.println("back line == " + str);
//        System.out.println("back temp == " + tempStr);
        backStrArr[0] = str == null ? "" : str;
        backStrArr[1] = tempStr;
        return backStrArr;
    }

    public String getReplaceText(String str) {
        String[] pst = str.split(",");
        String result = "";
        if (pst.length != 3 || TextUtils.isEmpty(pst[0]))
            return "";
        int index = pst[0].toString().indexOf("#");
        int tag = pst[0].toString().lastIndexOf("-");
        String markTag = "";
        if (index > tag) {
            markTag = pst[0].substring(tag + 1, index);
        }
        pst[0] = pst[0].toString().substring(index + 1);
        index = pst[2].toString().indexOf("-");
        pst[2] = pst[2].toString().substring(0, index);
        if (TextUtils.equals("PICTURE", markTag)) {
            result = pearseImageString(Long.parseLong(pst[0]));
        } else if (TextUtils.equals("VIDEO", markTag)) {
            result = pearseVideoString(Long.parseLong(pst[0]));
        } else if (TextUtils.equals("AUDIO", markTag)) {
            result = pearseAudioString(Long.parseLong(pst[0]));
        }
        return result;
    }

    private boolean isFind(String lineStr) {
        return lineStr.contains(KEYWORDSTART);
    }

    public String pearseImageString(long id) {
        String newLine = "";
        if (_geComtenInfo == null) return "";
        List<ContentCmsInfoEntry.GroupImgsBean> items = _geComtenInfo.getGroupimgs();
        if (items != null && items.size() > 0) {
            for (ContentCmsInfoEntry.GroupImgsBean bean : _geComtenInfo.getGroupimgs()) {
                if (id == bean.getId()) {
                    String path = bean.getUrl();
//                    path += "?w=" + nScreenWidth + "&s=1";
                    int dpWidth = Util.dp2px(context, bean.getWidth());
                    int dpHeight = Util.dp2px(context, bean.getHeight());
//                    int dpWidth = bean.getWidth();
                    if (dpWidth >= mScreenWidth) {
                        path += "?w=" + mScreenWidth + "&s=0";
                        newLine += "<img ds-body-img src=\"" + path + "\" height=\"auto\" width=\"100%\" />";
                    } else {
                        path += "?w=" + dpWidth + "&h=" + dpHeight;
                        newLine += "<img ds-body-img src=\"" + path + "\" height=\"" + bean.getHeight() + "\" width=\"" + bean.getWidth() + "\" />";
                    }
//                    path += "?w=" + nScreenWidth + "&s=0";
//                    newLine += "<p><img src=\"" + path + "\" height=\"auto\" width=\"100%\" /></p>";
                    break;
                }
            }
        }
        return newLine;
    }

    public String pearseVideoString(long id) {
        String newLine = "";
        if (_geComtenInfo == null) return "";
        List<ContentCmsInfoEntry.VideosBean> items = _geComtenInfo.getVideoGroups();
        if (items != null && items.size() > 0) {
            for (ContentCmsInfoEntry.VideosBean bean : items) {
                if (id == bean.getId()) {
                    if (bean.getVersions() == null) break;
//                    newLine += "<p><video src=\"" + bean.getVersions().get(0).getUrl()+ "\"  poster=\"" + bean.getCoverUrl()+ "\"  height=auto  width=100%  controls /></p>";
//                    width:100%;height:auto;
                    //                newLine += "<div class=\"playvideo\" >";
                    newLine += "<div style=\"position:relative;overflow: hidden;\"><div><video  poster=\"" + bean.getCoverUrl() + "\" />";
                    newLine += "<source src=\"" + bean.getVersions().getUrl() + "\" type=\"video/mp4\" >";
                    newLine += "您的浏览器不支持HTML5视频";
                    newLine += "</video></div>";
                    newLine += "<span class=\"play-video-btn\"></span></div>";
//                    newLine += "<div id=\"modelView\">&nbsp;</div>\n" +
//                            "</div>";

//                    newLine += "<script>\n" +
//                            "//document.documentElement.style.overflow='hidden';\n" +
//                            "document.body.style.overflow='hidden';\n" +
//                            "zymedia('video',{autoplay: true});\n" +
//                            "var screenheight = window.screen.height/2;\n" +
//                            "$(\"#modelView\").width(window.screen.width);\n" +
//                            "$(\"#modelView\").height(window.screen.height);\n" +
//                            "var videoheight = $(\".zy_media\").height()/2;\n" +
//                            "var padding_top = screenheight-videoheight;\n" +
//                            "$(\".playvideo\").css({\"top\":padding_top});\n" +
//                            "$(\"#modelView\").css({\"margin-top\":-1*(padding_top+$(\".zy_media\").height())});\n" +
//                            "</script>";
                    break;
                }
            }
        }
        return newLine;
    }

    public String pearseAudioString(long id) {
        String newLine = "";
        if (_geComtenInfo == null) return "";
        List<ContentCmsInfoEntry.VideosBean> items = _geComtenInfo.getAduioGroups();
        if (items != null && items.size() > 0) {
            for (ContentCmsInfoEntry.VideosBean bean : items) {
                if (id == bean.getId()) {
                    if (bean.getVersions() != null)
                        newLine += "<p><audio id=\"audio\"  src=\"" + bean.getVersions().getUrl() + "\" height=auto  width=100%  controls /></p>";
//                        newLine += "<p><audio  src=\"http://223.255.28.42:8003/2017-06-14/317/1238c0ef6d345011cdda8d5604d6333e.mp3\" height=auto  width=100%  controls /></p>";
                    break;
                }
            }
        }
        return newLine;
    }


}
