package com.team.witkers.eventbus;

import com.team.witkers.bean.TendComments;
import com.team.witkers.bean.TendItems;
import com.team.witkers.utils.MonitorData;

/**
 * Created by hys on 2016/8/29.
 */
public class MonitorEvent {

    public TendItems getTend() {
        return tend;
    }

    public TendComments getComment() {
        return comment;
    }

    private TendComments comment;
    private TendItems tend;

    public MonitorEvent(TendItems tend,TendComments comment){
        this.tend=tend;
        this.comment=comment;
    }
    
}
