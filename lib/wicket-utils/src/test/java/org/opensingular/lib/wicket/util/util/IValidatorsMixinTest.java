package org.opensingular.lib.wicket.util.util;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.time.LocalDate;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.Validatable;
import org.junit.Assert;
import org.junit.Test;

public class IValidatorsMixinTest {

    @Test
    public void maxLength() {
        IValidatable<String> validatable = new Validatable<>("12345");
        $v.maxLength(3, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void minLength() {
        IValidatable<String> validatable = new Validatable<>("1");
        $v.minLength(3, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void notFutureDate() {
        IValidatable<LocalDate> validatable = new Validatable<>(LocalDate.now().plusDays(1));
        $v.notFutureDate($m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

    @Test
    public void validator() {
        IValidatable<String> validatable = new Validatable<>("");
        $v.validator((String it) -> true, $m.ofValue("error")).validate(validatable);
        Assert.assertFalse(validatable.isValid());
    }

}
