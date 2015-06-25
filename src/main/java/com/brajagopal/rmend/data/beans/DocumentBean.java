package com.brajagopal.rmend.data.beans;

import com.google.common.collect.HashMultimap;
import com.google.gson.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
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
        if (contentBeans != null) {
            return contentBeans.values();
        }
        return null;
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

    public Map<ContentType, Collection<BaseContent>> getContentBeansByType() {
        return contentBeans.asMap();
    }

    public static class DocumentSerDe implements JsonSerializer<DocumentBean>, JsonDeserializer<DocumentBean> {

        @Override
        public JsonElement serialize(final DocumentBean bean, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject root = new JsonObject();
            root.addProperty("docId", bean.docId);
            root.addProperty("title", bean.title);
            root.addProperty("md5sum", bean.contentMD5Sum);
            root.addProperty("docBody", bean.document);
            final JsonArray jsonTopicsArray = new JsonArray();
            for (final String topic : bean.getTopic()) {
                final JsonPrimitive topicPrimitive = new JsonPrimitive(topic);
                jsonTopicsArray.add(topicPrimitive);
            }
            root.add("topics", jsonTopicsArray);
            root.addProperty("docNum", bean.documentNumber);
            final JsonObject jsonObject = new JsonObject();
            for (Map.Entry<ContentType, Collection<BaseContent>> entry : bean.getContentBeansByType().entrySet()) {
                final JsonArray jsonArray = new JsonArray();
                for (final BaseContent contentBean : entry.getValue()) {
                    try {
                        jsonArray.add(new JsonPrimitive(contentBean.getName()));
                    }
                    catch (UnsupportedOperationException e) {}
                }
                jsonObject.add(entry.getKey().toString(), jsonArray);
            }
            root.add("contentBeans", jsonObject);

            return root;
        }

        @Override
        public DocumentBean deserialize(final JsonElement jsonElement,
                                        final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            DocumentBean bean = new DocumentBean();
            final JsonObject root = jsonElement.getAsJsonObject();

            final String docId = root.get("docId").getAsString();
            final String title = root.get("title").getAsString();
            final String md5sum = root.get("md5sum").getAsString();
            final Collection<String> topics = new ArrayList<String>();
            final JsonArray jsonTopicsArray = root.get("topics").getAsJsonArray();
            for (final JsonElement _jsonElement : jsonTopicsArray) {
                topics.add(_jsonElement.getAsString());
            }
            final long documentNumber = root.get("docNum").getAsLong();
            final String docBody = root.get("docBody").getAsString();

            bean.docId = docId;
            bean.title = title;
            bean.topics = topics;
            bean.contentMD5Sum = md5sum;
            bean.documentNumber = documentNumber;
            bean.document = docBody;

            return bean;
        }
    }
}
