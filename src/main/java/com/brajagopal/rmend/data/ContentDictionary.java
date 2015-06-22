package com.brajagopal.rmend.data;

import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.beans.RelationsBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <bxr4261>
 */
public class ContentDictionary {

    private final SetMultimap<String, DocumentMeta> dict;
    private static final String KEY_SEPARATOR = ":";
    private static final Logger logger = Logger.getLogger(ContentDictionary.class);

    public ContentDictionary() {
        dict = HashMultimap.create();

    }

    public void putData(DocumentBean _documentBean) {

        for (BaseContent _contentBean : _documentBean.getContentBeans()) {
            // Skip RelationsBean
            if (_contentBean instanceof RelationsBean) {
                continue;
            }

            String beanType = "";
            String beanName = "";

            try {
                beanType = _contentBean.getType();
            }
            catch (UnsupportedOperationException e) {}
            try {
                beanName = _contentBean.getName();
            }
            catch (UnsupportedOperationException e) {}

            String key = StringUtils.join(Arrays.asList(_contentBean.getContentType(), beanType, beanName), KEY_SEPARATOR);
            dict.put(key, DocumentMeta.createInstance(_documentBean.getDocumentNumber(), _documentBean.getDocId(), _contentBean.getScore()));
        }
    }

    public Collection<DocumentMeta> getData(BaseContent.ContentType _contentType, String _type, String _name) {
        Collection<DocumentMeta> retVal;
        String key = _contentType + KEY_SEPARATOR + _type  + KEY_SEPARATOR + _name;
        StringUtils.replace(key, KEY_SEPARATOR+KEY_SEPARATOR, KEY_SEPARATOR);
        retVal = new TreeList<DocumentMeta>(dict.get(key));
        return retVal;
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
