package com.grapheditor;

import com.mxgraph.io.mxCodec;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxUtils;
import com.mxgraph.util.mxXmlUtils;
import com.mxgraph.view.mxGraph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrew on 21.03.2015.
 */
// variant 1-8-15
public class ActionManager {

    private static Integer DEFAULT_MIN = 1;
    private static Integer DEFAULT_MAX = 4;
    private static Integer DEFAULT_COUNT = 8;
    private static Double DEFAULT_CONNECTIVITY = 0.6;

    public static void generateGraphAction(JTabbedPane tabPane){
        Object[] graphParam = getParam();
        Integer min = (Integer)graphParam[0];
        Integer max = (Integer)graphParam[1];
        Integer count = (Integer)graphParam[2];
        Double connectivity = (Double) graphParam[3];
        //create new graph model
        tabPane.remove(0);
        TaskPanel task = new TaskPanel(tabPane);
        tabPane.add(task,0);
        tabPane.setTitleAt(0, "    Task Graph    ");
        tabPane.setSelectedIndex(0);
        mxGraph graph = task.graph;

        //start generate graph
        Random rand = new Random();
        graph.getModel().beginUpdate();

        //generate Wi
        int sumaWi = 0;
        ArrayList<mxCell> vertexList = new ArrayList<mxCell>();
        for(int i=0; i<count; i++){
            int randValue = rand.nextInt(max+1);
            if( randValue < min ){ randValue=min; }
            sumaWi += randValue;
            mxCell cell = (mxCell)graph.insertVertex(graph.getDefaultParent(), null, "", 0, 0, 50,50,"TASK_CELL_STYLE");
            cell.setValue((Integer.parseInt(cell.getId())-1)+"\n"+randValue);
            vertexList.add(cell);
        }

        //generate Li
        double sumaLi = sumaWi/connectivity - sumaWi;
        int ELi = (int)Math.round(sumaLi);
        int maxLi = 1;
        if(ELi < 4){
            maxLi = ELi;
        }else{
            maxLi = ELi/4;
        }
        int minLi = 1;
        ArrayList<Integer> edgeList = new ArrayList<Integer>();
        while(ELi>0){
            int Li = rand.nextInt(maxLi) + 1;
            if(Li <= ELi){
                ELi -= Li;
                edgeList.add(Li);
            }else{
                edgeList.add(minLi);
            }
        }

        //insert edges
        while(edgeList.size() > 0){
            Integer edgeWeight = edgeList.get(0);
            int sourceIndex = rand.nextInt(vertexList.size());
            int targetIndex = rand.nextInt(vertexList.size());
            if(sourceIndex == targetIndex){
                continue;
            }
            if(hasLink(graph,vertexList.get(sourceIndex),vertexList.get(targetIndex))){
                edgeList.remove(0);
                mxCell tmpEdge = (mxCell) graph.getEdgesBetween(vertexList.get(sourceIndex), vertexList.get(targetIndex))[0];
                tmpEdge.setValue(""+(Integer.parseInt(tmpEdge.getValue().toString())+edgeWeight));
                continue;
            }
            Object edge = graph.insertEdge(graph.getDefaultParent(), null, edgeWeight, vertexList.get(sourceIndex), vertexList.get(targetIndex));
            if(hasCycle(graph)){
                graph.getModel().remove(edge);
                continue;
            }
            edgeList.remove(0);
        }
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
        layout.execute(graph.getDefaultParent());
        graph.getModel().endUpdate();
    }

    private static boolean hasLink(mxGraph graph,mxCell source, mxCell target){
        Object[]links = graph.getEdgesBetween(source,target);
        if(links.length==0){
            return false;
        }
        return true;
    }

    private static Object[] getParam(){
        JTextArea min = new JTextArea();
        min.setText(DEFAULT_MIN.toString());
        JTextArea max = new JTextArea();
        max.setText(DEFAULT_MAX.toString());
        JTextArea count = new JTextArea();
        count.setText(DEFAULT_COUNT.toString());
        JTextArea connectivity = new JTextArea();
        connectivity.setText(DEFAULT_CONNECTIVITY.toString());

        Object[] fields = {"min вага вершини графу",min,"max вага вершини графу",max,"кількість вершин графу",count,"зв’язність графу",connectivity};
        JOptionPane.showMessageDialog(null,fields,"Graph Param",JOptionPane.OK_CANCEL_OPTION);
        Integer minValue = null;
        Integer maxValue = null;
        Integer countValue = null;
        Double connectivityValue = null;
        try{
            minValue = Integer.parseInt(min.getText());
            maxValue = Integer.parseInt(max.getText());
            countValue = Integer.parseInt(count.getText());
            connectivityValue = Double.parseDouble(connectivity.getText());
        }catch (Exception e){
            minValue = DEFAULT_MIN;
            maxValue = DEFAULT_MAX;
            countValue = DEFAULT_COUNT;
            connectivityValue = DEFAULT_CONNECTIVITY;
        }
        return new Object[]{minValue,maxValue,countValue,connectivityValue};
    }

