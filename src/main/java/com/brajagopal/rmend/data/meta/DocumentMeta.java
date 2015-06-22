package com.brajagopal.rmend.data.meta;

import com.google.common.primitives.Doubles;

import java.util.Comparator;

/**
 * @author <bxr4261>
 */
public class DocumentMeta {

    private final long docId;
    private final String uniqueId;
    private final double score;

    private DocumentMeta(long _docId, String _uniqueId, Double _score) {
        this.docId = _docId;
        this.uniqueId = _uniqueId;
        this.score = _score;
    }

    public static DocumentMeta createInstance(long _docId, String _uniqueId, Double _score) {
        return new DocumentMeta(_docId, _uniqueId, _score);
    }

    public long getDocId() {
        return docId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "DocumentMeta {" +
                "docId=" + docId +
                ", uniqueId='" + uniqueId + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentMeta)) return false;

        DocumentMeta that = (DocumentMeta) o;

        if (docId != that.docId) return false;
        if (Double.compare(that.score, score) != 0) return false;
        if (!uniqueId.equals(that.uniqueId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (docId ^ (docId >>> 32));
        result = 31 * result + uniqueId.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final Comparator<DocumentMeta> DOCUMENT_META_COMPARATOR = new Comparator<DocumentMeta>() {
        @Override
        public int compare(DocumentMeta o1, DocumentMeta o2) {
            // Reversing the ordering to sort descending
            return Doubles.compare(o2.getScore(), o1.getScore());
        }
    };
}
