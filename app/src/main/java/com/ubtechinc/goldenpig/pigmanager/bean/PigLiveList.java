package com.ubtechinc.goldenpig.pigmanager.bean;

import java.util.ArrayList;
import java.util.List;

public class PigLiveList {
    private ArrayList<PigInfo> pigInfos;
    private static boolean isSingal;
    private PigLiveList() throws RuntimeException{
        if (!isSingal){
            throw new RuntimeException("没有正确初始化");
        }
    }
    private static PigLiveList instance;

    public static PigLiveList getInstance(){
        if (instance==null){
            synchronized (PigLiveList.class){
                if (instance==null){
                    isSingal=true;
                    instance=new PigLiveList();
                }
            }
        }
        return instance;
    }

    public void addPig(PigInfo pigInfo){
        if (pigInfos==null){
            pigInfos=new ArrayList<>();
        }
    }

    public ArrayList<PigInfo> getPigInfos() {
        return pigInfos;
    }

    public void addPigList(List<PigInfo> pigInfoList){
        if (pigInfoList!=null){
            if (pigInfos==null){
                pigInfos=new ArrayList<>();
                pigInfos.addAll(pigInfoList);
            }
        }
    }
}
