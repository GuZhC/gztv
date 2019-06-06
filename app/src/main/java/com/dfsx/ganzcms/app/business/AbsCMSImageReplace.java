package com.dfsx.ganzcms.app.business;

public abstract class AbsCMSImageReplace implements StringReplaceHelper.ReplaceString {
    public static final String REGEX = "<\\!--PICTURE#(\\d*),(\\d*),(\\d*)-->";

    @Override
    public String replace(String... replaceString) {
        if (replaceString != null && replaceString.length >= 2) {
            try {
                long id = Long.valueOf(replaceString[1]);
                String imagePath = getImagePathById(id);
                if (imagePath != null && !imagePath.isEmpty()) {
                    int width = 0;
                    String text = "";
                    if (replaceString.length >= 4) {
                        width = Integer.valueOf(replaceString[2]);
                    }
                    String heightText = "auto";
                    if (width != 0) {
                        text = "<p><img src=\"" + imagePath + "\" height=\"" + heightText + "\" width=\"" + width + "\" /></p>";
                    } else {
                        text = "<p><img src=\"" + imagePath + "\" height=\"auto\" width=\"100%\" /></p>";
                    }
                    return text;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public abstract String getImagePathById(long id);
}
