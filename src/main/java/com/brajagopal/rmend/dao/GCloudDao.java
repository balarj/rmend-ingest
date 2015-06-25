package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.brajagopal.rmend.utils.JsonUtils;
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
@SuppressWarnings("unused")
public class GCloudDao implements IRMendDao {

    private static final Logger logger = Logger.getLogger(GCloudDao.class);
    private final Datastore datastore;
    private int batchSize;

    static final String DOCUMENT_KIND = "document";
    static final String DOCUMENT_MD5SUM_KIND = "md5sum";
    static final String DOCUMENT_TITLE_KIND = "title";
    static final String DOCUMENT_NUMBER_KIND = "docNumber";
    static final String DOCUMENT_JSON_KIND = "docBody";
    static final String DOCUMENT_TOPIC_KIND = "topic";
    static final String KEY_PROPERTY = "__key__";

    static final String ENTITY_KIND = "entity";
    static final String DOCUMENT_SCORE_KIND = "score";
    static final String ENTITIY_CLASSIFIER_KIND = "entityId";
    static final String DOCUMENT_META_KIND = "docMeta";

    private GCloudDao(boolean _isLocal) throws GeneralSecurityException, IOException {
        this(_isLocal, 1);
    }

    private GCloudDao(boolean _isLocal, int _batchSize) throws GeneralSecurityException, IOException {
        batchSize = _batchSize;
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

    public static GCloudDao getLocalInstance(int _batchSize) throws GeneralSecurityException, IOException {
        return new GCloudDao(true, _batchSize);
    }

    @Override
    public void putDocument(DocumentBean _docBean) throws DatastoreException {
        putDocument(_docBean, _docBean.getDocId());
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
                .addProperty(DatastoreHelper.makeProperty(DOCUMENT_JSON_KIND, DatastoreHelper.makeValue(JsonUtils.getGsonInstance().toJson(_docBean)).setIndexed(false)))
                .build();

        CommitRequest request = CommitRequest.newBuilder()
                .setMode(CommitRequest.Mode.NON_TRANSACTIONAL)
                .setMutation(Mutation.newBuilder().addInsert(article))
                .build();

        datastore.commit(request);
    }

    @Override
    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException {
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
        else if (documents.size() == 1) {
            Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(documents.get(0));
            List<Value> topicValues = DatastoreHelper.getList(propertyMap.get(DOCUMENT_TOPIC_KIND));
            Collection<String> topics = Lists.transform(topicValues, new Function<Value, String>() {
                @Nullable
                @Override
                public String apply(Value value) {
                    return DatastoreHelper.getString(value);
                }
            });

            return JsonUtils.getGsonInstance().fromJson(DatastoreHelper.getString(propertyMap.get(DOCUMENT_JSON_KIND)), DocumentBean.class);
        }
        return null;
    }

    @Override
    public void putEntityMeta(Collection<Map.Entry<String , DocumentMeta>> _docMetaCollection) throws DatastoreException {
        int ctr = 0;
        Mutation.Builder builder = Mutation.newBuilder();
        for (Map.Entry<String, DocumentMeta> entry : _docMetaCollection) {
            String _entityIdentifier = entry.getKey();
            DocumentMeta _docMeta = entry.getValue();
            String identifier = _entityIdentifier + ContentDictionary.KEY_SEPARATOR + _docMeta.getDocId();
            Entity article = Entity.newBuilder()
                    .setKey(DatastoreHelper.makeKey(ENTITY_KIND, identifier))
                    .addProperty(DatastoreHelper.makeProperty(ENTITIY_CLASSIFIER_KIND, DatastoreHelper.makeValue(_entityIdentifier)))
                    .addProperty(DatastoreHelper.makeProperty(DOCUMENT_SCORE_KIND, DatastoreHelper.makeValue(_docMeta.getScore())))
                    .addProperty(DatastoreHelper.makeProperty(DOCUMENT_META_KIND, DatastoreHelper.makeValue(JsonUtils.getGsonInstance().toJson(_docMeta)).setIndexed(false)))
                    .build();

            builder.addInsert(article);

            if ((ctr++ % batchSize) == 0) {
                try {
                    persist(builder);
                }
                catch (DatastoreException e) {
                    logger.warn("Failed to persist : "+ identifier + " (" + e.getMessage() + ")");
                }
                finally {
                    builder = Mutation.newBuilder();
                }
            }
        }
    }

    @Override
    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier) throws DatastoreException {
        Collection<DocumentMeta> retVal = null;
        Query.Builder query = Query.newBuilder();
        query.addKindBuilder().setName(ENTITY_KIND);
        query.setFilter(DatastoreHelper.makeFilter(
                ENTITIY_CLASSIFIER_KIND,
                PropertyFilter.Operator.EQUAL,
                DatastoreHelper.makeValue(_metaIdentifier)));
        List<Entity> entityMetadata = runQuery(query.build());
        if (entityMetadata.size() == 0) {
            logger.warn("No Metadata found for EntityId: " + _metaIdentifier);
        }
        else if (entityMetadata.size() > 0) {
            retVal = new ArrayList<DocumentMeta>(entityMetadata.size());
            for (Entity entity : entityMetadata) {
                Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(entity);
                retVal.add(JsonUtils.getGsonInstance().fromJson(DatastoreHelper.getString(propertyMap.get(DOCUMENT_META_KIND)), DocumentMeta.class));
            }
        }
        return retVal;
    }

    @Override
    public Map<String, Collection<DocumentMeta>> getEntityMeta(Collection<String> _metaIdentifier) throws DatastoreException {
        return null;
    }

    private void persist(Mutation.Builder _builder) throws DatastoreException {
        CommitRequest request = CommitRequest.newBuilder()
                .setMode(CommitRequest.Mode.NON_TRANSACTIONAL)
                .setMutation(_builder)
                .build();

        datastore.commit(request);
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
