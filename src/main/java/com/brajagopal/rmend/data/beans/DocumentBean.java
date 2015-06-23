package com.brajagopal.rmend.data.beans;

import com.google.common.collect.HashMultimap;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Collection<String> topics;
    private HashMultimap<BaseContent.ContentType, BaseContent> contentBeans;

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
            this.document = StringUtils.toEncodedString(docBody.getBytes(), Charset.forName("UTF8"));
        }

        this.docId = documentId.substring(documentId.lastIndexOf("/") + 1, documentId.length());
        this.contentMD5Sum = DigestUtils.md5Hex(this.document);
        this.documentNumber = System.currentTimeMillis();
        contentBeans = HashMultimap.create();
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
                ", topic='" + getTopic() + '\'' +
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
        return contentBeans.values();
    }

    public Collection<String> getTopic() {
        return topics;
    }

    public void setContentBeans(Collection<BaseContent> _contentBeans) {
        for (BaseContent bean : _contentBeans) {
            contentBeans.put(bean.getContentType(), bean);
        }

        Collection<BaseContent> topics = contentBeans.get(ContentType.TOPICS);
        if (topics.size() > 0) {
            this.topics = new ArrayList<String>();
            for (BaseContent topic : topics) {
                this.topics.add(topic.getName());
            }
        }
        else {
            this.topics = Arrays.asList("NA");
        }
    }

    public int getEntitySize() {
        return this.getContentBeans().size();
    }
}
