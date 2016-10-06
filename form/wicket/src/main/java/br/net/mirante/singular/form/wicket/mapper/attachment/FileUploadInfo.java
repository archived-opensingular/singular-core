package br.net.mirante.singular.form.wicket.mapper.attachment;

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
