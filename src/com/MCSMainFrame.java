package com;

import com.grapheditor.SystemPanel;
import com.grapheditor.TaskPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrew on 08.03.2015.
 */
public class MCSMainFrame {
    private JFrame frame;
    private JTabbedPane tabPane;
    public MCSMainFrame(){
        frame = new JFrame("MCSMainFrame");
        tabPane = new JTabbedPane();
        tabPane.add(new TaskPanel(tabPane));
        tabPane.add(new SystemPanel());
        tabPane.setTitleAt(0, "    Task Graph    ");
        tabPane.setTitleAt(1, "    System Graph    ");
    }
    public void launchFrame() {
        //init frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 500));
        frame.pack();
        frame.add(tabPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MCSMainFrame editor = new MCSMainFrame();
        editor.launchFrame();
    }
}
