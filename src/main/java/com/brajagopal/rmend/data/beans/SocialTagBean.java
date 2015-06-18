package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class SocialTagBean extends BaseContent {

    private final ContentType contentType = ContentType.SOCIAL_TAGS;
    private boolean forEndUserDisplay;
    private String name;
    private int importance;

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.importance = MapUtils.getIntValue(_value, "importance", 0);
        this.name = MapUtils.getString(_value, "name", null);
    }

    @Override
    public BaseContent getInstance() {
        return new SocialTagBean();
    }

    @Override
    public String toString() {
        return "SocialTag {" +
                "contentType=" + contentType +
                ", forEndUserDisplay=" + forEndUserDisplay +
                ", name='" + name + '\'' +
                ", importance=" + importance +
                '}';
    }

    public ContentType getContentType() {
        return contentType;
    }

    public boolean isForEndUserDisplay() {
        return forEndUserDisplay;
    }

    public String getName() {
        return name;
    }

    public int getImportance() {
        return importance;
    }
}
