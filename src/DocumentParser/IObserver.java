package DocumentParser;

/**
 * Interface connected with Observer design pattern
 */
public interface IObserver {
    /**
     * "update" part of Observer design pattern
     * @param lvl level of indentation
     */
    void ActualizeRegister(int lvl);
}
