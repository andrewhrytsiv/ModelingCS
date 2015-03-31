package com.analyze;

/**
 * Created by Andrew on 31.03.2015.
 */
public class Task implements Comparable<Task>{
    private Integer id;

    private Integer weight;

    public Task(Integer id, Integer weight){
        this.id = id;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "{id=" + id + " : w=" + weight +'}';
    }

    @Override
    public int compareTo(Task obj) {
        return weight - obj.getWeight();
    }
}
