package com;

import com.grapheditor.SystemGraph;
import com.grapheditor.TaskGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrew on 08.03.2015.
 */
public class MCS{
    private JFrame frame;
    private JTabbedPane tabPane;
    public MCS(){
        frame = new JFrame("MCS");
        tabPane = new JTabbedPane();
        tabPane.add(new TaskGraph(tabPane));
        tabPane.add(new SystemGraph());
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
        MCS editor = new MCS();
        editor.launchFrame();
    }
}
