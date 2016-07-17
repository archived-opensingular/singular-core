package br.net.mirante.singular.server.commons.wicket.error;

import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import org.apache.log4j.lf5.LogLevel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.*;

/**
 * Created by nuk on 01/07/16.
 */
public class Page500Content extends Content{
    private final static Logger LOGGER = Logger.getLogger("GENERAL_LOGGER");

    private final Exception exception;

    public Page500Content(String id, Exception exception){
        super(id);
        this.exception = exception;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        String errorCode = errorCode();
        LOGGER.log(Level.WARNING, errorCode, this.exception);
        queue(new Label("codigo-erro",Model.of(errorCode)));
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of("500");
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of("Algo inesperado aconteceu");
    }

    private static String errorCode() {
        DateTime now = DateTime.now();
        return format("SER-%04d-%02d%02d%02d-%02d%02d-%04d ",
                get(now.year()), get(now.monthOfYear()), get(now.dayOfMonth()),
                get(now.hourOfDay()), get(now.minuteOfHour()), get(now.secondOfMinute()),
                get(now.millisOfSecond()));
    }

    private static int get(DateTime.Property prop) {
        return prop.get();
    }
}
