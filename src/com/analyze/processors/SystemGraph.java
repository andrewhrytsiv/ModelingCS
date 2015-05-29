package com.analyze.processors;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 29.05.2015.
 */
public class SystemGraph {

    public Map<Integer,Processor> processorMap = new HashMap<Integer, Processor>();
    public Map<Integer,LogicalLink> logicalLinkMap = new HashMap<Integer, LogicalLink>();


    public void setConnectivity(){
        for(Map.Entry<Integer,Processor> processorEntry: processorMap.entrySet()){
            Integer taskId = processorEntry.getValue().getId();
            int currentTaskConnectivity = 0;
            for(Map.Entry<Integer,LogicalLink> lineEntry: logicalLinkMap.entrySet()){
                Integer sourceId = lineEntry.getValue().getSourceId();
                Integer targetId = lineEntry.getValue().getTargetId();
                if(sourceId.equals(taskId)){
                    currentTaskConnectivity++;
                }
                if(targetId.equals(taskId)){
                    currentTaskConnectivity++;
                }
            }
            processorEntry.getValue().setConnectivity(currentTaskConnectivity);
        }
    }
}
