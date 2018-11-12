package org.opensingular.internal.lib.commons.util;

import org.assertj.core.util.Lists;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Daniel C. Bordin
 * @since 2018-09-25
 */
public class RandomUtilTest {

    @Test
    public void randomId() {
        assertThat(RandomUtil.generateID()).hasSize(22);
        assertThat(RandomUtil.generateID(128)).hasSize(22);
        assertThat(RandomUtil.generateID(256)).hasSize(43);
        assertThat(RandomUtil.generateID(8)).hasSize(2);
        assertThat(RandomUtil.generateID(6)).hasSize(1);
    }

    @Test
    public void randomPassword() {
        assertThat(RandomUtil.generateRandomPassword(8)).hasSize(8);
    }

    @Test
    public void selectRandom() {
        testSelectRandom(Lists.newArrayList("A", "B", "C"));
        testSelectRandom(Lists.newArrayList("A"));
        testSelectRandom(Lists.newArrayList());
    }

    private void testSelectRandom(@Nonnull ArrayList<String> list) {
        if (list.isEmpty()) {
            assertThat(RandomUtil.selectRandom(list)).isNull();
            assertThat(RandomUtil.selectRandom(new HashSet<>(list))).isNull();
        } else {
            assertThat(RandomUtil.selectRandom(list)).isIn(list);
            assertThat(RandomUtil.selectRandom(new HashSet<>(list))).isIn(list);
        }
    }
}