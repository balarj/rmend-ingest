package com.brajagopal.rmend.recommender;

import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.TopicBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.api.services.datastore.client.DatastoreException;
import org.apache.commons.collections4.CollectionUtils;

import java.io.InvalidClassException;
import java.util.*;

/**
 * @author <bxr4261>
 */
public class ContentRecommender {

    private IRMendDao dao;

    public ContentRecommender(IRMendDao _dao) {
        this.dao = _dao;
    }

    /**
     * Method to retrieve Document (content), filtered by topic
     * @return
     */
    public Collection<DocumentMeta> getContentByTopic(TopicBean _topicBean, ResultsType resultsType) throws DatastoreException {

        Collection<DocumentMeta> entityValues = dao.getEntityMeta(ContentDictionary.makeKey(_topicBean));
        return ResultsType.getResults(entityValues, resultsType);
    }

    public Collection<DocumentMeta> getSimilarContent(long _documentNumber) {
        return CollectionUtils.EMPTY_COLLECTION;
    }

    public static enum ResultsType {
        RANDOM_10,
        TOP_10,
        TOP_5,
        TOP_3,
        TOP_1,
        ALL;

        public static Collection<DocumentMeta> getResults(Collection<DocumentMeta> _input, ResultsType _type) {
            if(_input == null || _input.isEmpty()) {
                return CollectionUtils.EMPTY_COLLECTION;
            }

            final List<DocumentMeta> value = new ArrayList<DocumentMeta>(_input);
            switch (_type){
                case RANDOM_10:
                    if (value.size() > 10) {
                        Collections.shuffle(value);
                        return value.subList(0, 10);
                    }
                    return value;
                case TOP_10:
                    if (value.size() > 10) {
                        Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                        return value.subList(0, 10);
                    }
                    return value;
                case TOP_5:
                    if (value.size() > 5) {
                        Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                        return value.subList(0, 5);
                    }
                    return value;
                case TOP_3:
                    if (value.size() > 3) {
                        Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                        return value.subList(0, 3);
                    }
                    return value;
                case ALL:
                    Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                    return value;
                case TOP_1:
                default:
                    Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                    return value.subList(0, 1);
            }
        }
    }

    public static TopicBean makeTopicBean(String _topic) throws IllegalAccessException, InvalidClassException, InstantiationException {
        Map<String, String> beanValues = new HashMap<String, String>();
        beanValues.put("contentType", BaseContent.ContentType.TOPICS.toString());
        beanValues.put("name", _topic);
        BaseContent retVal = BaseContent.getChildInstance(beanValues);
        if (retVal != null && retVal instanceof TopicBean) {
            return (TopicBean) retVal;
        }
        else {
            throw new RuntimeException("Unable to generate a TopicBean for the topic: "+_topic);
        }
    }
}
