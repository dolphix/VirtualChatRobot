package com.virtualchatrobot.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.virtualchatrobot.R;

/**
 * Created by Dolphix.J Qing on 2016/5/2.
 */
public class AudioRecorderButton extends Button implements AudioMediaRecorder.AudioStateLinster {

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_CANCEL = 3;

    private int curState = STATE_NORMAL;
    private boolean isRecording = false;
    private float totalAudioTime = 0f;
    //是否触发Long onClick
    private boolean longClick = false;

    AudioRecorderDialog audioRecorderDialog;
    AudioMediaRecorder audioMediaRecorder;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        audioRecorderDialog = new AudioRecorderDialog(getContext());

        //TODO 判断是否有外置存储卡
        String folder = Environment.getExternalStorageDirectory() + "/VCR";
        audioMediaRecorder = AudioMediaRecorder.getInstance(folder);
        audioMediaRecorder.setOnAudioStateLinster(this);
        totalAudioTime = 0f;

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick = true;
                audioMediaRecorder.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioRecorderFinishLinster{
        void recorderFinish(float seconds, String folder);
    }

    private AudioRecorderFinishLinster audioRecorderFinishLinster;

    public void setAudioRecorderFinishLinster(AudioRecorderFinishLinster audioRecorderFinishLinster){
        this.audioRecorderFinishLinster = audioRecorderFinishLinster;
    }

    @Override
    public void prepareIsOk() {
        handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    private Runnable getVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRecording){
                try{
                    Thread.sleep(100);
                    totalAudioTime += 0.1f;
                }catch (Exception e){e.printStackTrace();}

                handler.sendEmptyMessage(MSG_VOICE_CHANGED);
            }
        }
    };
    private static final int MSG_AUDIO_PREPARED = 0x001;
    private static final int MSG_VOICE_CHANGED = 0x010;
    private static final int MSG_DIALOG_DIMISS = 0x011;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    audioRecorderDialog.loadRecordingDialog();
                    isRecording = true;
                    new Thread(getVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    audioRecorderDialog.updateVoiceLevelDialog(audioMediaRecorder.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    audioRecorderDialog.dimissDialog();
                    break;

            }
            super.handleMessage(msg);
        }
    };

    /**
     * 改变状态
     * @param curState
     */
    private void changeState(int curState){
        if (curState != this.curState){
            this.curState = curState;
            switch (curState){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording){
                        audioRecorderDialog.recordingDialog();
                    }
                    break;
                case STATE_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_recording);
                    setText(R.string.str_recorder_cancel);
                    audioRecorderDialog.wantToCancelDialog();
                    break;
            }
        }
    }

    /**
     * 重置状态
     */
    private void reSetState(){
        isRecording = false;
        longClick = false;
        changeState(STATE_NORMAL);
        totalAudioTime = 0f;
    }

    /**
     * 判断手势是否取消
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y){
        if (x < 0 || x > getWidth()){
            return true;
        }
        if (y < (- DISTANCE_Y_CANCEL) || y > (getHeight() + DISTANCE_Y_CANCEL)){
            return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording){
                    if (wantToCancel(x,y)){
                        changeState(STATE_CANCEL);
                    }else{
                        changeState(STATE_RECORDING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!longClick){
                    reSetState();
                    super.onTouchEvent(event);
                }
                if (!isRecording || totalAudioTime < 0.6f){
                    Log.i("TAG", "onTouchEvent: timeTooShortDialog");
                    audioRecorderDialog.timeTooShortDialog();
                    audioMediaRecorder.cancel();
                    handler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS,1300);
                } else if (STATE_RECORDING == curState){//正常录制结束
                    audioRecorderDialog.dimissDialog();
                    audioMediaRecorder.release();
                    if (null != audioRecorderFinishLinster){
                        audioRecorderFinishLinster.recorderFinish(totalAudioTime,audioMediaRecorder.getCurFilePath());
                    }
                }else if (STATE_CANCEL == curState){
                    audioRecorderDialog.dimissDialog();
                }
                reSetState();
                break;
        }

        return super.onTouchEvent(event);
    }


}
