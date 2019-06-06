package com.dfsx.ganzcms.app.business;

/**
 * 可以匹配所用的CMS主键内容
 */
public abstract class AbsCMSContentReplace implements StringReplaceHelper.ReplaceString {
    public static final String REGEX = "<\\!--(\\w*)#(\\d*)(,(.*))?-->";

    @Override
    public String replace(String... replaceString) {
        String contentTypeString = "";
        long id = 0;
        int width = 0;
        int height = 0;
        if (replaceString != null && replaceString.length > 1) {
            contentTypeString = replaceString[1];
        }
        if (replaceString != null && replaceString.length > 2) {
            try {
                id = Long.valueOf(replaceString[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (replaceString != null && replaceString.length > 3) {
            String wh = replaceString[3];
            if (wh != null && !wh.isEmpty()) {
                if (wh.startsWith(",")) {
                    wh = wh.substring(1);
                }
                String[] whArr = wh.split(",");
                if (whArr != null && whArr.length > 0) {
                    width = stringToInt(whArr[0]);
                }
                if (whArr != null && whArr.length > 1) {
                    height = stringToInt(whArr[1]);
                }
            }
        }

        return replaceType(contentTypeString, id, width, height);
    }

    protected String replaceType(String contentTypeString, long id, int w, int h) {
        if ("PICTURE".equalsIgnoreCase(contentTypeString)) {
            return replacePicture(id, w, h);
        }
        return "";
    }

    private String replacePicture(long id, int width, int height) {
        String imagePath = getPicturePath(id);
        if (imagePath != null && !imagePath.isEmpty()) {
            String heightText = "auto";
            String text = "";
            if (width != 0) {
                text = "<p><img src=\"" + imagePath + "\" height=\"" + heightText + "\" width=\"" + width + "\" /></p>";
            } else {
                text = "<p><img src=\"" + imagePath + "\" height=\"auto\" width=\"100%\" /></p>";
            }
            return text;
        }
        return "";
    }

    public abstract String getPicturePath(long id);

    private int stringToInt(String str) {
        int count = 0;
        try {
            count = Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
