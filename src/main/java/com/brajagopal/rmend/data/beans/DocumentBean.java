package com.brajagopal.rmend.data.beans;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @author <bxr4261>
 */
public class DocumentBean extends BaseContent {

    private long documentNumber;
    private String docId;
    private String title;
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
        String documentId = infoValue.get("docId");
        String docBody = infoValue.get("document");
        String[] docElements = docBody.split("\\n", 2);

        if (docElements.length == 2) {
            this.title = docElements[0];
            this.document = StringUtils.trim(docElements[1]);
        }
        else {
            this.title = "";
            this.document = docBody;
        }

        this.docId = documentId.substring(documentId.lastIndexOf("/") + 1, documentId.length());
        this.contentMD5Sum = DigestUtils.md5Hex(this.document);
        this.documentNumber = System.currentTimeMillis();
    }

    @Override
    public BaseContent getInstance() {
        return new DocumentBean();
    }

    @Override
    public double getScore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "DocumentBean {" +
                "docId='" + getDocId() + '\'' +
                ", docTitle='" + getTitle() + '\'' +
                ", documentNumber='" + getDocumentNumber() + '\'' +
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

    public long getDocumentNumber() {
        return documentNumber;
    }

    public String getTitle() {
        return title;
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
