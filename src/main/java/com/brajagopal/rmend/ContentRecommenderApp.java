package com.brajagopal.rmend;

import com.brajagopal.rmend.dao.GCloudDao;
import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.ResultsType;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.brajagopal.rmend.exception.DatastoreExceptionManager;
import com.brajagopal.rmend.exception.DocumentNotFoundException;
import com.brajagopal.rmend.recommender.ContentRecommender;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

/**
 * @author <bxr4261>
 */
public class ContentRecommenderApp {

    private static Logger logger = Logger.getLogger(ContentRecommenderApp.class);
    private static IRMendDao dao;
    private ContentRecommender contentRecommender;

    private DatastoreExceptionManager datastoreExceptionManager = new DatastoreExceptionManager();

    public ContentRecommenderApp() throws GeneralSecurityException, IOException {
        dao = GCloudDao.getLocalInstance();
        contentRecommender = new ContentRecommender(dao);
    }

    public ContentRecommender getRecommender() {
        return contentRecommender;
    }

    public static void main(String[] args) throws DatastoreException, GeneralSecurityException, IOException {
        ContentRecommenderApp app = new ContentRecommenderApp();
        //logger.info(dao.getDocument(2330894543l).getContentBeansByType());
        TreeMultimap<BaseContent.ContentType, DocumentMeta> entityValues = dao.getEntityMeta(Arrays.asList("ENTITIES:Person:joachim_johansson"));
        //logger.info(entityValues);
        //logger.info(dao.getDocument(2423267037l).getRelevantBeans());
        try {
            logger.info(app.getRecommender().getContentByTopic(ContentRecommender.makeTopicBean("human_interest"), ResultsType.TOP_3));
            logger.info(dao.getDocument(2423265542l));
            logger.info(app.getRecommender().getSimilarContent(2423265542l, ResultsType.TOP_5));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (DocumentNotFoundException e) {
            e.printStackTrace();
        }
    }
}
