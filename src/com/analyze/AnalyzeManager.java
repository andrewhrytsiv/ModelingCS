package com.analyze;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Andrew on 31.03.2015.
 */
public class AnalyzeManager {
    public static ArrayList<Task> getWeightOrderQueue(mxGraph graph){
        ArrayList<Task> taskQueue = new ArrayList<Task>();
        graph.selectAll();
        Object[] cells = graph.getSelectionCells();
        for (Object c : cells) {
            mxCell cell = (mxCell) c;
            if(cell.isVertex()){
                String[] cellValue = cell.getValue().toString().split("\n");
                taskQueue.add(new Task(Integer.parseInt(cellValue[0]),Integer.parseInt(cellValue[1])));
            }
        }
        graph.clearSelection();
        Collections.sort(taskQueue);
        return taskQueue;
    }
}
