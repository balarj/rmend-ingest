package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class TopicBean extends BaseContent {

    private String name;
    private double score;

    public TopicBean() {
        this(ContentType.TOPICS);
    }

    private TopicBean(ContentType _contentType) {
        super(_contentType);
    }

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.score = MapUtils.getDoubleValue(_value, "score", 0.0);
        this.name = MapUtils.getString(_value, "name", null);
    }

    @Override
    public BaseContent getInstance() {
        return new TopicBean();
    }

    @Override
    public String toString() {
        return "TopicBean {" +
                "contentType=" + contentType +
                ", forEndUserDisplay=" + forEndUserDisplay +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public Boolean isForEndUserDisplay() {
        return forEndUserDisplay;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }
}
