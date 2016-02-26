package br.net.mirante.singular.pet.module.wicket.view.form;

import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.pet.module.exception.SingularServerException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.StringValue;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class FormPageUtil {

    public static final String TYPE_NAME = "t";
    public static final String MODEL_KEY = "k";
    public static final String VIEW_MODE = "v";
    public static final String ANNOTATION_MODE = "a";
    private static final String ENCODING = "UTF-8";
    private static final BiMap<String, ViewMode> keyMapViewMode = HashBiMap.create();
    private static final BiMap<String, AnnotationMode> keyMapAnnotationMode = HashBiMap.create();
    private static int keySeed = 0;

    static {
        for (ViewMode v : ViewMode.values()) {
            keyMapViewMode.put(String.valueOf(keySeed++), v);
        }

        for (AnnotationMode a : AnnotationMode.values()) {
            keyMapAnnotationMode.put(String.valueOf(keySeed++), a);
        }
    }

    static URLParams readParameters(Request request) {
        URLParams urLparams = new URLParams();
        StringValue type = request.getRequestParameters().getParameterValue(TYPE_NAME);
        StringValue formId = request.getRequestParameters().getParameterValue(MODEL_KEY);
        StringValue viewMode = request.getRequestParameters().getParameterValue(VIEW_MODE);
        StringValue annotationMode = request.getRequestParameters().getParameterValue(ANNOTATION_MODE);
        urLparams.type = decompress(type.toString());
        urLparams.formId = formId.toString();
        urLparams.viewMode = Optional
                .ofNullable(
                        keyMapViewMode
                                .get(viewMode
                                        .toString()))
                .orElse(ViewMode.EDITION);
        urLparams.annotationMode = Optional
                .ofNullable(
                        keyMapAnnotationMode
                                .get(annotationMode
                                        .toString()))
                .orElse(AnnotationMode.NONE);
        return urLparams;
    }

    public static String buildUrl(String baseURL, String type) {
        return buildUrl(baseURL, type, null, null, null);
    }

    public static String buildUrl(String baseURL, String type, Object formId, ViewMode mode) {
        return buildUrl(baseURL, type, String.valueOf(formId), mode, null);
    }

    public static String buildUrl(String baseURL, String type, Object formId, ViewMode mode, AnnotationMode annotationMode) {
        return buildUrl(baseURL, type, String.valueOf(formId), mode, annotationMode);
    }

    public static String buildUrl(String baseURL, String type, String formId, ViewMode mode, AnnotationMode annotationMode) {
        String base = baseURL + "?" + TYPE_NAME + "=" + compress(type);
        if (!StringUtils.isEmpty(formId)) {
            base += "&" + MODEL_KEY + "=" + formId;
        }
        if (mode != null) {
            base += "&" + VIEW_MODE + "=" + keyMapViewMode.inverse().get(mode);
        }
        if (annotationMode != null) {
            base += "&" + ANNOTATION_MODE + "=" + keyMapAnnotationMode.inverse().get(annotationMode);
        }
        return base;
    }

    private static String compress(String text) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = new DeflaterOutputStream(baos);
            out.write(text.getBytes(ENCODING));
            out.close();
            return new String(Base64.getEncoder().encode(baos.toByteArray()), ENCODING);
        } catch (IOException e) {
            throw new SingularServerException(e.getMessage(), e);
        }
    }

    private static String decompress(String value) {
        try {
            if (value == null) {
                return null;
            }

            byte[] bytes = Base64.getDecoder().decode(value.getBytes(ENCODING));
            InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            in.close();
            baos.close();
            return new String(baos.toByteArray(), ENCODING);
        } catch (IOException e) {
            throw new SingularServerException(e.getMessage(), e);
        }
    }

    static class URLParams {
        ViewMode viewMode = ViewMode.VISUALIZATION;
        AnnotationMode annotationMode = AnnotationMode.NONE;
        String formId;
        String type;
    }


}
