package org.opensingular.form.wicket.behavior;

import org.apache.wicket.Component;

import java.util.Map;

/**
 * Defines the behaviour for the Regex input mask.
 * <p>
 * Note: Uses the literal "X" as an extended definition for input mask to validate the whole field with a regular expression pattern.
 */
public class RegexMaskBehaviour extends InputMaskBehavior {

    private String pattern;

    private static final Masks regexDefinition = Masks.valueOf("X");

    public RegexMaskBehaviour(Masks patternMask, Map<String, Object> options) {
        super(regexDefinition, options);
        this.pattern = patternMask.getMask();
    }

    /**
     * Create the script for this behavior
     *
     * @param component componente o qual este <i>behavior</i> deverá ser adicionado.
     * @return InputMask script with regex Mask.
     */
    @Override
    protected String getScript(Component component) {
        return getInputmaskExtendDefinitions()
                + super.getScript(component);
    }

    /**
     * Create a the script with a extended Definition for the regex validator.
     *
     * @return script for InputMask.
     */
    private String getInputmaskExtendDefinitions() {
        return "  Inputmask.extendDefinitions({\n" +
                "  '" + regexDefinition.getMask() + "': {\n" +
                "     validator: \"" + pattern + "\"\n" +
                "   }" +
                "});";
    }
}
