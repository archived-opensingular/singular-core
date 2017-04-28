package org.opensingular.form.wicket;

import org.junit.Test;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.helpers.SingularDummyFormPageTester;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

public class SValidationFeedbackHandlerTest {

    @Test
    public void testBasic() {
        SingularDummyFormPageTester tester = new SingularDummyFormPageTester();
        tester.getDummyPage().setTypeBuilder(tb->tb.addFieldString("string"));
        tester.getDummyPage().setAsEditView();
        tester.startDummyPage();

        Set<? extends SInstance> lowerBound = SValidationFeedbackHandler.collectLowerBoundInstances(
                new FeedbackFence(tester.getDummyPage().getSingularFormPanel().getParent()));

        assertThat(lowerBound.isEmpty()).isFalse();
    }
}
