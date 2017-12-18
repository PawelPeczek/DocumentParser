package DocumentParser;

/**
 * Interface connected with Observer design pattern
 */
public interface IObservable {
    /**
     * @param observer Object that implements IObserver and can be registered as
     *                 observer of IObservable object
     */
    void registerObserver(IObserver observer);

    /**
     * @param lvl Level of trie structure connected with "notify" message
     */
    void notifyObserver(int lvl);
}
