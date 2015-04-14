package com.analyze;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Created by Andrew on 31.03.2015.
 */
public class AnalyzeManager {
    public static ArrayList<Task> getCriticalPathNormalizationOrderQueue(mxGraph graph){
        DirectedAcyclicGraph dag = getDAG(graph);
        ArrayList<Task> queue = dag.getVertexCriticalPathWeights();
        dag.getCriticalPathWeightsWithNormalization(queue, dag.Ncr, dag.Tcr);
        System.out.println(queue);
        Collections.sort(queue,new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                double x = o1.getPr()-o2.getPr();
                if(x > 0){
                    return -1;
                }else if(x < 0){
                    return 1;
                }
                return 0;
            }
        });
        return queue;
    }
    public static ArrayList<Task> getCriticalPathOrderQueue(mxGraph graph){
        DirectedAcyclicGraph dag = getDAG(graph);
        ArrayList<Task> queue = dag.getVertexCriticalPathWeights();
        System.out.println(queue);
        Collections.sort(queue,new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                int x = o1.getCriticalPath()-o2.getCriticalPath();
                if(x == 0){
                    return o2.getCriticalPathWithVertex()-o1.getCriticalPathWithVertex();
                }
                return x;
            }
        });
        return queue;
    }
    private static DirectedAcyclicGraph getDAG(mxGraph graph){
        graph.selectAll();
        Object[] cells = graph.getSelectionCells();
        DirectedAcyclicGraph dag = new DirectedAcyclicGraph();
        for (Object c : cells) {
            mxCell cell = (mxCell) c;
            if(cell.isVertex()){
                String[] cellValue = cell.getValue().toString().split("\n");
                Task task = new Task(Integer.parseInt(cellValue[0]),Integer.parseInt(cellValue[1]));
                dag.vertexMap.put(task.getId(),task);
            }else{
                String value = "";
                if(cell.getValue()!=null){
                    value = cell.getValue().toString();
                }
                if(value.equals("")){
                    cell.setValue("1");
                    value = cell.getValue().toString();
                }
                graph.getView().clear(cell, false, false);
                graph.getView().validate();
                mxCell source = (mxCell) cell.getSource();
                mxCell target = (mxCell) cell.getTarget();
                String sourceId = source.getValue().toString().split("\n")[0];
                String targetId = target.getValue().toString().split("\n")[0];
                Link edge = new Link(Integer.parseInt(cell.getId()),Integer.parseInt(cell.getValue().toString()),Integer.parseInt(sourceId),Integer.parseInt(targetId));
                dag.edgeMap.put(edge.getId(),edge);
            }
        }
        graph.clearSelection();
        return dag;
    }

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