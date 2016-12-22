package org.opensingular.form.wicket.mapper.attachment.upload.manager;

import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileUploadManagerTest {



}