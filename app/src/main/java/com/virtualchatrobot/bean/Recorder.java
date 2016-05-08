package com.virtualchatrobot.bean;

/**
 * Created by Dolphix.J Qing on 2016/5/3.
 */
public class Recorder {
    private float time;
    private String folder;

    public Recorder(float time, String folder) {
        this.time = time;
        this.folder = folder;
    }

    public float getTime() {
        return time;
    }

    public String getFolder() {
        return folder;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
