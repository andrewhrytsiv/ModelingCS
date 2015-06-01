package com.scheduler;

import com.analyze.processors.LogicalLink;
import com.analyze.processors.Processor;
import com.analyze.processors.SystemGraph;
import com.analyze.tasks.DirectedAcyclicGraph;
import com.analyze.tasks.Link;
import com.analyze.tasks.TaskAnalyzeManager;
import com.analyze.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grapheditor.TaskPanel;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Andrew on 29.05.2015.
 */
public class SchedulerManager {

    public static void buildJSON(Map<Integer,Integer> validator, DirectedAcyclicGraph dag, List<Task> taskQueue, SystemGraph systemGraph, Map<Integer,Integer> sysValidator,Integer physLinks,Config duplexMode){
        //build task transaction matrix
        int[][] matrix = new int[validator.size()][validator.size()];
        for(Map.Entry<Integer,Task> entry : dag.vertexMap.entrySet()){
            int indexForTask = validator.get(entry.getKey());
            int weight = entry.getValue().getWeight();
            matrix[indexForTask][indexForTask] = weight;
        }
        for( Map.Entry<Integer, Link> entry : dag.edgeMap.entrySet()){
            Link link = entry.getValue();
            int sourseIndex = validator.get(link.getSourceId());
            int targetIndex = validator.get(link.getTargetId());
            matrix[sourseIndex][targetIndex] = link.getWeight();
        }
      /*  for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[i].length; j++){
                System.out.print(matrix[i][j]+" ");
            }
            System.out.println();
        }*/
        //build taskQueue
        int[] queue = new int[taskQueue.size()];
        int current = 0;
        for(Task task : taskQueue){
            int index = validator.get(task.getId());
            queue[current] = index;
            current++;
        }
       /* for(int i=0; i<queue.length; i++){
            System.out.print(queue[i]+" ");
        }*/
        //system
        int[][] systemMatrix = new int[sysValidator.size()][sysValidator.size()];
        for(Map.Entry<Integer, LogicalLink> entry : systemGraph.logicalLinkMap.entrySet()){
            LogicalLink logicalLink = entry.getValue();
            int sourceIndex = sysValidator.get(logicalLink.getSourceId());
            int targetIndex = sysValidator.get(logicalLink.getTargetId());
            systemMatrix[sourceIndex][targetIndex] = 1;
        }
       /* System.out.println();
        for(int i=0; i<systemMatrix.length; i++){
            for(int j=0; j<systemMatrix[i].length; j++){
                System.out.print(systemMatrix[i][j]+" ");
            }
            System.out.println();
        }*/
        //build json
        Gson gson = new Gson();
        JsonObject result = new JsonObject();
        //tasks attribute
        String json = gson.toJson(matrix);
        JsonElement jelem = gson.fromJson(json, JsonElement.class);
        result.add("task", jelem);
        //queue attribute
        json = gson.toJson(queue);
        jelem = gson.fromJson(json, JsonElement.class);
        result.add("queue", jelem);
        //system attribute
        json = gson.toJson(systemMatrix);
        jelem = gson.fromJson(json, JsonElement.class);
        result.add("system", jelem);
        result.addProperty("physLinks", physLinks.intValue());
        if(duplexMode == Config.FULL_DUPLEX){
            result.addProperty("duplex", 1);
        }else{
            result.addProperty("duplex", 0);
        }
        System.out.println(gson.toJson(result));

    }

    public static List<Task> getTaskQueue(Object[] initParam){
        Config queueType = (Config) initParam[3];
        List<Task> queue = null;
        switch (queueType){
            case WEIGHT_ORDER:
                queue =  TaskAnalyzeManager.getWeightOrderQueue(TaskPanel.graph);
                break;
            case INVERSE_CRITICAL_PATH_ORDER:
                queue =  TaskAnalyzeManager.getInverseCriticalPathOrderQueue(TaskPanel.graph);
                break;
            case CRITICAL_PATH_ORDER_WITH_NORMALIZATION:
                queue = TaskAnalyzeManager.getCriticalPathNormalizationOrderQueue(TaskPanel.graph);
                break;
        }
        return queue;
    }

    public  static Object[] getParam(){
        JComboBox<String> linksType = new JComboBox<String>();
        linksType.addItem(Config.FULL_DUPLEX.toString());
        linksType.addItem(Config.HALF_DUPLEX.toString());
        JComboBox<String> ioProcessor = new JComboBox<String>();
        ioProcessor.addItem(Config.YES.toString());
        ioProcessor.addItem(Config.NO.toString());
        JComboBox<Integer> physicalLink = new JComboBox<Integer>();
        physicalLink.addItem(1);
        physicalLink.addItem(2);
        physicalLink.addItem(3);
        physicalLink.addItem(4);
        physicalLink.addItem(5);
        JComboBox<String> taskQueueAlgorithm = new JComboBox<String>();
        taskQueueAlgorithm.addItem(Config.WEIGHT_ORDER.toString());
        taskQueueAlgorithm.addItem(Config.INVERSE_CRITICAL_PATH_ORDER.toString());
        taskQueueAlgorithm.addItem(Config.CRITICAL_PATH_ORDER_WITH_NORMALIZATION.toString());

        Object[] fields = {"Links Type",linksType,"IO Processor",ioProcessor,"Physical Link",physicalLink,"Task Queue Algorithm", taskQueueAlgorithm};
        JOptionPane.showMessageDialog(null,fields,"Graph Param",JOptionPane.OK_CANCEL_OPTION);

        Config lType = Config.valueOf((String)linksType.getSelectedItem());
        Config io = Config.valueOf((String)ioProcessor.getSelectedItem());
        Integer pLinks = (Integer)physicalLink.getSelectedIndex();
        Config taskQueue = Config.valueOf((String)taskQueueAlgorithm.getSelectedItem());
        return new Object[]{lType,io,pLinks,taskQueue};
    }
}
