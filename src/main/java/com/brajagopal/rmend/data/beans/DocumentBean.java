package com.brajagopal.rmend.data.beans;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class DocumentBean extends BaseContent {

    private String docId;
    private String document;
    private String contentMD5Sum;
    private Collection<BaseContent> contentBeans;

    public DocumentBean() {
        this(ContentType.DOCUMENT_INFO);
    }

    private DocumentBean(ContentType _contentType) {
        super(_contentType);
    }

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
        return "DocumentBean{" +
                "docId='" + getDocId() + '\'' +
                ", contentMD5Sum='" + getContentMD5Sum() + '\'' +
                ", contentBeans=" + getContentBeans() +
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

    @Override
    public Boolean isForEndUserDisplay() {
        return null;
    }

    public Collection<BaseContent> getContentBeans() {
        return contentBeans;
    }

    public void setContentBeans(Collection<BaseContent> contentBeans) {
        this.contentBeans = contentBeans;
    }

    public int getEntitySize() {
        return this.getContentBeans().size();
    }
}
