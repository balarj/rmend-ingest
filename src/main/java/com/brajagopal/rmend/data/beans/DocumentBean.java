package com.brajagopal.rmend.data.beans;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;

/**
 * @author <bxr4261>
 */
public class DocumentBean extends BaseContent {

    private String docId;
    private String document;
    private String contentMD5Sum;

    @Override
    public void process(Map<String, ? extends Object> _value) {
        Map<String, String> infoValue = ((Map<String, String>)_value.get("info"));
        this.docId = infoValue.get("docId");
        this.document = infoValue.get("document");
        this.contentMD5Sum = DigestUtils.md5Hex(this.document);
    }

    @Override
    public BaseContent getInstance() {
        return new DocumentBean();
    }

    @Override
    public String toString() {
        return "DocumentBean {" +
                "docId='" + docId + '\'' +
                ", contentMD5Sum='" + contentMD5Sum + '\'' +
                '}';
    }

    public String getDocId() {
        return docId;
    }

    public String getDocument() {
        return document;
    }

    public String getContentMD5Sum() {
        return contentMD5Sum;
    }
}
