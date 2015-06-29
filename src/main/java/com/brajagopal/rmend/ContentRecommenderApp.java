package com.brajagopal.rmend;

import com.brajagopal.rmend.dao.GCloudDao;
import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.brajagopal.rmend.exception.DatastoreExceptionManager;
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

    private static Logger logger = Logger.getLogger(ContentProcessor.class);
    private static IRMendDao dao;

    private DatastoreExceptionManager datastoreExceptionManager = new DatastoreExceptionManager();

    public ContentRecommenderApp() throws GeneralSecurityException, IOException {
        dao = GCloudDao.getLocalInstance();
    }

    public static void main(String[] args) throws DatastoreException, GeneralSecurityException, IOException {
        ContentRecommenderApp app = new ContentRecommenderApp();
        logger.info(dao.getDocument(2330894543l).getContentBeansByType());
        TreeMultimap<String, DocumentMeta> entityValues = dao.getEntityMeta(Arrays.asList("ENTITIES:City:athens"));
        logger.info(entityValues);
        logger.info(dao.getDocument(2330894543l).getRelevantBeans());
    }
}
