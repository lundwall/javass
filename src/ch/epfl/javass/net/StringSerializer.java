package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class helping serialization and deserialization of transmitted data.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class StringSerializer {

    // private constructor to never initialize the class
    private StringSerializer() {}

    public static String serializeBoolean(boolean b) {
        return Boolean.toString(b);
    }

    public static boolean deserializeBoolean(String s) {
        return Boolean.valueOf(s);
    }

    /**
     * Returns a string containing the int number in base 16.
     *
     * @param n the int number
     * @return the corresponding string of the number in base 16
     */
    public static String serializeInt(int n) {
        return Integer.toUnsignedString(n, 16);
    }

    /**
     * Returns the int number corresponding to a base 16 string.
     *
     * @param s the string to be deserialized
     * @return the original int number
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }

    /**
     * Returns a string containing the long number in base 16.
     *
     * @param n the long number
     * @return the corresponding string of the number in base 16
     */
    public static String serializeLong(long n) {
        return Long.toUnsignedString(n, 16);
    }

    /**
     * Returns the long number corresponding to a base 16 string.
     *
     * @param s the string to be deserialized
     * @return the original long number
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }

    /**
     * Returns a base 64 string of a UTF-8 string
     *
     * @param s the original UTF-8 string to be serialized
     * @return the corresponding string in base 64
     */
    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Returns the UTF-8 string corresponding to the serialized base 64 string
     *
     * @param s the base 64 string
     * @return the original UTF-8 string
     */
    public static String deserializeString(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    /**
     * Returns a concatenation of strings, separated by a separator string
     *
     * @param sep the separator (string)
     * @param ss all the strings to be joined
     * @return the corresponding concatenation
     */
    public static String join(String sep, String... ss) {
        return String.join(sep, ss);
    }

    /**
     * Returns an array of strings, taken from a concatenation with a separator string
     *
     * @param sep the separator between the strings
     * @param s the full string to be broken up
     * @return all individual strings that were joined together with the separator
     */
    public static String[] split(String sep, String s) {
        return s.split(sep);
    }

}
