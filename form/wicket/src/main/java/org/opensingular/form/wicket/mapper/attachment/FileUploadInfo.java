package org.opensingular.form.wicket.mapper.attachment;

import java.io.Serializable;
import java.util.UUID;

import org.json.JSONObject;

import com.google.common.collect.ComparisonChain;

public class FileUploadInfo implements Serializable, Comparable<FileUploadInfo> {

    public final UUID   uploadId;
    public final UUID   fileId;
    public final String hash;
    public final String name;
    public final long   size;
    public final long   timestamp;

    public FileUploadInfo(UUID uploadId, UUID fileId, String name, long size, String hash, long timestamp) {
        this.uploadId = uploadId;
        this.fileId = fileId;
        this.hash = hash;
        this.name = name;
        this.size = size;
        this.timestamp = timestamp;
    }

    public JSONObject toJSON() {
        JSONObject jsonFile = new JSONObject()
            .put("fileId", fileId)
            .put("name", name)
            .put("hashSHA1", hash)
            .put("size", size)
            .put("timestamp", timestamp);
        return jsonFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileUploadInfo that = (FileUploadInfo) o;

        if (size != that.size) {
            return false;
        }
        if (timestamp != that.timestamp){
            return false;
        }
        if (uploadId != null ? !uploadId.equals(that.uploadId) : that.uploadId != null){
            return false;
        }
        if (fileId != null ? !fileId.equals(that.fileId) : that.fileId != null){
            return false;
        }
        if (hash != null ? !hash.equals(that.hash) : that.hash != null){
            return false;
        }
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = uploadId != null ? uploadId.hashCode() : 0;
        result = 31 * result + (fileId != null ? fileId.hashCode() : 0);
        result = 31 * result + (hash != null ? hash.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public int compareTo(FileUploadInfo o) {
        return ComparisonChain.start()
            .compare(this.timestamp, o.timestamp)
            .compare(this.uploadId, o.uploadId)
            .compare(this.fileId, o.fileId)
            .compare(this.name, o.name)
            .compare(this.hash, o.hash)
            .compare(this.size, o.size)
            .result();
    }

    @Override
    public String toString() {
        return toJSON().toString(2);
    }
}
