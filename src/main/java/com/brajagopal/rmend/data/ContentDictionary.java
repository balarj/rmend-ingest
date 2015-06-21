package com.brajagopal.rmend.data;

import com.brajagopal.rmend.data.beans.BaseContent;
import com.google.common.collect.HashMultimap;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @author <bxr4261>
 */
public class ContentDictionary {

    private final HashMultimap<String, String> dict;
    private static final String KEY_SEPARATOR = ":";

    public ContentDictionary() {
        dict = HashMultimap.create();
    }

    public void putData(BaseContent.ContentType _contentType, String _type, String _name, String _value) {

        String key = _contentType + KEY_SEPARATOR + _type  + KEY_SEPARATOR + _name;
        StringUtils.replace(key, KEY_SEPARATOR+KEY_SEPARATOR, KEY_SEPARATOR);
        dict.put(key, _value);
    }

    public Collection<String> getData(String _type, String _name) {
        String key = _type + KEY_SEPARATOR + _name;
        return dict.get(key);
    }

    public int size() {
        return dict.size();
    }

    @Override
    public String toString() {
        return "ContentDictionary{" +
                "dict=" + dict +
                '}';
    }
}
