/*
 *
 *  * Copyright (C) 2018 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileUploadException;
import org.opensingular.lib.commons.util.Loggable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton that handles upload strategies from a {@link javax.servlet.Servlet} instance.
 * <p>
 * Process lifecycle:
 * <ul>
 * <li>{@link ServletFileUploadStrategy#accept(HttpServletRequest)}</li>
 * <li>{@link ServletFileUploadStrategy#init()}</li>
 * <li>{@link ServletFileUploadStrategy#process(HttpServletRequest, HttpServletResponse)}</li>
 * </ul>
 */
public final class ServletFileUploadStrategyHandler implements Loggable {

    private static final ServletFileUploadStrategyHandler INSTANCE = new ServletFileUploadStrategyHandler();
    private final ServletFileUploadStrategy DEFAULT_STRATEGY = new AttachmentKeyStrategy();

    private ServletFileUploadStrategyHandler() {
        // Default constructor
    }

    /**
     * The only way to get the instance of this class.
     *
     * @return unique instance of {@link ServletFileUploadStrategyHandler}.
     */
    public static ServletFileUploadStrategyHandler getInstance() {
        return INSTANCE;
    }

    /**
     * It processes a strategy instance according to the chosen instance.
     *
     * @param request  instace of a servlet request.
     * @param response request instace of a servlet response.
     * @throws ServletException    servlet exception.
     * @throws FileUploadException file upload exception.
     * @throws IOException         I/O exception.
     */
    public void processFileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, FileUploadException, IOException {
        ServletFileUploadStrategy strategy = chooseStrategy(request, response);
        strategy.init();
        strategy.process(request, response);
    }

    /**
     * Choose the strategy to be applied to upload a file based on request parameter called (TODO ver com o Bud).
     * If there is no strategy available, returns the default one.
     *
     * @param request  instace of a servlet request.
     * @param response request instace of a servlet response.
     * @return an implementation of {@link ServletFileUploadStrategy}.
     */
    private ServletFileUploadStrategy chooseStrategy(HttpServletRequest request, HttpServletResponse response) {
        for (ServletFileUploadStrategy strategy : listAvailableStrategies()) {
            if (strategy.accept(request)) {
                return strategy;
            }
        }

        getLogger().error("No strategy defined, using default: {}", DEFAULT_STRATEGY.getClass().getSimpleName());

        return DEFAULT_STRATEGY;
    }

    /**
     * Lists all available strategies.
     *
     * @return list of available strategies.
     */
    private List<ServletFileUploadStrategy> listAvailableStrategies() {
        return Arrays.asList(new ServletFileUploadStrategy[]{
                new AttachmentKeyStrategy(),
                new SimplePostFilesStrategy()});
    }
}
