package org.opensingular.lib.commons.util.format;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.util.FormatUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListFormatUtilTest {

    @Test
    public void formatListToString() {
        List<String> fruits = Arrays.asList("Apple", "Banana", "Grape", "Watermelon", "Melon");
        String formattedFruits = FormatUtil.formatListToString(fruits, ", ", " and ");
        Assert.assertEquals(formattedFruits, "Apple, Banana, Grape, Watermelon and Melon");

        formattedFruits = FormatUtil.formatListToString(fruits, ", ", " and ", "!");
        Assert.assertEquals(formattedFruits, "Apple, Banana, Grape, Watermelon and Melon!");

        formatListToStringWithLastCharacter();
    }

    @Test
    public void formatListToStringWithLastCharacter() {
        String one = FormatUtil.formatListToString(Collections.singletonList("One"), ", ", " and ", "!");
        Assert.assertEquals(one, "One!");
    }
}
