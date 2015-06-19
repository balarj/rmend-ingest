package com.brajagopal.rmend.data;

import com.brajagopal.rmend.data.beans.BaseContent;
import com.google.appengine.repackaged.com.google.gson.Gson;
import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class ContentProcessor {

    private static Logger logger = Logger.getLogger(ContentProcessor.class);
    private int fileCnt;

    public static void main(String[] args) {
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("File count: " + processor.getFileCnt());
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
                                if (beanValue.isForEndUserDisplay() == null || beanValue.isForEndUserDisplay()) {
                                    logger.info(key + ": " + beanValue);
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
            }
            logger.info(_file.getPath() + " : " + val.size());
            fileCnt++;

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getFileCnt() {
        return fileCnt;
    }
}
