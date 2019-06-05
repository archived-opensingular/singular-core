package org.opensingular.lib.commons.util.format;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class ListFormatUtil {

    public static String formatListToString(List<String> listOfWords, String joiner, String lastJoiner, String lastCharacter) {
        return formatListToString(listOfWords, joiner, lastJoiner).concat(lastCharacter);
    }

    public static String formatListToString(List<String> listOfWords, String joiner, String lastJoiner) {
        if (CollectionUtils.isNotEmpty(listOfWords)) {
            if (listOfWords.size() == 1) {
                return listOfWords.get(0);
            }
            int last = listOfWords.size() - 1;
            return String.join(lastJoiner,
                    String.join(joiner, listOfWords.subList(0, last)),
                    listOfWords.get(last));
        }
        return "";
    }
}
