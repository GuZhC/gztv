package com.dfsx.ganzcms.app.business;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplaceHelper {


    public String replaceAll(String regex, String str, ReplaceString replace) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            int count = matcher.groupCount();
            String[] findStr = new String[count];
            for (int i = 0; i < count; i++) {
                findStr[i] = matcher.group(i);
            }
            String toString = replace.replace(findStr);
            str = str.replace(matcher.group(), toString);
        }
        return str;
    }

    public String replaceAllContentImageString(String str, AbsCMSImageReplace imageReplace) {
        return replaceAll(AbsCMSImageReplace.REGEX, str, imageReplace);
    }

    public String replaceAllContentVideoString(String str, AbsCMSVideoReplace videoReplace) {
        return replaceAll(AbsCMSVideoReplace.REGEX, str, videoReplace);
    }

    public String replaceAllContentString(String str, AbsCMSContentReplace replace) {
        return replaceAll(AbsCMSContentReplace.REGEX, str, replace);
    }

    public interface ReplaceString {

        String replace(String... replaceString);
    }


    public static void main(String[] args) {

        String str = "<p>　　<strong>女富豪平均年龄49岁</strong></p> \n" +
                "<p>　　<strong>42%出自“社会大学”</strong></p> \n" +
                "<p>　　从年龄层来看，上榜女富豪平均年龄49岁，比去年大1岁，而比百富榜总榜平均年龄小5岁。</p> \n" +
                "<p>　　有7位“80后”上榜。其中，“传媒女王”36岁吴艳是其中唯一一位“80后”白手起家女企业家。其他6位“80后”分别是28岁的纪凯婷、34岁的许阳阳和周晏齐、36岁的杨惠妍、37岁的卢晓云和刘畅。</p> \n" +
                "<p>　　从学历来看，前50名女企业家中，42%出自“社会大学”，但也不乏海归，如下图：</p> \n" +
                "<div class=\"img_wrapper\"> \n" +
                " <!--PICTURE#84845805,0,0--> \n" +
                " <span class=\"img_descr\"></span> \n" +
                "</div> \n" +
                "<p>　　<strong>哪些行业女富豪最多？</strong></p> \n" +
                "<div class=\"img_wrapper\"> \n" +
                " <!--PICTURE#84859353,0,0--> \n" +
                " <span class=\"img_descr\"></span> \n" +
                "</div> \n" +
                "<div class=\"img_wrapper\"> \n" +
                " <!--PICTURE#84865557,0,0--> \n" +
                " <span class=\"img_descr\"></span> \n" +
                "</div> \n" +
                "<p>　　<strong>女富豪都爱住哪儿？</strong></p> \n" +
                "<p>　　<strong>哪个地方盛产女富豪？</strong></p> \n" +
                "<p>　　从出生地来看，粤商仍然最多，有5位；浙商今年增加2位，与粤商并列第一。今年胡润百富榜上粤商人数第一次超越浙商。白手起家女企业家中，也是粤商最多。</p> \n" +
                "<div class=\"img_wrapper\"> \n" +
                " <!--PICTURE#84873788,100,0--> \n" +
                " <!--AUDIO#11,0,0--> \n" +
                " <!--PICTURESET#11,0,0--> \n" +
                " <!--LIVE#11,0,0--> \n" +

                " <span class=\"img_descr\"></span> \n" +
                "</div> ";

        StringReplaceHelper replaceHelper = new StringReplaceHelper();
        String regex = "<\\!--PICTURE#(\\d*),(\\d*),(\\d*)-->";
        String afterReplace = replaceHelper.replaceAllContentString(str, new AbsCMSContentReplace() {
            @Override
            public String getPicturePath(long id) {
                return "[" + id + "]";
            }
        });

        System.out.println(afterReplace);
    }

}
