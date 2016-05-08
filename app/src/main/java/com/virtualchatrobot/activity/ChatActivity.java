package com.virtualchatrobot.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.virtualchatrobot.R;
import com.virtualchatrobot.adapter.RecorderAdapter;
import com.virtualchatrobot.bean.Recorder;
import com.virtualchatrobot.manager.RecorderMediaManager;
import com.virtualchatrobot.view.AudioRecorderButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dolphix.J Qing on 2016/5/2.
 */
public class ChatActivity extends Activity{

    private ListView listView;
    private ArrayAdapter<Recorder> arrayAdapter;
    private List<Recorder> rDatas = new ArrayList<Recorder>();
    private AudioRecorderButton audioRecorderButton;

    private View animView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView)findViewById(R.id.lv_chat);
        audioRecorderButton = (AudioRecorderButton)findViewById(R.id.recorder_button);
        audioRecorderButton.setAudioRecorderFinishLinster(new AudioRecorderButton.AudioRecorderFinishLinster() {
            @Override
            public void recorderFinish(float seconds, String folder) {
                Recorder recorder = new Recorder(seconds, folder);
                rDatas.add(recorder);
                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(rDatas.size()-1);
            }
        });

        arrayAdapter = new RecorderAdapter(this,rDatas);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != animView){
                    animView.setBackgroundResource(R.mipmap.adj);
                    animView = null;
                }
                //播放动画
                animView = view.findViewById(R.id.recorder_anim);
                animView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable animDrawable =  (AnimationDrawable) animView.getBackground();
                animDrawable.start();
                //播放帧频
                RecorderMediaManager.PlaySound(rDatas.get(position).getFolder(),new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animView.setBackgroundResource(R.mipmap.adj);
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        RecorderMediaManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        RecorderMediaManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecorderMediaManager.release();
    }
}
