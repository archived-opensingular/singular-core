/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.form.wicket.mapper.attachment;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.io.IOUtils;
import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.flow.AbortWithHttpErrorCodeException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;

import com.google.common.collect.ImmutableMap;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;

/**
 * Behavior a ser adicionado ao componente de upload/download para permitir download dos arquivos
 * Busca o arquivo por meio do hash e do nome e retorna uma url com um link temporário para download
 * o link retornado funciona apenas uma vez.
 * <p>
 * A busca é feita primeiro no armazenamento temporárioe  em seguida no permanente.
 *
 * @author vinicius
 */
public class DownloadSupportedBehavior extends Behavior implements IResourceListener, Loggable {
    private static final String         DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String         DOWNLOAD_PATH        = "/download";
    private Component                   component;
    private IModel<? extends SInstance> model;

    public DownloadSupportedBehavior(IModel<? extends SInstance> model) {
        this.model = model;
    }

    private List<IAttachmentPersistenceHandler<?>> getHandlers() {
        List<IAttachmentPersistenceHandler<?>> services = new ArrayList<>();
        if (model.getObject().getDocument().isAttachmentPersistenceTemporaryHandlerSupported()) {
            services.add(model.getObject().getDocument().getAttachmentPersistenceTemporaryHandler());
        }
        if (model.getObject().getDocument().isAttachmentPersistencePermanentHandlerSupported()) {
            services.add(model.getObject().getDocument().getAttachmentPersistencePermanentHandler());
        }
        return services;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(forReference(new PackageResourceReference(getClass(), "DownloadSupportedBehavior.js")));
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    @Override
    public void onResourceRequested() {
        try {
            handleRequest();
        } catch (IOException e) {
            throw new AbortWithHttpErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private IAttachmentRef findAttachmentRef(String id) {
        IAttachmentRef ref = null;
        for (IAttachmentPersistenceHandler<?> service : getHandlers()) {
            ref = service.getAttachment(id);
            if (ref != null) {
                break;
            }
        }
        return ref;
    }

    private void handleRequest() throws IOException {
        WebRequest request = (WebRequest) RequestCycle.get().getRequest();
        IRequestParameters parameters = request.getRequestParameters();
        StringValue id = parameters.getParameterValue("fileId");
        StringValue name = parameters.getParameterValue("fileName");
        writeResponse(getDownloadURL(id.toString(), name.toString()));
    }

    private void writeResponse(String url) throws IOException {
        JSONObject jsonFile = new JSONObject();
        jsonFile.put("url", url);
        WebResponse response = (WebResponse) RequestCycle.get().getResponse();
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.getOutputStream().write(jsonFile.toString().getBytes());
        response.flush();
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }

    /**
     * Registra um recurso compartilhado do wicket para permitir o download
     * sem bloquear a fila de ajax do wicket.
     * O recurso compartilhado é removido tão logo o download é executado
     * Esse procedimento visa garantir que somente quem tem acesso à página pode fazer
     * download dos arquivos.
     *
     * @param filename
     * @return
     */
    private String getDownloadURL(String id, String filename) {
        String url = DOWNLOAD_PATH + "/" + id + "/" + new Date().getTime();
        SharedResourceReference ref = new SharedResourceReference(String.valueOf(id));
        AbstractResource resource = new AbstractResource() {
            @Override
            protected ResourceResponse newResourceResponse(Attributes attributes) {
                IAttachmentRef fileRef = findAttachmentRef(id);
                if (fileRef == null) {
                    return new ResourceResponse().setStatusCode(HttpServletResponse.SC_NOT_FOUND);
                }
                ResourceResponse resourceResponse = new ResourceResponse();
                if (fileRef.getSize() > 0) {
                    resourceResponse.setContentLength(fileRef.getSize());
                }
                resourceResponse.setFileName(filename);
                try {
                    resourceResponse.setContentType(fileRef.getContentType());
                    resourceResponse.setWriteCallback(new WriteCallback() {
                        @Override
                        public void writeData(Attributes attributes) throws IOException {
                            try (InputStream inputStream = fileRef.getInputStream()) {
                                IOUtils.copy(inputStream, attributes.getResponse().getOutputStream());
                                /*Desregistrando recurso compartilhado*/
                                WebApplication.get().unmount(url);
                                WebApplication.get().getSharedResources().remove(ref.getKey());
                            } catch (Exception e) {
                                getLogger().error("Erro ao recuperar arquivo.", e);
                                ((WebResponse)attributes.getResponse()).setStatus(HttpServletResponse.SC_NOT_FOUND);
                                resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
                            }
                        }
                    });
                } catch (Exception e) {
                    getLogger().error("Erro ao recuperar arquivo.", e);
                    resourceResponse.setStatusCode(HttpServletResponse.SC_NOT_FOUND);
                }
                return resourceResponse;
            }
        };
        /*registrando recurso compartilhado*/
        WebApplication.get().getSharedResources().add(String.valueOf(id), resource);
        WebApplication.get().mountResource(url, ref);
        String path = WebApplication.get().getServletContext().getContextPath() + "/" + WebApplication.get().getWicketFilter().getFilterPath() + url;
        path = path.replaceAll("\\*", "").replaceAll("//", "/");
        return path;
    }
    private static String resolveContentType(String filename) {
        String ext = lowerCase(substringAfterLast(filename, "."));
        return MIME_TYPES.getOrDefault(ext, DEFAULT_CONTENT_TYPE);
    }

    // fonte: http://www.freeformatter.com/mime-types-list.html
    private static final ImmutableMap<String, String> MIME_TYPES;
    static {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.<String, String> builder();
        buildMimeTypes1(builder);
        buildMimeTypes2(builder);
        buildMimeTypes3(builder);
        buildMimeTypes4(builder);
        MIME_TYPES = builder.build();
    }
    protected static void buildMimeTypes4(ImmutableMap.Builder<String, String> builder) {
        builder
            .put("tei", "application/tei+xml")
            .put("txt", "text/plain")
            .put("dxp", "application/vnd.spotfire.dxp")
            .put("sfs", "application/vnd.spotfire.sfs")
            .put("tsd", "application/timestamped-data")
            .put("tpt", "application/vnd.trid.tpt")
            .put("mxs", "application/vnd.triscape.mxs")
            .put("t", "text/troff")
            .put("tra", "application/vnd.trueapp")
            .put("ttf", "application/x-font-ttf")
            .put("ttl", "text/turtle")
            .put("umj", "application/vnd.umajin")
            .put("uoml", "application/vnd.uoml+xml")
            .put("unityweb", "application/vnd.unity")
            .put("ufd", "application/vnd.ufdl")
            .put("uri", "text/uri-list")
            .put("utz", "application/vnd.uiq.theme")
            .put("ustar", "application/x-ustar")
            .put("uu", "text/x-uuencode")
            .put("vcs", "text/x-vcalendar")
            .put("vcf", "text/x-vcard")
            .put("vcd", "application/x-cdlink")
            .put("vsf", "application/vnd.vsf")
            .put("wrl", "model/vrml")
            .put("vcx", "application/vnd.vcx")
            .put("mts", "model/vnd.mts")
            .put("vtu", "model/vnd.vtu")
            .put("vis", "application/vnd.visionary")
            .put("viv", "video/vnd.vivo")
            .put("ccxml", "application/ccxml+xml")
            .put("vxml", "application/voicexml+xml")
            .put("src", "application/x-wais-source")
            .put("wbxml", "application/vnd.wap.wbxml")
            .put("wbmp", "image/vnd.wap.wbmp")
            .put("wav", "audio/x-wav")
            .put("davmount", "application/davmount+xml")
            .put("woff", "application/x-font-woff")
            .put("wspolicy", "application/wspolicy+xml")
            .put("webp", "image/webp")
            .put("wtb", "application/vnd.webturbo")
            .put("wgt", "application/widget")
            .put("hlp", "application/winhlp")
            .put("wml", "text/vnd.wap.wml")
            .put("wmls", "text/vnd.wap.wmlscript")
            .put("wmlsc", "application/vnd.wap.wmlscriptc")
            .put("wpd", "application/vnd.wordperfect")
            .put("stf", "application/vnd.wt.stf")
            .put("wsdl", "application/wsdl+xml")
            .put("xbm", "image/x-xbitmap")
            .put("xpm", "image/x-xpixmap")
            .put("xwd", "image/x-xwindowdump")
            .put("der", "application/x-x509-ca-cert")
            .put("fig", "application/x-xfig")
            .put("xhtml", "application/xhtml+xml")
            .put("xml", "application/xml")
            .put("xdf", "application/xcap-diff+xml")
            .put("xenc", "application/xenc+xml")
            .put("xer", "application/patch-ops-error+xml")
            .put("rl", "application/resource-lists+xml")
            .put("rs", "application/rls-services+xml")
            .put("rld", "application/resource-lists-diff+xml")
            .put("xslt", "application/xslt+xml")
            .put("xop", "application/xop+xml")
            .put("xpi", "application/x-xpinstall")
            .put("xspf", "application/xspf+xml")
            .put("xul", "application/vnd.mozilla.xul+xml")
            .put("xyz", "chemical/x-xyz")
            .put("yaml", "text/yaml")
            .put("yang", "application/yang")
            .put("yin", "application/yin+xml")
            .put("zir", "application/vnd.zul")
            .put("zip", "application/zip")
            .put("zmm", "application/vnd.handheld-entertainment+xml")
            .put("zaz", "application/vnd.zzazz.deck+xml");
    }
    protected static void buildMimeTypes3(ImmutableMap.Builder<String, String> builder) {
        builder
            .put("m21", "application/mp21")
            .put("mp4a", "audio/mp4")
            .put("mp4", "video/mp4")
            .put("m3u8", "application/vnd.apple.mpegurl")
            .put("mus", "application/vnd.musician")
            .put("msty", "application/vnd.muvee.style")
            .put("mxml", "application/xv+xml")
            .put("ngdat", "application/vnd.nokia.n-gage.data")
            .put("n-gage", "application/vnd.nokia.n-gage.symbian.install")
            .put("ncx", "application/x-dtbncx+xml")
            .put("nc", "application/x-netcdf")
            .put("nlu", "application/vnd.neurolanguage.nlu")
            .put("dna", "application/vnd.dna")
            .put("nnd", "application/vnd.noblenet-directory")
            .put("nns", "application/vnd.noblenet-sealer")
            .put("nnw", "application/vnd.noblenet-web")
            .put("rpst", "application/vnd.nokia.radio-preset")
            .put("rpss", "application/vnd.nokia.radio-presets")
            .put("n3", "text/n3")
            .put("edm", "application/vnd.novadigm.edm")
            .put("edx", "application/vnd.novadigm.edx")
            .put("ext", "application/vnd.novadigm.ext")
            .put("gph", "application/vnd.flographit")
            .put("ecelp4800", "audio/vnd.nuera.ecelp4800")
            .put("ecelp7470", "audio/vnd.nuera.ecelp7470")
            .put("ecelp9600", "audio/vnd.nuera.ecelp9600")
            .put("oda", "application/oda")
            .put("ogx", "application/ogg")
            .put("oga", "audio/ogg")
            .put("ogv", "video/ogg")
            .put("dd2", "application/vnd.oma.dd2+xml")
            .put("oth", "application/vnd.oasis.opendocument.text-web")
            .put("opf", "application/oebps-package+xml")
            .put("qbo", "application/vnd.intu.qbo")
            .put("oxt", "application/vnd.openofficeorg.extension")
            .put("osf", "application/vnd.yamaha.openscoreformat")
            .put("weba", "audio/webm")
            .put("webm", "video/webm")
            .put("odc", "application/vnd.oasis.opendocument.chart")
            .put("otc", "application/vnd.oasis.opendocument.chart-template")
            .put("odb", "application/vnd.oasis.opendocument.database")
            .put("odf", "application/vnd.oasis.opendocument.formula")
            .put("odft", "application/vnd.oasis.opendocument.formula-template")
            .put("odg", "application/vnd.oasis.opendocument.graphics")
            .put("otg", "application/vnd.oasis.opendocument.graphics-template")
            .put("odi", "application/vnd.oasis.opendocument.image")
            .put("oti", "application/vnd.oasis.opendocument.image-template")
            .put("odp", "application/vnd.oasis.opendocument.presentation")
            .put("otp", "application/vnd.oasis.opendocument.presentation-template")
            .put("ods", "application/vnd.oasis.opendocument.spreadsheet")
            .put("ots", "application/vnd.oasis.opendocument.spreadsheet-template")
            .put("odt", "application/vnd.oasis.opendocument.text")
            .put("odm", "application/vnd.oasis.opendocument.text-master")
            .put("ott", "application/vnd.oasis.opendocument.text-template")
            .put("ktx", "image/ktx")
            .put("sxc", "application/vnd.sun.xml.calc")
            .put("stc", "application/vnd.sun.xml.calc.template")
            .put("sxd", "application/vnd.sun.xml.draw")
            .put("std", "application/vnd.sun.xml.draw.template")
            .put("sxi", "application/vnd.sun.xml.impress")
            .put("sti", "application/vnd.sun.xml.impress.template")
            .put("sxm", "application/vnd.sun.xml.math")
            .put("sxw", "application/vnd.sun.xml.writer")
            .put("sxg", "application/vnd.sun.xml.writer.global")
            .put("stw", "application/vnd.sun.xml.writer.template")
            .put("otf", "application/x-font-otf")
            .put("osfpvg", "application/vnd.yamaha.openscoreformat.osfpvg+xml")
            .put("dp", "application/vnd.osgi.dp")
            .put("pdb", "application/vnd.palm")
            .put("p", "text/x-pascal")
            .put("paw", "application/vnd.pawaafile")
            .put("pclxl", "application/vnd.hp-pclxl")
            .put("efif", "application/vnd.picsel")
            .put("pcx", "image/x-pcx")
            .put("psd", "image/vnd.adobe.photoshop")
            .put("prf", "application/pics-rules")
            .put("pic", "image/x-pict")
            .put("chat", "application/x-chat")
            .put("p10", "application/pkcs10")
            .put("p12", "application/x-pkcs12")
            .put("p7m", "application/pkcs7-mime")
            .put("p7s", "application/pkcs7-signature")
            .put("p7r", "application/x-pkcs7-certreqresp")
            .put("p7b", "application/x-pkcs7-certificates")
            .put("p8", "application/pkcs8")
            .put("plf", "application/vnd.pocketlearn")
            .put("pnm", "image/x-portable-anymap")
            .put("pbm", "image/x-portable-bitmap")
            .put("pcf", "application/x-font-pcf")
            .put("pfr", "application/font-tdpfr")
            .put("pgn", "application/x-chess-pgn")
            .put("pgm", "image/x-portable-graymap")
            .put("png", "image/png")
            .put("ppm", "image/x-portable-pixmap")
            .put("pskcxml", "application/pskc+xml")
            .put("pml", "application/vnd.ctc-posml")
            .put("ai", "application/postscript")
            .put("pfa", "application/x-font-type1")
            .put("pbd", "application/vnd.powerbuilder6")
            .put("pgp", "application/pgp-encrypted")
            .put("box", "application/vnd.previewsystems.box")
            .put("ptid", "application/vnd.pvi.ptid1")
            .put("pls", "application/pls+xml")
            .put("str", "application/vnd.pg.format")
            .put("ei6", "application/vnd.pg.osasli")
            .put("dsc", "text/prs.lines.tag")
            .put("psf", "application/x-font-linux-psf")
            .put("qps", "application/vnd.publishare-delta-tree")
            .put("wg", "application/vnd.pmi.widget")
            .put("qxd", "application/vnd.quark.quarkxpress")
            .put("esf", "application/vnd.epson.esf")
            .put("msf", "application/vnd.epson.msf")
            .put("ssf", "application/vnd.epson.ssf")
            .put("qam", "application/vnd.epson.quickanime")
            .put("qfx", "application/vnd.intu.qfx")
            .put("qt", "video/quicktime")
            .put("rar", "application/x-rar-compressed")
            .put("ram", "audio/x-pn-realaudio")
            .put("rmp", "audio/x-pn-realaudio-plugin")
            .put("rsd", "application/rsd+xml")
            .put("rm", "application/vnd.rn-realmedia")
            .put("mxl", "application/vnd.recordare.musicxml")
            .put("musicxml", "application/vnd.recordare.musicxml+xml")
            .put("rnc", "application/relax-ng-compact-syntax")
            .put("rdz", "application/vnd.data-vision.rdz")
            .put("rdf", "application/rdf+xml")
            .put("rp9", "application/vnd.cloanto.rp9")
            .put("jisp", "application/vnd.jisp")
            .put("rtf", "application/rtf")
            .put("rtx", "text/richtext")
            .put("link66", "application/vnd.route66.link66+xml")
            .put("rss", "application/rss+xml")
            .put("shf", "application/shf+xml")
            .put("st", "application/vnd.sailingtracker.track")
            .put("svg", "image/svg+xml")
            .put("sus", "application/vnd.sus-calendar")
            .put("sru", "application/sru+xml")
            .put("setpay", "application/set-payment-initiation")
            .put("setreg", "application/set-registration-initiation")
            .put("sema", "application/vnd.sema")
            .put("semd", "application/vnd.semd")
            .put("semf", "application/vnd.semf")
            .put("see", "application/vnd.seemail")
            .put("snf", "application/x-font-snf")
            .put("spq", "application/scvp-vp-request")
            .put("spp", "application/scvp-vp-response")
            .put("scq", "application/scvp-cv-request")
            .put("scs", "application/scvp-cv-response")
            .put("sdp", "application/sdp")
            .put("etx", "text/x-setext")
            .put("movie", "video/x-sgi-movie")
            .put("ifm", "application/vnd.shana.informed.formdata")
            .put("itp", "application/vnd.shana.informed.formtemplate")
            .put("iif", "application/vnd.shana.informed.interchange")
            .put("ipk", "application/vnd.shana.informed.package")
            .put("tfi", "application/thraud+xml")
            .put("shar", "application/x-shar")
            .put("rgb", "image/x-rgb")
            .put("slt", "application/vnd.epson.salt")
            .put("aso", "application/vnd.accpac.simply.aso")
            .put("imp", "application/vnd.accpac.simply.imp")
            .put("twd", "application/vnd.simtech-mindmapper")
            .put("csp", "application/vnd.commonspace")
            .put("saf", "application/vnd.yamaha.smaf-audio")
            .put("mmf", "application/vnd.smaf")
            .put("spf", "application/vnd.yamaha.smaf-phrase")
            .put("teacher", "application/vnd.smart.teacher")
            .put("svd", "application/vnd.svd")
            .put("rq", "application/sparql-query")
            .put("srx", "application/sparql-results+xml")
            .put("gram", "application/srgs")
            .put("grxml", "application/srgs+xml")
            .put("ssml", "application/ssml+xml")
            .put("skp", "application/vnd.koan")
            .put("sgml", "text/sgml")
            .put("sdc", "application/vnd.stardivision.calc")
            .put("sdd", "application/vnd.stardivision.impress")
            .put("sdw", "application/vnd.stardivision.writer")
            .put("sm", "application/vnd.stepmania.stepchart")
            .put("sit", "application/x-stuffit")
            .put("sitx", "application/x-stuffitx")
            .put("sdkm", "application/vnd.solent.sdkm+xml")
            .put("xo", "application/vnd.olpc-sugar")
            .put("au", "audio/basic")
            .put("wqd", "application/vnd.wqd")
            .put("sis", "application/vnd.symbian.install")
            .put("smi", "application/smil+xml")
            .put("xsm", "application/vnd.syncml+xml")
            .put("bdm", "application/vnd.syncml.dm+wbxml")
            .put("xdm", "application/vnd.syncml.dm+xml")
            .put("sv4cpio", "application/x-sv4cpio")
            .put("sv4crc", "application/x-sv4crc")
            .put("sbml", "application/sbml+xml")
            .put("tsv", "text/tab-separated-values")
            .put("tiff", "image/tiff")
            .put("tao", "application/vnd.tao.intent-module-archive")
            .put("tar", "application/x-tar")
            .put("tcl", "application/x-tcl")
            .put("tex", "application/x-tex")
            .put("tfm", "application/x-tex-tfm");
    }
    protected static void buildMimeTypes2(ImmutableMap.Builder<String, String> builder) {
        builder
            .put("gv", "text/vnd.graphviz")
            .put("gac", "application/vnd.groove-account")
            .put("ghf", "application/vnd.groove-help")
            .put("gim", "application/vnd.groove-identity-message")
            .put("grv", "application/vnd.groove-injector")
            .put("gtm", "application/vnd.groove-tool-message")
            .put("tpl", "application/vnd.groove-tool-template")
            .put("vcg", "application/vnd.groove-vcard")
            .put("h261", "video/h261")
            .put("h263", "video/h263")
            .put("h264", "video/h264")
            .put("hpid", "application/vnd.hp-hpid")
            .put("hps", "application/vnd.hp-hps")
            .put("hdf", "application/x-hdf")
            .put("rip", "audio/vnd.rip")
            .put("hbci", "application/vnd.hbci")
            .put("jlt", "application/vnd.hp-jlyt")
            .put("pcl", "application/vnd.hp-pcl")
            .put("hpgl", "application/vnd.hp-hpgl")
            .put("hvs", "application/vnd.yamaha.hv-script")
            .put("hvd", "application/vnd.yamaha.hv-dic")
            .put("hvp", "application/vnd.yamaha.hv-voice")
            .put("sfd-hdstx", "application/vnd.hydrostatix.sof-data")
            .put("stk", "application/hyperstudio")
            .put("hal", "application/vnd.hal+xml")
            .put("html", "text/html")
            .put("irm", "application/vnd.ibm.rights-management")
            .put("sc", "application/vnd.ibm.secure-container")
            .put("ics", "text/calendar")
            .put("icc", "application/vnd.iccprofile")
            .put("ico", "image/x-icon")
            .put("igl", "application/vnd.igloader")
            .put("ief", "image/ief")
            .put("ivp", "application/vnd.immervision-ivp")
            .put("ivu", "application/vnd.immervision-ivu")
            .put("rif", "application/reginfo+xml")
            .put("spot", "text/vnd.in3d.spot")
            .put("igs", "model/iges")
            .put("i2g", "application/vnd.intergeo")
            .put("cdy", "application/vnd.cinderella")
            .put("xpw", "application/vnd.intercon.formnet")
            .put("fcs", "application/vnd.isac.fcs")
            .put("ipfix", "application/ipfix")
            .put("cer", "application/pkix-cert")
            .put("pki", "application/pkixcmp")
            .put("crl", "application/pkix-crl")
            .put("pkipath", "application/pkix-pkipath")
            .put("igm", "application/vnd.insors.igm")
            .put("rcprofile", "application/vnd.ipunplugged.rcprofile")
            .put("irp", "application/vnd.irepository.package+xml")
            .put("jad", "text/vnd.sun.j2me.app-descriptor")
            .put("jar", "application/java-archive")
            .put("class", "application/java-vm")
            .put("jnlp", "application/x-java-jnlp-file")
            .put("ser", "application/java-serialized-object")
            .put("java", "text/x-java-source")
            .put("js", "application/javascript")
            .put("json", "application/json")
            .put("joda", "application/vnd.joost.joda-archive")
            .put("jpm", "video/jpm")
            .put("jpg", "image/jpeg")
            .put("jpeg", "image/jpeg")
            .put("jpgv", "video/jpeg")
            .put("ktz", "application/vnd.kahootz")
            .put("mmd", "application/vnd.chipnuts.karaoke-mmd")
            .put("karbon", "application/vnd.kde.karbon")
            .put("chrt", "application/vnd.kde.kchart")
            .put("kfo", "application/vnd.kde.kformula")
            .put("flw", "application/vnd.kde.kivio")
            .put("kon", "application/vnd.kde.kontour")
            .put("kpr", "application/vnd.kde.kpresenter")
            .put("ksp", "application/vnd.kde.kspread")
            .put("kwd", "application/vnd.kde.kword")
            .put("htke", "application/vnd.kenameaapp")
            .put("kia", "application/vnd.kidspiration")
            .put("kne", "application/vnd.kinar")
            .put("sse", "application/vnd.kodak-descriptor")
            .put("lasxml", "application/vnd.las.las+xml")
            .put("latex", "application/x-latex")
            .put("lbd", "application/vnd.llamagraphics.life-balance.desktop")
            .put("lbe", "application/vnd.llamagraphics.life-balance.exchange+xml")
            .put("jam", "application/vnd.jam")
            .put("123", "application/vnd.lotus-1-2-3")
            .put("apr", "application/vnd.lotus-approach")
            .put("pre", "application/vnd.lotus-freelance")
            .put("nsf", "application/vnd.lotus-notes")
            .put("org", "application/vnd.lotus-organizer")
            .put("scm", "application/vnd.lotus-screencam")
            .put("lwp", "application/vnd.lotus-wordpro")
            .put("lvp", "audio/vnd.lucent.voice")
            .put("m3u", "audio/x-mpegurl")
            .put("m4v", "video/x-m4v")
            .put("hqx", "application/mac-binhex40")
            .put("portpkg", "application/vnd.macports.portpkg")
            .put("mgp", "application/vnd.osgeo.mapguide.package")
            .put("mrc", "application/marc")
            .put("mrcx", "application/marcxml+xml")
            .put("mxf", "application/mxf")
            .put("nbp", "application/vnd.wolfram.player")
            .put("ma", "application/mathematica")
            .put("mathml", "application/mathml+xml")
            .put("mbox", "application/mbox")
            .put("mc1", "application/vnd.medcalcdata")
            .put("mscml", "application/mediaservercontrol+xml")
            .put("cdkey", "application/vnd.mediastation.cdkey")
            .put("mwf", "application/vnd.mfer")
            .put("mfm", "application/vnd.mfmp")
            .put("msh", "model/mesh")
            .put("mads", "application/mads+xml")
            .put("mets", "application/mets+xml")
            .put("mods", "application/mods+xml")
            .put("meta4", "application/metalink4+xml")
            .put("potm", "application/vnd.ms-powerpoint.template.macroenabled.12")
            .put("docm", "application/vnd.ms-word.document.macroenabled.12")
            .put("dotm", "application/vnd.ms-word.template.macroenabled.12")
            .put("mcd", "application/vnd.mcd")
            .put("flo", "application/vnd.micrografx.flo")
            .put("igx", "application/vnd.micrografx.igx")
            .put("es3", "application/vnd.eszigno3+xml")
            .put("mdb", "application/x-msaccess")
            .put("asf", "video/x-ms-asf")
            .put("exe", "application/x-msdownload")
            .put("cil", "application/vnd.ms-artgalry")
            .put("cab", "application/vnd.ms-cab-compressed")
            .put("ims", "application/vnd.ms-ims")
            .put("application", "application/x-ms-application")
            .put("clp", "application/x-msclip")
            .put("mdi", "image/vnd.ms-modi")
            .put("eot", "application/vnd.ms-fontobject")
            .put("xls", "application/vnd.ms-excel")
            .put("xlam", "application/vnd.ms-excel.addin.macroenabled.12")
            .put("xlsb", "application/vnd.ms-excel.sheet.binary.macroenabled.12")
            .put("xltm", "application/vnd.ms-excel.template.macroenabled.12")
            .put("xlsm", "application/vnd.ms-excel.sheet.macroenabled.12")
            .put("chm", "application/vnd.ms-htmlhelp")
            .put("crd", "application/x-mscardfile")
            .put("lrm", "application/vnd.ms-lrm")
            .put("mvb", "application/x-msmediaview")
            .put("mny", "application/x-msmoney")
            .put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
            .put("sldx", "application/vnd.openxmlformats-officedocument.presentationml.slide")
            .put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow")
            .put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template")
            .put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template")
            .put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            .put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            .put("obd", "application/x-msbinder")
            .put("thmx", "application/vnd.ms-officetheme")
            .put("onetoc", "application/onenote")
            .put("pya", "audio/vnd.ms-playready.media.pya")
            .put("pyv", "video/vnd.ms-playready.media.pyv")
            .put("ppt", "application/vnd.ms-powerpoint")
            .put("ppam", "application/vnd.ms-powerpoint.addin.macroenabled.12")
            .put("sldm", "application/vnd.ms-powerpoint.slide.macroenabled.12")
            .put("pptm", "application/vnd.ms-powerpoint.presentation.macroenabled.12")
            .put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroenabled.12")
            .put("mpp", "application/vnd.ms-project")
            .put("pub", "application/x-mspublisher")
            .put("scd", "application/x-msschedule")
            .put("xap", "application/x-silverlight-app")
            .put("stl", "application/vnd.ms-pki.stl")
            .put("cat", "application/vnd.ms-pki.seccat")
            .put("vsd", "application/vnd.visio")
            .put("wm", "video/x-ms-wm")
            .put("wma", "audio/x-ms-wma")
            .put("wax", "audio/x-ms-wax")
            .put("wmx", "video/x-ms-wmx")
            .put("wmd", "application/x-ms-wmd")
            .put("wpl", "application/vnd.ms-wpl")
            .put("wmz", "application/x-ms-wmz")
            .put("wmv", "video/x-ms-wmv")
            .put("wvx", "video/x-ms-wvx")
            .put("wmf", "application/x-msmetafile")
            .put("trm", "application/x-msterminal")
            .put("doc", "application/msword")
            .put("wri", "application/x-mswrite")
            .put("wps", "application/vnd.ms-works")
            .put("xbap", "application/x-ms-xbap")
            .put("xps", "application/vnd.ms-xpsdocument")
            .put("mid", "audio/midi")
            .put("mpy", "application/vnd.ibm.minipay")
            .put("afp", "application/vnd.ibm.modcap")
            .put("rms", "application/vnd.jcp.javame.midlet-rms")
            .put("tmo", "application/vnd.tmobile-livetv")
            .put("prc", "application/x-mobipocket-ebook")
            .put("mbk", "application/vnd.mobius.mbk")
            .put("dis", "application/vnd.mobius.dis")
            .put("plc", "application/vnd.mobius.plc")
            .put("mqy", "application/vnd.mobius.mqy")
            .put("msl", "application/vnd.mobius.msl")
            .put("txf", "application/vnd.mobius.txf")
            .put("daf", "application/vnd.mobius.daf")
            .put("fly", "text/vnd.fly")
            .put("mpc", "application/vnd.mophun.certificate")
            .put("mpn", "application/vnd.mophun.application")
            .put("mj2", "video/mj2")
            .put("mpga", "audio/mpeg")
            .put("mxu", "video/vnd.mpegurl")
            .put("mpeg", "video/mpeg");
    }
    protected static void buildMimeTypes1(ImmutableMap.Builder<String, String> builder) {
        builder
            .put("x3d", "application/vnd.hzn-3d-crossword")
            .put("3gp", "video/3gpp")
            .put("3g2", "video/3gpp2")
            .put("mseq", "application/vnd.mseq")
            .put("pwn", "application/vnd.3m.post-it-notes")
            .put("plb", "application/vnd.3gpp.pic-bw-large")
            .put("psb", "application/vnd.3gpp.pic-bw-small")
            .put("pvb", "application/vnd.3gpp.pic-bw-var")
            .put("tcap", "application/vnd.3gpp2.tcap")
            .put("7z", "application/x-7z-compressed")
            .put("abw", "application/x-abiword")
            .put("ace", "application/x-ace-compressed")
            .put("acc", "application/vnd.americandynamics.acc")
            .put("acu", "application/vnd.acucobol")
            .put("atc", "application/vnd.acucorp")
            .put("adp", "audio/adpcm")
            .put("aab", "application/x-authorware-bin")
            .put("aam", "application/x-authorware-map")
            .put("aas", "application/x-authorware-seg")
            .put("air", "application/vnd.adobe.air-application-installer-package+zip")
            .put("swf", "application/x-shockwave-flash")
            .put("fxp", "application/vnd.adobe.fxp")
            .put("pdf", "application/pdf")
            .put("ppd", "application/vnd.cups-ppd")
            .put("dir", "application/x-director")
            .put("xdp", "application/vnd.adobe.xdp+xml")
            .put("xfdf", "application/vnd.adobe.xfdf")
            .put("aac", "audio/x-aac")
            .put("ahead", "application/vnd.ahead.space")
            .put("azf", "application/vnd.airzip.filesecure.azf")
            .put("azs", "application/vnd.airzip.filesecure.azs")
            .put("azw", "application/vnd.amazon.ebook")
            .put("ami", "application/vnd.amiga.ami")
            .put("apk", "application/vnd.android.package-archive")
            .put("cii", "application/vnd.anser-web-certificate-issue-initiation")
            .put("fti", "application/vnd.anser-web-funds-transfer-initiation")
            .put("atx", "application/vnd.antix.game-component")
            .put("mpkg", "application/vnd.apple.installer+xml")
            .put("aw", "application/applixware")
            .put("les", "application/vnd.hhe.lesson-player")
            .put("swi", "application/vnd.aristanetworks.swi")
            .put("s", "text/x-asm")
            .put("atomcat", "application/atomcat+xml")
            .put("atomsvc", "application/atomsvc+xml")
            .put("atom", "application/atom+xml")
            .put("ac", "application/pkix-attr-cert")
            .put("aif", "audio/x-aiff")
            .put("avi", "video/x-msvideo")
            .put("aep", "application/vnd.audiograph")
            .put("dxf", "image/vnd.dxf")
            .put("dwf", "model/vnd.dwf")
            .put("par", "text/plain-bas")
            .put("bcpio", "application/x-bcpio")
            .put("bin", "application/octet-stream")
            .put("torrent", "application/x-bittorrent")
            .put("cod", "application/vnd.rim.cod")
            .put("bmi", "application/vnd.bmi")
            .put("sh", "application/x-sh")
            .put("btif", "image/prs.btif")
            .put("rep", "application/vnd.businessobjects")
            .put("bz", "application/x-bzip")
            .put("bz2", "application/x-bzip2")
            .put("csh", "application/x-csh")
            .put("c", "text/x-c")
            .put("cdxml", "application/vnd.chemdraw+xml")
            .put("css", "text/css")
            .put("cdx", "chemical/x-cdx")
            .put("cml", "chemical/x-cml")
            .put("csml", "chemical/x-csml")
            .put("cdbcmsg", "application/vnd.contact.cmsg")
            .put("cla", "application/vnd.claymore")
            .put("c4g", "application/vnd.clonk.c4group")
            .put("sub", "image/vnd.dvb.subtitle")
            .put("cdmia", "application/cdmi-capability")
            .put("cdmic", "application/cdmi-container")
            .put("cdmid", "application/cdmi-domain")
            .put("cdmio", "application/cdmi-object")
            .put("cdmiq", "application/cdmi-queue")
            .put("c11amc", "application/vnd.cluetrust.cartomobile-config")
            .put("c11amz", "application/vnd.cluetrust.cartomobile-config-pkg")
            .put("ras", "image/x-cmu-raster")
            .put("csv", "text/csv")
            .put("cpt", "application/mac-compactpro")
            .put("wmlc", "application/vnd.wap.wmlc")
            .put("cgm", "image/cgm")
            .put("ice", "x-conference/x-cooltalk")
            .put("cmx", "image/x-cmx")
            .put("xar", "application/vnd.xara")
            .put("cmc", "application/vnd.cosmocaller")
            .put("cpio", "application/x-cpio")
            .put("clkx", "application/vnd.crick.clicker")
            .put("clkk", "application/vnd.crick.clicker.keyboard")
            .put("clkp", "application/vnd.crick.clicker.palette")
            .put("clkt", "application/vnd.crick.clicker.template")
            .put("clkw", "application/vnd.crick.clicker.wordbank")
            .put("wbs", "application/vnd.criticaltools.wbs+xml")
            .put("cryptonote", "application/vnd.rig.cryptonote")
            .put("cif", "chemical/x-cif")
            .put("cmdf", "chemical/x-cmdf")
            .put("cu", "application/cu-seeme")
            .put("cww", "application/prs.cww")
            .put("dcurl", "text/vnd.curl.dcurl")
            .put("mcurl", "text/vnd.curl.mcurl")
            .put("scurl", "text/vnd.curl.scurl")
            .put("car", "application/vnd.curl.car")
            .put("pcurl", "application/vnd.curl.pcurl")
            .put("cmp", "application/vnd.yellowriver-custom-menu")
            .put("dssc", "application/dssc+der")
            .put("xdssc", "application/dssc+xml")
            .put("deb", "application/x-debian-package")
            .put("uva", "audio/vnd.dece.audio")
            .put("uvi", "image/vnd.dece.graphic")
            .put("uvh", "video/vnd.dece.hd")
            .put("uvm", "video/vnd.dece.mobile")
            .put("uvu", "video/vnd.uvvu.mp4")
            .put("uvp", "video/vnd.dece.pd")
            .put("uvs", "video/vnd.dece.sd")
            .put("uvv", "video/vnd.dece.video")
            .put("dvi", "application/x-dvi")
            .put("seed", "application/vnd.fdsn.seed")
            .put("dtb", "application/x-dtbook+xml")
            .put("res", "application/x-dtbresource+xml")
            .put("ait", "application/vnd.dvb.ait")
            .put("svc", "application/vnd.dvb.service")
            .put("eol", "audio/vnd.digital-winds")
            .put("djvu", "image/vnd.djvu")
            .put("dtd", "application/xml-dtd")
            .put("mlp", "application/vnd.dolby.mlp")
            .put("wad", "application/x-doom")
            .put("dpg", "application/vnd.dpgraph")
            .put("dra", "audio/vnd.dra")
            .put("dfac", "application/vnd.dreamfactory")
            .put("dts", "audio/vnd.dts")
            .put("dtshd", "audio/vnd.dts.hd")
            .put("dwg", "image/vnd.dwg")
            .put("geo", "application/vnd.dynageo")
            .put("es", "application/ecmascript")
            .put("mag", "application/vnd.ecowin.chart")
            .put("mmr", "image/vnd.fujixerox.edmics-mmr")
            .put("rlc", "image/vnd.fujixerox.edmics-rlc")
            .put("exi", "application/exi")
            .put("mgz", "application/vnd.proteus.magazine")
            .put("epub", "application/epub+zip")
            .put("eml", "message/rfc822")
            .put("nml", "application/vnd.enliven")
            .put("xpr", "application/vnd.is-xpr")
            .put("xif", "image/vnd.xiff")
            .put("xfdl", "application/vnd.xfdl")
            .put("emma", "application/emma+xml")
            .put("ez2", "application/vnd.ezpix-album")
            .put("ez3", "application/vnd.ezpix-package")
            .put("fst", "image/vnd.fst")
            .put("fvt", "video/vnd.fvt")
            .put("fbs", "image/vnd.fastbidsheet")
            .put("fe_launch", "application/vnd.denovo.fcselayout-link")
            .put("f4v", "video/x-f4v")
            .put("flv", "video/x-flv")
            .put("fpx", "image/vnd.fpx")
            .put("npx", "image/vnd.net-fpx")
            .put("flx", "text/vnd.fmi.flexstor")
            .put("fli", "video/x-fli")
            .put("ftc", "application/vnd.fluxtime.clip")
            .put("fdf", "application/vnd.fdf")
            .put("f", "text/x-fortran")
            .put("mif", "application/vnd.mif")
            .put("fm", "application/vnd.framemaker")
            .put("fh", "image/x-freehand")
            .put("fsc", "application/vnd.fsc.weblaunch")
            .put("fnc", "application/vnd.frogans.fnc")
            .put("ltf", "application/vnd.frogans.ltf")
            .put("ddd", "application/vnd.fujixerox.ddd")
            .put("xdw", "application/vnd.fujixerox.docuworks")
            .put("xbd", "application/vnd.fujixerox.docuworks.binder")
            .put("oas", "application/vnd.fujitsu.oasys")
            .put("oa2", "application/vnd.fujitsu.oasys2")
            .put("oa3", "application/vnd.fujitsu.oasys3")
            .put("fg5", "application/vnd.fujitsu.oasysgp")
            .put("bh2", "application/vnd.fujitsu.oasysprs")
            .put("spl", "application/x-futuresplash")
            .put("fzs", "application/vnd.fuzzysheet")
            .put("g3", "image/g3fax")
            .put("gmx", "application/vnd.gmx")
            .put("gtw", "model/vnd.gtw")
            .put("txd", "application/vnd.genomatix.tuxedo")
            .put("ggb", "application/vnd.geogebra.file")
            .put("ggt", "application/vnd.geogebra.tool")
            .put("gdl", "model/vnd.gdl")
            .put("gex", "application/vnd.geometry-explorer")
            .put("gxt", "application/vnd.geonext")
            .put("g2w", "application/vnd.geoplan")
            .put("g3w", "application/vnd.geospace")
            .put("gsf", "application/x-font-ghostscript")
            .put("bdf", "application/x-font-bdf")
            .put("gtar", "application/x-gtar")
            .put("texinfo", "application/x-texinfo")
            .put("gnumeric", "application/x-gnumeric")
            .put("kml", "application/vnd.google-earth.kml+xml")
            .put("kmz", "application/vnd.google-earth.kmz")
            .put("gqf", "application/vnd.grafeq")
            .put("gif", "image/gif");
    }
}