    public static boolean hasCycle(mxGraph graph){
        DefaultDirectedGraph<String, DefaultEdge> graphNoCycle = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
        graphNoCycle.getAllEdges(null,null);
        graph.clearSelection();
        graph.selectAll();
        Object[] cells = graph.getSelectionCells();
        for (Object c : cells) {
            mxCell cell = (mxCell) c;
            if (cell.isVertex()) {
                graphNoCycle.addVertex(cell.getValue().toString());
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
                String sValue = source.getValue().toString();
                String tValue = target.getValue().toString();
                graphNoCycle.addEdge(sValue, tValue);
            }
        }
        graph.clearSelection();
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(graphNoCycle);
        return cycleDetector.detectCycles();
    }

    public static boolean checkConnectivity(mxGraph graph){
        UndirectedGraph<String,DefaultEdge> graphCheck = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
        graph.clearSelection();
        graph.selectAll();
        Object[] cells = graph.getSelectionCells();
        for (Object c : cells) {
            mxCell cell = (mxCell) c;
            if (cell.isVertex()) {
                graphCheck.addVertex(cell.getValue().toString());
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
                String sValue = source.getValue().toString();
                String tValue = target.getValue().toString();
                graphCheck.addEdge(sValue, tValue);
            }
        }
        graph.clearSelection();
        ConnectivityInspector<String,DefaultEdge> conn = new ConnectivityInspector<String, DefaultEdge>(graphCheck);
        return conn.isGraphConnected();
    }

    public static void saveAsFileAction(mxGraph graph){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("mxe", "MXE"));
        fileChooser.setCurrentDirectory(new File("/"));
        int retVal = fileChooser.showSaveDialog(null);
        if(retVal == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            mxCodec codec = new mxCodec();
            String xml = mxXmlUtils.getXml(codec.encode(graph.getModel()));
            try {
                String fileName = file.getAbsolutePath();
                if(fileName.contains(".mxe") && fileName.substring(fileName.length()-4).equals(".mxe")){
                    mxUtils.writeFile(xml, file.getAbsolutePath());
                }else {
                    mxUtils.writeFile(xml, file.getAbsolutePath()+".mxe");
                }
                JOptionPane.showMessageDialog(null,"Save with success");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void openFileAction(mxGraph graph){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("mxe", "MXE"));
        fileChooser.setCurrentDirectory(new File("/"));
        int retVal = fileChooser.showOpenDialog(null);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String xml = mxUtils.readFile(file.getAbsolutePath());
                Document document = mxXmlUtils.parseXml(xml);
                mxCodec codec = new mxCodec(document);
                Object s = codec.decode(document.getDocumentElement(),graph.getModel());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void mouseReleasedAction(MouseEvent event,mxGraph graph,mxGraphComponent graphComponent,JPopupMenu popupPanel,boolean isTask){
        if(SwingUtilities.isRightMouseButton(event)){
            mxCell cell = (mxCell)graphComponent.getCellAt(event.getX(), event.getY());
            if (cell == null)
            {
                if(isTask){
                    TaskPanel.X = event.getX();
                    TaskPanel.Y = event.getY();
                }else{
                    SystemPanel.X = event.getX();
                    SystemPanel.Y = event.getY();
                }
                popupPanel.show(graphComponent,event.getX(), event.getY());
            }
        }
        if(SwingUtilities.isLeftMouseButton(event)){
            mxCell cell = (mxCell)graphComponent.getCellAt(event.getX(), event.getY());

            if(cell != null && cell.isVertex() && event.isShiftDown()){
                Object[] incomingEdges  = graph.getIncomingEdges(cell);
                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                for(Object edge : incomingEdges){
                    graph.getModel().remove(edge);
                }
                for(Object edge : outgoingEdges){
                    graph.getModel().remove(edge);
                }
                graph.getModel().remove(cell);
            }
            if(cell != null && cell.isEdge() && event.isShiftDown()){
                graph.getModel().remove(cell);
            }
        }
    }

    public static void mouseClickedAction(MouseEvent event,mxGraph graph,mxGraphComponent graphComponent, String vertexMessage,boolean isTaskGraph){
        if (event.getClickCount() == 2) {
            mxCell cell = (mxCell)graphComponent.getCellAt(event.getX(), event.getY());

            if (cell != null && cell.isVertex()){
                String input = JOptionPane.showInputDialog(null,vertexMessage);
                boolean isDigit = true;
                int value = -1;
                try{
                    value = Integer.parseInt(input);
                }catch(Exception e){
                    isDigit = false;
                }
                if(isDigit && value>=0){
                    if(isTaskGraph){
                        cell.setValue((Integer.parseInt(cell.getId())-1)+"\n"+value);
                    }else{
                        cell.setValue("id = "+(Integer.parseInt(cell.getId())-1)+"\n\n"+value);
                    }
                    graph.getView().clear(cell, false, false);
                    graph.getView().validate();
                }
            }
            if(cell != null && cell.isEdge()){
                String input = JOptionPane.showInputDialog(null,"Enter value");
                boolean isDigit = true;
                int value = -1;
                try{
                    value = Integer.parseInt(input);
                }catch(Exception e){
                    isDigit = false;
                }
                if(isDigit && value>=0){
                    cell.setValue(value);
                    graph.getView().clear(cell, false, false);
                    graph.getView().validate();
                }
            }

        }
    }
}
