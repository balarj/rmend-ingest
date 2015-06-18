package com.brajagopal.rmend.data.beans;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public abstract class BaseContent {
    public static final String KEY_TYPEGROUP = "_typeGroup";

    public static Class<? extends BaseContent> find(Map<String, ? extends Object> _value) {
        if (_value.containsKey(KEY_TYPEGROUP)) {
            ContentType contentType = ContentType.getInstance((String) _value.get(KEY_TYPEGROUP));
            if (contentType != null) {
                return contentType.getClassInstance();
            }
        }
        else if (_value.keySet().containsAll(Arrays.asList("info", "meta"))) { // document information
            return ContentType.DOCUMENT_INFO.getClassInstance();
        }
        return null;
    }

    public abstract void process(Map<String, ? extends Object> _value);
    public abstract BaseContent getInstance();

    public static enum ContentType {
        TOPICS("topics", TopicBean.class),
        SOCIAL_TAGS("socialTag", SocialTagBean.class),
        ENTITIES("entities", EntitiesBean.class),
        RELATIONS("relations", RelationsBean.class),
        DOCUMENT_INFO("document", DocumentBean.class),
        DISCARDED(StringUtils.EMPTY, null);

        private String name;
        private Class<? extends BaseContent> classInstance;

        private ContentType(String _typeGroup, Class<? extends BaseContent> _classInstance) {
            this.name = _typeGroup;
            this.classInstance = _classInstance;

        }

        public static ContentType getInstance(String _typeGroup) {
            for (ContentType value : values()) {
                if (value.getTypeGroup().equals(_typeGroup)) {
                    return value;
                }
            }
            return ContentType.DISCARDED;
        }

        public Class<? extends BaseContent> getClassInstance() {
            return classInstance;
        }

        public String getTypeGroup() {
            return name;
        }
    }
}
