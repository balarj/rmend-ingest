package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class EntitiesBean extends BaseContent {

    private double relevance;

    public EntitiesBean() {
        this(ContentType.ENTITIES);
    }

    private EntitiesBean(ContentType _contentType) {
        super(_contentType);
    }

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.type = MapUtils.getString(_value, "_type", null);
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.relevance = MapUtils.getDouble(_value, "relevance", 0.0);
        this.name = MapUtils.getString(_value, "name", null);
    }

    @Override
    public BaseContent getInstance() {
        return new EntitiesBean();
    }

    @Override
    public String toString() {
        return "EntitiesBean {" +
                "contentType=" + getContentType() +
                ", type='" + getType() + '\'' +
                ", forEndUserDisplay=" + isForEndUserDisplay() +
                ", name='" + getName() + '\'' +
                ", score=" + getScore() +
                '}';
    }

    public double getRelevance() {
        return relevance;
    }

    @Override
    public double getScore() {
        return getRelevance();
    }
}
