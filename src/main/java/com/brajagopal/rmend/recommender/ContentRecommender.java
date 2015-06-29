package com.brajagopal.rmend.recommender;

import com.brajagopal.rmend.data.beans.DocumentBean;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * @author <bxr4261>
 */
public class ContentRecommender {

    /**
     * Method to retrieve Document (content), filtered by topic
     * @return
     */
    public Collection<DocumentBean> getContentByTopic(String _topic, int _numResults) {
        return CollectionUtils.emptyCollection();
    }
}
