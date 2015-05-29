package com.analyze.processors;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

import java.util.List;

/**
 * Created by Andrew on 29.05.2015.
 */
public class ProcessorAnalyzeManager {
    public static List<Processor> getProcessorConnectivityOrderQueue(mxGraph graph){

        return null;
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
}
