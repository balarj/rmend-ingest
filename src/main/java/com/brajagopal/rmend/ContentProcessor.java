package com.brajagopal.rmend;

import com.brajagopal.rmend.dao.GCloudDao;
import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.ContentDictionary;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
import com.brajagopal.rmend.exception.DatastoreExceptionManager;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.gson.Gson;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Traverses through the content JSON file (from the input path), and loads the Document
 * and Entity details into the RMend DAO.
 *
 * @author <bxr4261>
 */
public class ContentProcessor {

    private static Logger logger = Logger.getLogger(ContentProcessor.class);
    private int fileCnt;
    private ContentDictionary dictionary = new ContentDictionary();
    private static IRMendDao dao;

    private DatastoreExceptionManager datastoreExceptionManager = new DatastoreExceptionManager();

    private ContentProcessor() throws GeneralSecurityException, IOException {
        dao = GCloudDao.getLocalInstance();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException, DatastoreException {
        ContentProcessor processor = new ContentProcessor();
        processor.process(args);
    }

    public void process(String[] args) {
        try {
            if (args.length > 0) {
                File fIn = new File(args[0]);
                if (fIn.exists()) {
                    processPath(fIn);
                } else {
                    throw new UnsupportedOperationException("Input argument is not a file.");
                }
            } else {
                throw new UnsupportedOperationException("Input file path not specified.");
            }
            logger.info("File count: " + getFileCnt());

            getDictionary().persistData(dao);
            logger.info(DatastoreExceptionManager.getValues());
        }
        catch (Exception e) {
            logger.error(e);
        }
    }

    public void processPath(File _file) throws InterruptedException {
        if (_file.isFile()) {
            if (_file.getName().endsWith(".json")) {
                processFile(_file);
                Thread.sleep(500l);
                logger.info("----------------------------------------------------------------------");
            }
        } else if (_file.isDirectory()) {
            for (File fileFromPath : _file.listFiles()) {
                processPath(fileFromPath);
            }
        } else {
            throw new UnsupportedOperationException("Input argument is not a file.");
        }
    }

    protected void processFile(File _file) {
        try {
            Collection<BaseContent> contentBeans = new ArrayList<BaseContent>();
            DocumentBean documentBean = null;
            Map<String, ? extends Object> val = new Gson().fromJson(new FileReader(_file), Map.class);
            if (MapUtils.isNotEmpty(val)) {
                for (Map.Entry<String, ? extends Object> entry : val.entrySet()) {
                    if (entry.getValue() instanceof Map) {
                        try {
                            BaseContent beanValue =
                                    BaseContent.getChildInstance((Map<String, ? extends Object>) entry.getValue());

                            if (beanValue instanceof DocumentBean) {
                                documentBean = (DocumentBean) beanValue;
                            }
                            else {
                                contentBeans.add(beanValue);
                            }

                        } catch (Exception e) {
                            logger.warn(e);
                        }
                    }
                }
                documentBean.setContentBeans(Collections.unmodifiableCollection(contentBeans));
                logger.info(documentBean.getDocumentNumber());
                logger.info(_file.getPath() + " : " + documentBean.getEntitySize());
                dao.putDocument(documentBean);
                dictionary.putData(documentBean);
            }
            fileCnt++;

        }
        catch (IOException e) {
            logger.error(e);
        } catch (DatastoreException e) {
            datastoreExceptionManager.trackException(e, "processFile()");
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    public int getFileCnt() {
        return fileCnt;
    }

    public ContentDictionary getDictionary() {
        return dictionary;
    }
}
