package ch.epfl.javass;

/**
 * Static methods to throw exceptions if arguments are not possible.
 *
 * @author Marc Lundwall (297665)
 * @author Pablo Stebler (302328)
 */
public final class Preconditions {
    // private constructor makes the class non-instantiable
    private Preconditions() {}

    /**
     * Checks if an argument is true, throwing an exception otherwise.
     *
     * @param b the proposition to be evaluated
     * @throws IllegalArgumentException if the statement is false
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns an index only if it is in a possible range, else throws an exception.
     *
     * @param index the index to check
     * @param size the size of the possible range of the index
     * @throws IndexOutOfBoundsException if the index is not between zero (included) and a specified size (excluded)
     * @return the index only if it is between these bounds
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException();
        } else {
            return index;
        }
    }
}
