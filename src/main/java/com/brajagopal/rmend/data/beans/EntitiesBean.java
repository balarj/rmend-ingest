package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class EntitiesBean extends BaseContent {

    private final ContentType contentType = ContentType.ENTITIES;
    private String type;
    private boolean forEndUserDisplay;
    private String name;
    private double relevance;

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
                "contentType=" + contentType +
                ", type='" + type + '\'' +
                ", forEndUserDisplay=" + forEndUserDisplay +
                ", name='" + name + '\'' +
                ", relevance=" + relevance +
                '}';
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getType() {
        return type;
    }

    public boolean isForEndUserDisplay() {
        return forEndUserDisplay;
    }

    public String getName() {
        return name;
    }

    public double getRelevance() {
        return relevance;
    }
}
