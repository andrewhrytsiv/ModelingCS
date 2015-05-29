package com.analyze.processors;

/**
 * Created by Andrew on 29.05.2015.
 */
public class LogicalLink {
    private Integer id;
    private Integer sourceId;
    private Integer targetId;

    public LogicalLink(Integer id, Integer sourceId, Integer targetId) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    @Override
    public String toString() {
        return "LogicalLink{" +
                "sId=" + sourceId +
                ", tId=" + targetId +
                '}';
    }
}
