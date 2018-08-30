package org.opensingular.form.wicket.behavior;

import org.apache.wicket.Component;

import java.util.Map;

/**
 * Defines the behaviour for the Regex input mask.
 * Uses the literal "X" as an extended definition for input mask
 * to validate the whole field with a regular expression pattern.
 */
public class RegexMaskBehaviour extends InputMaskBehavior {

    private String pattern;

    private static final Masks regexDefinition = Masks.valueOf("X");

    public RegexMaskBehaviour(Masks patternMask, Map<String, Object> options) {
        super(regexDefinition, options);
        this.pattern = patternMask.getMask();
    }

    /**
     * <p>Retorna o <i>script</i> gerado para este <i>behavior</i>.</p>
     *
     * @param component componente o qual este <i>behavior</i> deverá ser adicionado.
     * @return o <i>javascript</i> gerado.
     */
    @Override
    protected String getScript(Component component) {
       return getInputmaskExtendDefinitions()
               + super.getScript(component);
    }

    /**
     * <p>Retorna o <i>script</i> que extenda a definição do InputMask para utilizar um regex como validador da máscara.</p>
     *
     * @return o <i>javascript</i> gerado.
     */
    private String getInputmaskExtendDefinitions() {
        return "  Inputmask.extendDefinitions({\n" +
                "  '" + regexDefinition.getMask() + "': {\n" +
                "     validator: \"" + pattern + "\"\n" +
                "   }" +
                "});";
    }
}
