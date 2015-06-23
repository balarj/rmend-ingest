package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.beans.DocumentBean;
import com.google.api.services.datastore.DatastoreV1.*;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class GCloudDao implements IRMendDao {

    private static final Logger logger = Logger.getLogger(GCloudDao.class);
    private final Datastore datastore;

    static final String DOCUMENT_KIND = "document";
    static final String DOCUMENT_MD5SUM_KIND = "md5sum";
    static final String DOCUMENT_TITLE_KIND = "title";
    static final String DOCUMENT_NUMBER_KIND = "docNumber";
    static final String DOCUMENT_BODY_KIND = "docBody";
    static final String DOCUMENT_TOPIC_KIND = "topic";
    static final String KEY_PROPERTY = "__key__";

    private GCloudDao(boolean _isLocal) throws GeneralSecurityException, IOException {
        if (_isLocal) {
            datastore = DatastoreHelper.getDatastoreFromEnv();
        }
        else {
            datastore = null;
        }
    }

    public static GCloudDao getLocalInstance() throws GeneralSecurityException, IOException {
        return new GCloudDao(true);
    }

    @Override
    public void putDocument(DocumentBean _docBean) throws DatastoreException {
        putDocument(_docBean, _docBean.getContentMD5Sum());
    }

    @Override
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException {
        Collection<Value> topicValues = new ArrayList<Value>();
        for (String topic : _docBean.getTopic()) {
            topicValues.add(DatastoreHelper.makeValue(topic).build());
        }

        Entity article = Entity.newBuilder()
                .setKey(DatastoreHelper.makeKey(DOCUMENT_KIND, _identifier))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_MD5SUM_KIND, DatastoreHelper.makeValue(_docBean.getContentMD5Sum()).setIndexed(false)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_TITLE_KIND, DatastoreHelper.makeValue(_docBean.getTitle()).setIndexed(false)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_TOPIC_KIND, DatastoreHelper.makeValue(topicValues)))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_NUMBER_KIND, DatastoreHelper.makeValue(_docBean.getDocumentNumber())))
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_BODY_KIND, DatastoreHelper.makeValue(_docBean.getDocument()).setIndexed(false)))
                .build();

        CommitRequest request = CommitRequest.newBuilder()
                .setMode(CommitRequest.Mode.NON_TRANSACTIONAL)
                .setMutation(Mutation.newBuilder().addInsert(article))
                .build();

        datastore.commit(request);
    }

    @Override
    public void getDocument(Long _documentNumber) throws DatastoreException {
        Query.Builder query = Query.newBuilder();
        query.addKindBuilder().setName(DOCUMENT_KIND);
        query.setFilter(DatastoreHelper.makeFilter(
                DOCUMENT_NUMBER_KIND,
                PropertyFilter.Operator.EQUAL,
                DatastoreHelper.makeValue(_documentNumber)));
        List<Entity> documents = runQuery(query.build());
        if (documents.size() == 0) {
            logger.warn("No Document found for DocumentNumber: " + _documentNumber);
        }
        for (Entity document : documents) {
            Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(document);
            List<Value> topicValues = DatastoreHelper.getList(propertyMap.get(DOCUMENT_TOPIC_KIND));
            Collection<String> topics = Lists.transform(topicValues, new Function<Value, String>() {
                @Nullable
                @Override
                public String apply(Value value) {
                    return DatastoreHelper.getString(value);
                }
            });

            logger.info(
                    DatastoreHelper.getLong(propertyMap.get(DOCUMENT_NUMBER_KIND)) + ": " +
                            DatastoreHelper.getString(propertyMap.get(DOCUMENT_TITLE_KIND)) + " - " +
                            topics);
        }
    }

    private List<Entity> runQuery(Query query) throws DatastoreException {
        RunQueryRequest.Builder request = RunQueryRequest.newBuilder();
        request.setQuery(query);
        RunQueryResponse response = datastore.runQuery(request.build());

        if (response.getBatch().getMoreResults() == QueryResultBatch.MoreResultsType.NOT_FINISHED) {
            System.err.println("WARNING: partial results\n");
        }
        List<EntityResult> results = response.getBatch().getEntityResultList();
        List<Entity> entities = new ArrayList<Entity>(results.size());
        for (EntityResult result : results) {
            entities.add(result.getEntity());
        }
        return entities;
    }

}
