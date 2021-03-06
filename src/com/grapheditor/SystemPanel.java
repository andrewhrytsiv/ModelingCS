package com.grapheditor;

import com.analyze.processors.SystemGraph;
import com.analyze.tasks.DirectedAcyclicGraph;
import com.analyze.tasks.Task;
import com.analyze.tasks.TaskAnalyzeManager;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.scheduler.Config;
import com.scheduler.SchedulerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by Andrew on 21.03.2015.
 */
public class SystemPanel extends JPanel {
    static mxGraph graph;
    static mxGraphComponent graphPanel;
    static int X;
    static int Y;
    static JPopupMenu popupPanel;

    public SystemPanel(){
        //popupPanel
        popupPanel = new JPopupMenu();
        JMenuItem sysItem;
        JMenuItem openItem;
        JMenuItem saveItem;
        JMenuItem checkItem;
        JMenuItem schedulerItem;
        popupPanel.add(sysItem = new JMenuItem("Add System"));
        popupPanel.addSeparator();
        popupPanel.add(openItem = new JMenuItem("Open"));
        popupPanel.addSeparator();
        popupPanel.add(saveItem = new JMenuItem("Save as"));
        popupPanel.addSeparator();
        popupPanel.add(checkItem = new JMenuItem("Check connectivity"));
        popupPanel.addSeparator();
        popupPanel.add(schedulerItem = new JMenuItem("Scheduler"));

        checkItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "connectivity: " + ActionManager.checkConnectivity(graph));
            }
        });

        sysItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVertex();
            }
        });
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.openFileAction(graph);
            }
        });
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.saveAsFileAction(graph);
            }
        });
        schedulerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] initParam = SchedulerManager.getParam();
                Integer physLinks = (Integer)initParam[2] + 1;
                Config duplexMode = (Config)initParam[0];
                System.out.println(physLinks);
                System.out.println(duplexMode);
                DirectedAcyclicGraph dag = TaskAnalyzeManager.getDAG(TaskPanel.graph);
                //System.out.println(dag.vertexMap);
                //System.out.println(dag.edgeMap);
                List<Task> taskQueue = SchedulerManager.getTaskQueue(initParam);
                //System.out.println(taskQueue);
                SystemGraph system = SystemGraph.getSystemGraph(SystemPanel.graph);
                SchedulerManager.buildJSON(dag.getValidationIdMap(), dag, taskQueue,system, system.getValidationIdMap(),physLinks, duplexMode );
            }
        });

        init(this);
    }

    public  static void init(SystemPanel systemgraph){
        //init graph
        graph = new mxGraph();
        graphPanel = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);
        graphPanel.getGraphControl().addMouseListener(new SystemGraphMouselistener(graphPanel,popupPanel));
        systemgraph.setLayout(new BorderLayout());
        systemgraph.add(graphPanel, BorderLayout.CENTER);

        buildGraphEnvironment();
    }

    public static void buildGraphEnvironment() {
        try  {
            String nodeXMLSystemNode = mxUtils.readFile("D:\\ideaworkspace\\ModelingCS\\shapes_style\\expended-node.shape");
            addStencilShape(nodeXMLSystemNode);
        }
        catch(IOException ioe) {
            System.out.println("IOException: " + ioe);
        }
        // stylesheet
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_RESIZABLE, false);
        style.put(mxConstants.STYLE_SHAPE, "Misc - expended node");
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ENDARROW,mxConstants.NONE);
        stylesheet.putCellStyle("SYSTEM_CELL_STYLE", style);
        graph.setStylesheet(stylesheet);
        graph.setAllowLoops(false);
        graph.setAllowDanglingEdges(false);
        graph.setLabelsClipped(true);
        graph.setCellsEditable(false);
        graph.setCellsCloneable(false);
    }

    private static void addStencilShape(String nodeXML){
        int lessthanIndex = nodeXML.indexOf("<");
        nodeXML = nodeXML.substring(lessthanIndex);
        mxStencilShape newShape = new mxStencilShape(nodeXML);
        mxGraphics2DCanvas.putShape(newShape.getName(), newShape);
    }

    public static void addVertex(){
        graph.getModel().beginUpdate();
        try {
            mxCell cell = (mxCell)graph.insertVertex(graph.getDefaultParent(), null, "", X, Y, 70,70,"SYSTEM_CELL_STYLE");
            cell.setValue("id = "+(Integer.parseInt(cell.getId())-1)+"\n\n"+"1");
        }
        finally {
            graph.getModel().endUpdate();
        }
    }

    private static class SystemGraphMouselistener extends MouseAdapter {
        private mxGraphComponent graphComponent;
        private JPopupMenu popupPanel;

        SystemGraphMouselistener(mxGraphComponent graphComponent, JPopupMenu popupPanel){
            this.graphComponent = graphComponent;
            this.popupPanel = popupPanel;
        }
        public void mouseClicked(MouseEvent event) {
            ActionManager.mouseClickedAction(event,graph,graphComponent,"Enter system productivity",false);
        }

        public void mouseReleased(MouseEvent event){
            ActionManager.mouseReleasedAction(event,graph,graphComponent,popupPanel,false);
        }
    }
}
