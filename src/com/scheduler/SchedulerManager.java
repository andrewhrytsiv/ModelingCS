package com.scheduler;

import com.analyze.tasks.TaskAnalyzeManager;
import com.analyze.tasks.Task;
import com.grapheditor.TaskGraphPanel;

import javax.swing.*;
import java.util.List;

/**
 * Created by Andrew on 29.05.2015.
 */
public class SchedulerManager {
    static List<Task> taskQueue;

    public static List<Task> initTaskQueue(){
        Object[] initParam = SchedulerManager.getParam();
        Config queueType = (Config) initParam[3];
        switch (queueType){
            case WEIGHT_ORDER:
                taskQueue =  TaskAnalyzeManager.getWeightOrderQueue(TaskGraphPanel.graph);
                break;
            case INVERSE_CRITICAL_PATH_ORDER:
                taskQueue =  TaskAnalyzeManager.getInverseCriticalPathOrderQueue(TaskGraphPanel.graph);
                break;
            case CRITICAL_PATH_ORDER_WITH_NORMALIZATION:
                taskQueue = TaskAnalyzeManager.getCriticalPathNormalizationOrderQueue(TaskGraphPanel.graph);
                break;
        }
        return taskQueue;
    }

    private  static Object[] getParam(){

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
        Integer pLinks = physicalLink.getSelectedIndex();
        Config taskQueue = Config.valueOf((String)taskQueueAlgorithm.getSelectedItem());
        return new Object[]{lType,io,pLinks,taskQueue};
    }
}
