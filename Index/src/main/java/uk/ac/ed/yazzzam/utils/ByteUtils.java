// BSD License (http://lemurproject.org/galago-license)
package uk.ac.ed.yazzzam.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author jfoley
 */
public class ByteUtils {
    public static Charset utf8 = StandardCharsets.UTF_8;
    public static final byte[] EmptyArr = new byte[0];

    public static byte[] fromString(String word) {
        return word.getBytes(utf8);
    }

    public static byte[] fromStringNullTerminated(String word) {
        return String.format("%s\0", word).getBytes(utf8);
    }


    public static String toString(byte[] word) {
        return new String(word, utf8);
    }

    public static String toString(byte[] buffer, int offset, int len) {
        return new String(buffer, offset, len, utf8);
    }

    public static String toString(byte[] buffer, int len) {
        return new String(buffer, 0, len, utf8);
    }
}
