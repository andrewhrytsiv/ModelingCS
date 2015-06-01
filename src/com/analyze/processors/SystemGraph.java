package com.analyze.processors;

import com.analyze.tasks.Task;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;

/**
 * Created by Andrew on 29.05.2015.
 */
public class SystemGraph {

    public Map<Integer,Processor> processorMap = new HashMap<Integer, Processor>();
    public Map<Integer,LogicalLink> logicalLinkMap = new HashMap<Integer, LogicalLink>();

    public Map<Integer,Integer> getValidationIdMap(){
        Map<Integer,Integer> map = new HashMap<Integer, Integer>();
        Map<Integer,Processor> sorted = new TreeMap<Integer, Processor>(processorMap);
        Iterator<Integer> it = sorted.keySet().iterator();
        int i=0;
        while(it.hasNext()){
            map.put(it.next(), i); //processo_id , custom_vector_id
            i++;
        }
        return map;
    }
    public static SystemGraph getSystemGraph(mxGraph graph){
        graph.selectAll();
        Object[] cells = graph.getSelectionCells();
        SystemGraph sysGraph = new SystemGraph();
        for (Object c : cells) {
            mxCell cell = (mxCell) c;
            if(cell.isVertex()){
                String[] cellValue = cell.getValue().toString().split("\n");
                Processor processor = new Processor(Integer.parseInt(cellValue[0].substring(5)));
                sysGraph.processorMap.put(processor.getId(),processor);
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
                LogicalLink lLink = new LogicalLink(Integer.parseInt(cell.getId()),Integer.parseInt(sourceId.substring(5)),Integer.parseInt(targetId.substring(5)));
                sysGraph.logicalLinkMap.put(lLink.getId(), lLink);
            }
        }
        graph.clearSelection();
        return sysGraph;
    }

   /* public List<Processor> getProcessorConnectivityOrderQueue(){
        setConnectivity();
        List<Processor> list = new ArrayList<Processor>();
        for(Processor p : processorMap.values()){
            list.add(p);
        }
        return list;
    }

    private void setConnectivity(){
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
    }*/
}
