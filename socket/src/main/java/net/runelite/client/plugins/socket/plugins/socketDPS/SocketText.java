package net.runelite.client.plugins.socket.plugins.socketDPS;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;

public class SocketText {
    private static final StringBuilder SB = new StringBuilder(64);

    private static final Pattern TAG_REGEXP = Pattern.compile("<[^>]*>");

    public static final JaroWinklerDistance DISTANCE = new JaroWinklerDistance();

    public static final Splitter COMMA_SPLITTER = Splitter.on(",")
            .omitEmptyStrings()
            .trimResults();

    private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();

    public static List<String> fromCSV(String input) {
        return COMMA_SPLITTER.splitToList(input);
    }

    public static String toCSV(Collection<String> input) {
        return COMMA_JOINER.join(input);
    }

    public static String removeTags(String str, boolean removeLevels) {
        int strLen = str.length();
        if (removeLevels) {
            int levelIdx = StringUtils.lastIndexOf(str, "  (level");
            if (levelIdx >= 0)
                strLen = levelIdx;
        }
        int open, close;
        if ((open = StringUtils.indexOf(str, 60)) == -1 || (
                close = StringUtils.indexOf(str, 62, open)) == -1)
            return (strLen == str.length()) ? str : str.substring(0, strLen - 1);
        if (open == 0) {
            if ((open = close + 1) >= strLen)
                return "";
            if ((open = StringUtils.indexOf(str, 60, open)) == -1 ||
                    StringUtils.indexOf(str, 62, open) == -1)
                return StringUtils.substring(str, close + 1);
            open = 0;
        }
        SB.setLength(0);
        int i = 0;
        do {
            while (open != i)
                SB.append(str.charAt(i++));
            i = close + 1;
        } while ((open = StringUtils.indexOf(str, 60, close)) != -1 && (
                close = StringUtils.indexOf(str, 62, open)) != -1 && i < strLen);
        while (i < strLen)
            SB.append(str.charAt(i++));
        return SB.toString();
    }

    public static String removeTags(String str) {
        return removeTags(str, false);
    }

    public static String standardize(String str, boolean removeLevel) {
        if (StringUtils.isBlank(str))
            return str;
        return removeTags(str, removeLevel).replace(' ', ' ').trim().toLowerCase();
    }

    public static String standardize(String str) {
        return standardize(str, false);
    }

    public static String toJagexName(String str) {
        char[] chars = str.toCharArray();
        int newIdx = 0;
        for (int oldIdx = 0, strLen = str.length(); oldIdx < strLen; oldIdx++) {
            char c = chars[oldIdx];
            if (c == '-' || c == '_' || c == ' ') {
                if (oldIdx == strLen - 1 || newIdx == 0 || chars[newIdx - 1] == ' ')
                    continue;
                c = ' ';
            }
            if (c <= '')
                chars[newIdx++] = c;
        }
    return new String(chars, 0, newIdx);
}


    public static String sanitizeMultilineText(String str) {
        return removeTags(str
                .replaceAll("-<br>", "-")
                .replaceAll("<br>", " ")
                .replaceAll("[ ]+", " "));
    }

    public static String escapeJagex(String str) {
        StringBuilder out = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '<') {
                out.append("<lt>");
            } else if (c == '>') {
                out.append("<gt>");
            } else if (c == '\n') {
                out.append("<br>");
            } else if (c != '\r') {
                out.append(c);
            }
        }
        return out.toString();
    }

    public static String sanitize(String name) {
        String cleaned = name.contains("<img") ? name.substring(name.lastIndexOf('>') + 1) : name;
        return cleaned.replace(' ', ' ');
    }

    public static String titleCase(Enum o) {
        String toString = o.toString();
        if (o.name().equals(toString))
            return
                    WordUtils.capitalize(toString.toLowerCase(), new char[] { '_' }).replace('_', ' ');
        return toString;
    }

    public static boolean matchesSearchTerms(String[] searchTerms, Collection<String> keywords) {
        for (String term : searchTerms) {
            if (keywords.stream().noneMatch(t -> (t.contains(term) || DISTANCE.apply(t, term).doubleValue() > 0.9D)))
                return false;
        }
        return true;
    }
}
