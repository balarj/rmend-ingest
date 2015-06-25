package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.data.meta.DocumentMeta;
import com.google.api.services.datastore.client.DatastoreException;

import java.util.Collection;
import java.util.Map;

/**
 * @author <bxr4261>
 */
@SuppressWarnings("unused")
public interface IRMendDao {

    // Document specific DAO methods
    public void putDocument(DocumentBean _docBean) throws DatastoreException;
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException;

    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException;

    // Entity specific DAO methods
    public void putEntityMeta(Collection<Map.Entry<String, DocumentMeta>> _docMetaCollection) throws DatastoreException;

    public Collection<DocumentMeta> getEntityMeta(String _metaIdentifier) throws DatastoreException;
    public Map<String, Collection<DocumentMeta>> getEntityMeta(Collection<String> _metaIdentifier) throws DatastoreException;

}
