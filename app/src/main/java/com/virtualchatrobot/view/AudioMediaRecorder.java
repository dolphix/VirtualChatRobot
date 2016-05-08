package com.virtualchatrobot.view;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.util.UUID;

/**
 * Created by Dolphix.J Qing on 2016/5/3.
 */
public class AudioMediaRecorder {

    private MediaRecorder mediaRecorder;
    private static String folder;
    private static String curFilePath;

    private boolean isPrepared;

    //单例模式
    private static AudioMediaRecorder ourInstance = new AudioMediaRecorder();

    public static AudioMediaRecorder getInstance(String fold) {
        folder = fold;
        Log.i("TAG", "getInstance: "+folder);
        return ourInstance;
    }

    private AudioMediaRecorder() {
    }


    /**
     * 回调，准备完毕
     */
    public interface AudioStateLinster{
        void prepareIsOk();
    }

    public AudioStateLinster audioStateLinster;

    public void setOnAudioStateLinster(AudioStateLinster audioStateLinster){
        this.audioStateLinster = audioStateLinster;
    }

    public void prepareAudio(){
        isPrepared = false;
        File folder = new File(this.folder);
        if (!folder.exists()){
            folder.mkdirs();
        }
        String fileName = randomFileName();
        File file = new File(folder,fileName);
        curFilePath = file.getAbsolutePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOutputFile(curFilePath);
        //设置音频源为麦克风
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置音频格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        //设置音频编码格式.amr
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try{
            mediaRecorder.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        mediaRecorder.start();
        //准备结束
        isPrepared = true;
        if (null != audioStateLinster){
            audioStateLinster.prepareIsOk();
        }

    }

    /**
     * 获取一个随机文件名
     * @return
     */
    private String randomFileName(){
        return UUID.randomUUID().toString()+".amr";
    }

    /**
     * 返回音量等级
     * @param maxLevel
     * @return
     */
    public int getVoiceLevel(int maxLevel){
        if (isPrepared){
            try{
                //getMaxAmplitude()  return 1-32767
                return maxLevel * mediaRecorder.getMaxAmplitude() / 32768 + 1;
            }catch (Exception e){e.printStackTrace();}
        }
        return 1;
    }

    public void release(){
        if (null != mediaRecorder){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public void cancel(){
        release();
        if (null != curFilePath){
            File file = new File(curFilePath);
            file.delete();
            curFilePath = null;
        }
    }

    public String getCurFilePath(){
        return curFilePath;
    }
}
