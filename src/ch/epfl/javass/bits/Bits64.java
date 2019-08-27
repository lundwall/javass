package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Static methods to work with 64-bit vectors stored in a value of type long.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Bits64 {
    // private constructor makes the class non-instantiable
    private Bits64() {}

    /**
     * Creates a mask of desired size.
     *
     * @param start the index of the first bit equal to 1
     * @param size the number of 1s in total (start + size bit excluded)
     * @return the mask as a long (64 bits)
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0);
        Preconditions.checkArgument(start <= Long.SIZE);
        Preconditions.checkArgument(size >= 0);
        int end = start + size;
        Preconditions.checkArgument(end <= Long.SIZE);
        if (size == Long.SIZE) {
            return 0xffff_ffff_ffff_ffffL;
        }
        return ((1L << size) - 1L) << start;
    }

    /**
     * Reads a chosen section of the bit-string contained in a long.
     *
     * @param bits the target bit-string
     * @param start the index of the first bit equal to 1
     * @param size the number of bits being extracted in total (start + size bit excluded)
     * @return the extracted bits
     */
    public static long extract(long bits, int start, int size) {
        return (bits & mask(start, size)) >> start;
    }

    /**
     * Packs parts of two bit-strings into a 64-bit-string.
     *
     * @param v1 the first bit-string
     * @param s1 the number of LSBs of v1 to be included
     * @param v2 the second bit-string
     * @param s2 the number of LSBs of v2 to be included
     * @return a bit-string with v1 at the s1 LSBs, and then v2 at the s2 next LSBs
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        return (v2 << s1) | v1;
    }

    // makes sure the arguments for pack are valid
    private static void checkPack(long v, int s) {
        Preconditions.checkArgument(s >= 1);
        Preconditions.checkArgument(s <= 63);
        Preconditions.checkArgument(Long.SIZE - Long.numberOfLeadingZeros(v) <= s);
    }
}
