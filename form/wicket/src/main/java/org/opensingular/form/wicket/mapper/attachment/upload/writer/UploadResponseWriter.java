package org.opensingular.form.wicket.mapper.attachment.upload.writer;

import org.apache.wicket.ajax.json.JSONArray;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.lib.commons.base.SingularUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

public class UploadResponseWriter implements Serializable {

    public void writeJsonObjectResponseTo(HttpServletResponse response, UploadResponseInfo uploadResponseInfo) {
        doWrite(response, uploadResponseInfo.toString());
    }

    public void writeJsonArrayResponseTo(HttpServletResponse response, List<UploadResponseInfo> list) {
        JSONArray array = new JSONArray();
        for (UploadResponseInfo r : list)
            array.put(r);
        doWrite(response, array.toString());
    }

    private void doWrite(HttpServletResponse response, String s) {
        response.setContentType("application/json");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

}
