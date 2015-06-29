package com.brajagopal.rmend.recommender;

import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.beans.TopicBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.api.services.datastore.client.DatastoreException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public Collection<DocumentMeta> getContentByTopic(TopicBean _topicBean, int _numResults, ResultsType resultsType) throws DatastoreException {

        Collection<DocumentMeta> entityValues = dao.getEntityMeta(ContentDictionary.makeKey(_topicBean));
        return ResultsType.getResults(entityValues, resultsType);
    }

    public static enum ResultsType {
        RANDOM_10,
        TOP_10,
        ALL;

        public static Collection<DocumentMeta> getResults(Collection<DocumentMeta> _input, ResultsType _type) {
            final List<DocumentMeta> value = new ArrayList<DocumentMeta>(_input);
            if (_type == RANDOM_10) {
                Collections.shuffle(value);
                return value.subList(0, 10);
            }
            else if (_type == TOP_10) {
                Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                return value.subList(0, 10);
            }
            else if (_type == ALL) {
                Collections.sort(value, DocumentMeta.DOCUMENT_META_COMPARATOR);
                return value;
            }
            return null;
        }
    }
}
