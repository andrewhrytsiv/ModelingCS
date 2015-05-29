package com.analyze.processors;

/**
 * Created by Andrew on 29.05.2015.
 */
public class Processor {
    private int id;
    private int connectivity;


    public Processor(int id) {
        this.id = id;
    }
    public Processor(int id, int connectivity) {
        this.id = id;
        this.connectivity = connectivity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(int connectivity) {
        this.connectivity = connectivity;
    }

    @Override
    public String toString() {
        return "Processor{" +
                "id=" + id +
                '}';
    }
}
