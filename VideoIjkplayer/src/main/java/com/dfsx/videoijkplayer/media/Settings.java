/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dfsx.videoijkplayer.media;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.dfsx.videoijkplayer.R;


public class Settings {
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;
    public static final int PV_PLAYER__IjkKsyMediaPlayer = 4;

    public Settings(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public boolean getEnableBackgroundPlay() {
        String key = mAppContext.getString(R.string.pref_key_enable_background_play);
        return mSharedPreferences.getBoolean(key, false);
    }

    public int getPlayer() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setPlayer(int playerType) {
        String key = mAppContext.getString(R.string.pref_key_player);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, playerType + "");
        editor.commit();
    }

    public boolean getUsingMediaCodec() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        return mSharedPreferences.getBoolean(key, false);
    }

    public void setUsingMediaCodec(boolean isUseediaCodec) {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, isUseediaCodec);
        editor.commit();
    }

    public void setUsingMediaCodecAutoRotate(boolean isUsingMediaCodecAutoRotate) {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec_auto_rotate);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, isUsingMediaCodecAutoRotate);
        editor.commit();
    }

    public boolean getUsingMediaCodecAutoRotate() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec_auto_rotate);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getUsingOpenSLES() {
        String key = mAppContext.getString(R.string.pref_key_using_opensl_es);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getPixelFormat() {
        String key = mAppContext.getString(R.string.pref_key_pixel_format);
        return mSharedPreferences.getString(key, "");
    }

    public boolean getEnableNoView() {
        String key = mAppContext.getString(R.string.pref_key_enable_no_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableSurfaceView() {
        String key = mAppContext.getString(R.string.pref_key_enable_surface_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_texture_view);
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        String key = mAppContext.getString(R.string.pref_key_enable_detached_surface_texture);
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getLastDirectory() {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        return mSharedPreferences.getString(key, "/");
    }

    public void setLastDirectory(String path) {
        String key = mAppContext.getString(R.string.pref_key_last_directory);
        mSharedPreferences.edit().putString(key, path).apply();
    }

    public void setEnableTextureView(boolean enable) {
        String key = mAppContext.getString(R.string.pref_key_enable_texture_view);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, enable);
        editor.commit();
    }

    public void setEnableSurfaceView(boolean enable) {
        String key = mAppContext.getString(R.string.pref_key_enable_surface_view);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, enable);
        editor.commit();
    }

    public boolean getUsingLiveStyle() {
        String key = mAppContext.getString(R.string.pref_key_is_live_stream);
        return mSharedPreferences.getBoolean(key, false);
    }

    public void setUsingLiveStyle(boolean isLiveStyle) {
        String key = mAppContext.getString(R.string.pref_key_is_live_stream);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, isLiveStyle);
        editor.commit();
    }
}
