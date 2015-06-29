package com.brajagopal.rmend.enrich;

import com.brajagopal.rmend.config.RMendConfig;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.*;

/**
 * @author <bxr4261>
 */
public class CalaisClient {

    private RMendConfig config;
    private File inPath;
    private File outPath;
    private CloseableHttpClient httpclient;

    private static Logger logger = Logger.getLogger(CalaisClient.class);

    public CalaisClient() throws IOException {
        this(new File("input/"), new File("output/"));
    }

    public CalaisClient(File input, File output) throws IOException {
        config = new RMendConfig();
        httpclient = HttpClients.createDefault();
        inPath = input;
        outPath = output;
    }

    private HttpPost createRequestBaseline() {

        HttpPost postMethod = new HttpPost(config.getApiURL());

        // Set Request Headers
        // Set access token
        postMethod.addHeader("x-ag-access-token", config.getApiToken());

        // Set response outPath format
        postMethod.setHeader("outputFormat", config.getResponseType());

        // Enable Social Tags processing
        postMethod.setHeader("enableMetadataType", config.getMetaDataType());

        // Set inPath content type
        postMethod.setHeader(HttpHeaders.CONTENT_TYPE, "text/raw");

        // Set the content language
        postMethod.setHeader("x-calais-language", "English");

        return postMethod;
    }

    public void run() {
        try {
            parse(inPath, null);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void parse(File path, File baseDir) {
        if (path.isDirectory()) {
            baseDir = (baseDir == null) ? outPath : new File(baseDir, path.getName());
            for (File file : path.listFiles()) {
                parse(file, baseDir);
            }
        }
        else {
            if (!path.getName().startsWith(".")) {
                String outFileName = path.getName().replace(".txt", ".json");

                logger.info(baseDir+"/"+outFileName);
                File outFile = new File(baseDir, outFileName);
                // Check if the file was already processed
                if (!outFile.exists()) {
                    try {
                        postFile(path, outFile, createRequestBaseline());
                        Thread.sleep(config.getDelayInMillis());
                    } catch (IOException e) {
                        logger.warn(e);
                    } catch (InterruptedException e) {
                        logger.warn(e);
                    }
                } else {
                    // Skip if we have already processed it
                    logger.warn("(<>) Skipping " + path.getAbsolutePath());
                }
            } else {
                logger.warn("(--) Skipping " + path.getAbsolutePath());
            }
        }
    }

    private void doRequest(HttpPost method, String fileName, File outFile) {
        try {
            CloseableHttpResponse response = httpclient.execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                parseResponse(outFile, response.getEntity());
            }
            else {
                logger.error("(*) POST failed: " + fileName);
                StringBuffer failureMessage = new StringBuffer();

                // Construct the failure message
                failureMessage.append("(")
                        .append(response.getStatusLine().getStatusCode())
                        .append(") ")
                        .append(response.getStatusLine().getReasonPhrase());
                logger.warn(failureMessage);

                // still consume the response body
                EntityUtils.consumeQuietly(response.getEntity());
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            method.releaseConnection();
        }
    }

    private void parseResponse(File outFile, HttpEntity httpEntity) throws IOException {
        PrintWriter writer = null;
        try {
            outFile.getParentFile().mkdirs();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            FileUtils.writeStringToFile(outFile, EntityUtils.toString(httpEntity));
            logger.info("(+) Processed: " + outFile.getName());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void postFile(File rawFile, File outFile, HttpPost method) throws IOException {
        method.setEntity(new FileEntity(rawFile));
        doRequest(method, rawFile.getName(), outFile);
    }
}
