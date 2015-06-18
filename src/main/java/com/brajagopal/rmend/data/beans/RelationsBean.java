package com.brajagopal.rmend.data.beans;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class RelationsBean extends BaseContent {

    private final ContentType contentType = ContentType.RELATIONS;
    private String type;
    private boolean forEndUserDisplay;
    private String careerType;
    private String status;

    @Override
    public void process(Map<String, ? extends Object> _value) {
        this.type = MapUtils.getString(_value, "_type", null);
        this.forEndUserDisplay = MapUtils.getBoolean(_value, "forenduserdisplay", false);
        this.careerType = MapUtils.getString(_value, "careertype", null);
        this.status = MapUtils.getString(_value, "status", null);
    }

    @Override
    public BaseContent getInstance() {
        return new RelationsBean();
    }

    @Override
    public String toString() {
        return "RelationsBean {" +
                "contentType=" + contentType +
                ", type='" + type + '\'' +
                ", forEndUserDisplay=" + forEndUserDisplay +
                ", careerType='" + careerType + '\'' +
                ", status='" + status + '\'' +
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

    public String getCareerType() {
        return careerType;
    }

    public String getStatus() {
        return status;
    }
}
