package com.grapheditor;

import com.analyze.tasks.TaskAnalyzeManager;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Andrew on 08.03.2015.
 */
public class TaskGraphPanel extends JPanel {
    public static mxGraph graph;
    public static mxGraphComponent graphPanel;
    public static int X;
    public static int Y;
    public static JPopupMenu popupPanel;

    public TaskGraphPanel(JTabbedPane tabPane){
        //popupPanel
        popupPanel = new JPopupMenu();
        JMenuItem taskItem;
        JMenuItem openItem;
        JMenuItem saveItem;
        JMenuItem hasCycleItem;
        JMenuItem generateItem;
        JMenuItem queueWeightItem;
        JMenuItem queueInverseCriticapPathItem;
        JMenuItem queueNormalCriticapPathItem;
        popupPanel.add(taskItem = new JMenuItem("Add Task"));
        popupPanel.addSeparator();
        popupPanel.add(openItem = new JMenuItem("Open"));
        popupPanel.addSeparator();
        popupPanel.add(saveItem = new JMenuItem("Save as"));
        popupPanel.addSeparator();
        popupPanel.add(hasCycleItem = new JMenuItem("has Cycle ?"));
        popupPanel.addSeparator();
        popupPanel.add(generateItem = new JMenuItem("Generate graph"));
        popupPanel.addSeparator();
        popupPanel.add(queueWeightItem = new JMenuItem("Queue: weight order"));
        popupPanel.addSeparator();
        popupPanel.add(queueInverseCriticapPathItem = new JMenuItem("Queue: inverse critical path order"));
        popupPanel.addSeparator();
        popupPanel.add(queueNormalCriticapPathItem = new JMenuItem("Queue: critical path order with normalization"));

        generateItem.addActionListener(new ActionListener() {
            private JTabbedPane tabPane;

            @Override
            public void actionPerformed(ActionEvent e) {
                ActionManager.generateGraphAction(tabPane);
            }

            public ActionListener setTabPane(JTabbedPane tabPane) {
                this.tabPane = tabPane;
                return this;
            }
        }.setTabPane(tabPane));

        taskItem.addActionListener(new ActionListener() {
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

        hasCycleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean hasCycle = ActionManager.hasCycle(graph);
                String message = "has cycle: ";
                JOptionPane.showMessageDialog(null, message + hasCycle);
            }
        });
        queueWeightItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(TaskAnalyzeManager.getWeightOrderQueue(graph));
            }
        });
        queueInverseCriticapPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(TaskAnalyzeManager.getInverseCriticalPathOrderQueue(graph));
            }
        });
        queueNormalCriticapPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(TaskAnalyzeManager.getCriticalPathNormalizationOrderQueue(graph));
            }
        });

        init(this);
    }

    public  static void init( TaskGraphPanel taskGraph ){
        //init graph
        graph = new mxGraph();
        graphPanel = new mxGraphComponent(graph);
        graph.setAllowDanglingEdges(false);
        graphPanel.getGraphControl().addMouseListener(new TaskGraphMouselistener(graphPanel, popupPanel));
        taskGraph.setLayout(new BorderLayout());
        taskGraph.add(graphPanel, BorderLayout.CENTER);

        buildGraphEnvironment();
    }

    public static void buildGraphEnvironment() {
        try  {
            String nodeXMLTaskNode = mxUtils.readFile("D:\\ideaworkspace\\ModelingCS\\shapes_style\\circle.shape");
            addStencilShape(nodeXMLTaskNode);
        }
        catch(IOException e) {
            System.out.println("IOException: " + e);
        }
        // stylesheet
        mxStylesheet stylesheet = graph.getStylesheet();
        Hashtable<String, Object> style = new Hashtable<String, Object>();
        style.put(mxConstants.STYLE_RESIZABLE, false);
        style.put(mxConstants.STYLE_SHAPE, "Geometric - Perfect Circle");
        style.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ROUNDED, true);
        stylesheet.putCellStyle("TASK_CELL_STYLE", style);
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
            mxCell cell = (mxCell)graph.insertVertex(graph.getDefaultParent(), null, "", X, Y, 50,50,"TASK_CELL_STYLE");
            cell.setValue((Integer.parseInt(cell.getId())-1)+"\n"+"1");
        }
        finally {
            graph.getModel().endUpdate();
        }
    }


    private static class TaskGraphMouselistener extends MouseAdapter{

        private mxGraphComponent graphComponent;
        private JPopupMenu popupPanel;

        TaskGraphMouselistener(mxGraphComponent graphComponent, JPopupMenu popupPanel){
            this.graphComponent = graphComponent;
            this.popupPanel = popupPanel;
        }

        public void mouseClicked(MouseEvent event) {
            ActionManager.mouseClickedAction(event,graph,graphComponent,"Enter tasks value",true);
        }

        public void mouseReleased(MouseEvent event){
            ActionManager.mouseReleasedAction(event,graph,graphComponent,popupPanel,true);
        }
    }


}
