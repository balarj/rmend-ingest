package com.brajagopal.rmend.data;

import com.brajagopal.rmend.dao.GCloudDao;
import com.brajagopal.rmend.dao.IRMendDao;
import com.brajagopal.rmend.data.beans.BaseContent;
import com.brajagopal.rmend.data.beans.DocumentBean;
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
 * @author <bxr4261>
 */
public class ContentProcessor {

    private static Logger logger = Logger.getLogger(ContentProcessor.class);
    private int fileCnt;
    private ContentDictionary dictionary = new ContentDictionary();
    private IRMendDao dao = GCloudDao.getLocalInstance();
    private int maxCnt = 150;

    private ContentProcessor() throws GeneralSecurityException, IOException {
        dao = GCloudDao.getLocalInstance();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException, DatastoreException {
        ContentProcessor processor = new ContentProcessor();
        try {
            if (args.length > 0) {
                File fIn = new File(args[0]);
                if (fIn.exists()) {
                    processor.processPath(fIn);
                } else {
                    throw new UnsupportedOperationException("Input argument is not a file.");
                }
            } else {
                throw new UnsupportedOperationException("Input file path not specified.");
            }
            logger.info("File count: " + processor.getFileCnt());
            //logger.info(processor.dictionary);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processPath(File _file) {
        if (_file.isFile()) {
            if (_file.getName().endsWith(".json")) {
                processFile(_file);
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
                    String key = entry.getKey();
                    if (entry.getValue() instanceof Map) {
                        Map<String, ? extends Object> entityValue = (Map<String, ? extends Object>) entry.getValue();
                        Class<? extends BaseContent> content = BaseContent.find(entityValue);
                        if (content != null) {
                            try {
                                BaseContent beanValue = content.newInstance().getInstance();
                                beanValue.process(entityValue);

                                if (beanValue instanceof DocumentBean) {
                                    documentBean = (DocumentBean) beanValue;
                                }
                                else {
                                    contentBeans.add(beanValue);
                                }

                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            logger.warn("Skipping processing for entity: " + entityValue.get(BaseContent.KEY_TYPEGROUP));
                        }
                    }
                }
                documentBean.setContentBeans(Collections.unmodifiableCollection(contentBeans));
                dictionary.putData(documentBean);
                logger.info(documentBean.getDocumentNumber());
            }
            logger.info(_file.getPath() + " : " + documentBean.getEntitySize());
            dao.putDocument(documentBean);
            fileCnt++;

        }
        catch (IOException e) {
            logger.error(e);
        } catch (DatastoreException e) {
            logger.error(e);
        }
    }

    public int getFileCnt() {
        return fileCnt;
    }
}
