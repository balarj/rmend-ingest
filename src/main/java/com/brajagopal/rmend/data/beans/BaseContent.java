package com.brajagopal.rmend.data.beans;

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
        return null;
    }

    public abstract void process(Map<String, ? extends Object> _value);
    public abstract BaseContent getInstance();

    public static enum ContentType {
        TOPICS("topics", null),
        SOCIAL_TAGS("socialTag", SocialTag.class),
        ENTITIES("entities", null),
        DISCARDED("", null);

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
