package com.dfsx.lzcms.liveroom;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.dfsx.lzcms.liveroom.view.LiveIjkVideoPlayer;

/**
 * Created by liuwb on 2017/1/23.
 */
public class LivePlayerTestActivity extends FragmentActivity {

    private FrameLayout videoContainer;

    private EditText editUrlText;
    private Button btnStart;

    private LiveIjkVideoPlayer videoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test_player);
        videoPlayer = new LiveIjkVideoPlayer(this);
        videoContainer = (FrameLayout) findViewById(R.id.video_container);
        editUrlText = (EditText) findViewById(R.id.edit_player_url);
        btnStart = (Button) findViewById(R.id.btn_ok);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        videoPlayer.setLayoutParams(params);
        videoContainer.addView(videoPlayer);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editUrlText.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    videoPlayer.start(url);
                }
            }
        });
    }
}
