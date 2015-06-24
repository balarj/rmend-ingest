package com.brajagopal.rmend.dao;

import com.brajagopal.rmend.data.beans.DocumentBean;
import com.google.api.services.datastore.client.DatastoreException;

/**
 * @author <bxr4261>
 */
public interface IRMendDao {

    public void putDocument(DocumentBean _docBean) throws DatastoreException;
    public void putDocument(DocumentBean _docBean, String _identifier) throws DatastoreException;

    public DocumentBean getDocument(Long _documentNumber) throws DatastoreException;
}
