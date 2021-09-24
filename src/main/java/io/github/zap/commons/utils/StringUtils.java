package io.github.zap.commons.utils;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Contains several methods to help with string manipulation
 */
public class StringUtils {
    /**
     * Comma seperated strings, comma can be included by prepending \
     * @param s input string
     */
    @NotNull
    public static List<String> fromCommaSeperated(@Nullable String s) {
        if(Strings.isNullOrEmpty(s))
            return Collections.singletonList(s);
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        Optional<Character> lastCharacter = Optional.of(s.charAt(0));
        char[] chars = s.toCharArray();
        for (int i = 1; i < chars.length; i++) {
            if (lastCharacter.isPresent()) {
                if(chars[i] == ',') {
                    if(lastCharacter.get() == '\\') {
                        sb.append(",");
                    } else {
                        sb.append(lastCharacter.get());
                        result.add(sb.toString());
                        sb = new StringBuilder();
                    }

                    lastCharacter = Optional.empty();
                } else {
                    sb.append(lastCharacter.get());
                    lastCharacter = Optional.of(chars[i]);
                }
            } else {
                lastCharacter = Optional.of(chars[i]);
            }
        }

        sb.append(lastCharacter.get());
        result.add(sb.toString());

        return result;
    }
}
