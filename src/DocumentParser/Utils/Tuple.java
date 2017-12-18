package DocumentParser.Utils;

/**
 * Util Tuple class
 * @param <T> first element of Tuple type
 * @param <K> second element of Tuple type
 * @param <Z> third element of Tuple type
 */
public class Tuple<T, K, Z> {
    /**
     * first element of Tuple
     */
    public final T fst;
    /**
     * second element of Tuple
     */
    public final K snd;
    /**
     * third element of Tuple
     */
    public final Z thd;

    /**
     * @param first first element of Tuple
     * @param second second element of Tuple
     * @param third third element of Tuple
     */
    public Tuple(T first, K second, Z third){
        fst = first;
        snd = second;
        thd = third;
    }

    @Override
    public String toString() {
        return "(" + fst + ", " + snd + ", " + thd + ")";
    }
}
