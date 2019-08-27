package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Static methods to work with 32-bit vectors stored in a value of type int.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Bits32 {
    // private constructor makes the class non-instantiable
    private Bits32() {}

    /**
     * Creates a mask of desired size.
     *
     * @param start the index of the first bit equal to 1
     * @param size the number of 1s in total (start + size bit excluded)
     * @return the mask as an int (32 bits)
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0);
        Preconditions.checkArgument(start <= Integer.SIZE);
        Preconditions.checkArgument(size >= 0);
        Preconditions.checkArgument(start + size <= Integer.SIZE);
        if (size == Integer.SIZE) {
            return 0xffff_ffff;
        }
        return ((1 << size) - 1) << start;
    }

    /**
     * Reads a chosen section of the bit-string contained in an int.
     *
     * @param bits the target bit-string
     * @param start the index of the first bit equal to 1
     * @param size the number of bits being extracted in total (start + size bit excluded)
     * @return the extracted bits
     */
    public static int extract(int bits, int start, int size) {
        return (bits & mask(start, size)) >> start;
    }

    /**
     * Packs parts of two bit-strings into a 32-bit-string.
     *
     * @param v1 the first bit-string
     * @param s1 the number of LSBs of v1 to be included
     * @param v2 the second bit-string
     * @param s2 the number of LSBs of v2 to be included
     * @return a bit-string with v1 at the s1 LSBs, and then v2 at the s2 next LSBs
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(s1 + s2 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        return (v2 << s1) | v1;
    }

    /**
     * Packs parts of three bit-strings into a 32-bit-string.
     *
     * @param v1 the first bit-string
     * @param s1 the number of LSBs of v1 to be included
     * @param v2 the second bit-string
     * @param s2 the number of LSBs of v2 to be included
     * @param v3 the third bit-string
     * @param s3 the number of LSBs of v3 to be included
     * @return a bit-string with v1 at the s1 LSBs, v2 at the s2 next LSBs, and then v3 at the s3 next LSBs
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        Preconditions.checkArgument(s1 + s2 + s3 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        checkPack(v3, s3);
        return (v3 << (s2 + s1)) | (v2 << s1) | v1;
    }

    /**
     * Packs parts of seven bit-strings into a 32-bit-string.
     *
     * @param v1 the first bit-string
     * @param s1 the number of LSBs of v1 to be included
     * @param v2 the second bit-string
     * @param s2 the number of LSBs of v2 to be included
     * @param v3 the third bit-string
     * @param s3 the number of LSBs of v3 to be included
     * @param v4 the fourth bit-string
     * @param s4 the number of LSBs of v4 to be included
     * @param v5 the fifth bit-string
     * @param s5 the number of LSBs of v5 to be included
     * @param v6 the sixth bit-string
     * @param s6 the number of LSBs of v6 to be included
     * @param v7 the seventh bit-string
     * @param s7 the number of LSBs of v7 to be included
     * @return a bit-string with v1 at the s1 LSBs, v2 at the s2 next LSBs, and so on until v7 at the s7 next LSBs
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        Preconditions.checkArgument(s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE);
        checkPack(v1, s1);
        checkPack(v2, s2);
        checkPack(v3, s3);
        checkPack(v4, s4);
        checkPack(v5, s5);
        checkPack(v6, s6);
        checkPack(v7, s7);
        int v = v1;
        v |= (v2 << s1);
        v |= (v3 << (s2 + s1));
        v |= (v4 << (s3 + s2 + s1));
        v |= (v5 << (s4 + s3 + s2 + s1));
        v |= (v6 << (s5 + s4 + s3 + s2 + s1));
        v |= (v7 << (s6 + s5 + s4 + s3 + s2 + s1));
        return v;
    }

    // makes sure the arguments for pack are valid
    private static void checkPack(int v, int s) {
        Preconditions.checkArgument(s >= 1);
        Preconditions.checkArgument(s < Integer.SIZE);
        Preconditions.checkArgument(Integer.SIZE - Integer.numberOfLeadingZeros(v) <= s);
    }
}
