package com.team.witkers.eventbus;

import java.io.File;

/**
 * Created by hys on 2016/7/19.
 */
public class CropAvatarEvent {
    public CropAvatarEvent(){}
    private File file;
    private String bFileName;


    public String getbFileName() {
        return bFileName;
    }
    public void setbFileName(String bFileName) {
        this.bFileName = bFileName;
    }


    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }


    public CropAvatarEvent(File file1,String bFileName1){
        file=file1;
        bFileName=bFileName1;
    }

}
