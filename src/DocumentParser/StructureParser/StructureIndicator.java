package DocumentParser.StructureParser;

import DocumentParser.IObservable;
import DocumentParser.IObserver;

/**
 * Class of actual keys in Trie
 */
class StructureIndicator implements IObservable{
    /**
     * Element indicator
     */
    final String indicator;

    /**
     * Parent key
     */
    final StructureIndicator parentIndicator;

    /**
     * Object that points at position in keys hierarchy
     */
    private final StructureHierarchy structHierarchy;

    /**
     * Object that collect all structure elements
     */
    private final StructureHolder structHolder;

    /**
     * Indicates whether the observer has already been assigned
     */
    private boolean isObserverSetOnce = false;

    /**
     * Indicates whether the observer has already been assigned
     */
    private boolean isObserverNotifiedOnce = false;

    private IObserver observer;
    /**
     * @param id String identificator of Key
     * @param parent Parent in key space
     * @param structHolder Object that collect all structure elements
     */
    StructureIndicator(String id, StructureIndicator parent, StructureHolder structHolder) {
        indicator = id;
        parentIndicator = parent;
        this.structHolder = structHolder;
        registerObserver(structHolder);
        this.structHierarchy = new StructureHierarchy(parent, structHolder);
        notifyObserver(structHierarchy.level);
    }

    /**
     * @return Level in key space hierarchy
     */
    Integer getLevelInHierarchy(){
        return structHierarchy.level;
    }

    @Override
    public String toString() {
        return indicator;
    }

    /**
     * @param o Other key
     * @return LCA(this, o) in key hierarchy
     */
    StructureIndicator getLCA(StructureIndicator o) {
        StructureIndicator itThis = this;
        StructureIndicator itO = o;
        while (!itThis.equals(itO)){
            if(itThis.structHierarchy.level.compareTo(itO.structHierarchy.level) <= 0) itO = itO.parentIndicator;
            else itThis = itThis.parentIndicator;
        }
        return itThis;
    }

    /**
     * @param LCA LCA of the key in key space hierarchy
     * @return StructureIndicator of first node at path from LCA to the key
     */
    private StructureIndicator extendLCAWithChild(StructureIndicator LCA){
        StructureIndicator result = LCA;
        if(!this.equals(LCA)){
          result = this;
          while (result.parentIndicator != LCA) result = result.parentIndicator;
        }
        return result;
    }

    /**
     * @param o Other StructureIndicator to compare
     * @return result of structHolder caparison of two StructureIndicators
     */
    private boolean checkStructureHolders(StructureIndicator o){
        // default equals only based on objects memory addresses
        return this.structHolder.equals(o.structHolder);
    }

    /**
     * IMPORTANT!
     *  This method works correctly if and only if two indicators are keys of the same structure!!!
     *
     * @param o Other StructureIndicator to compare relative position in Linear Order of Indicators
     * @return 0 if the same (based on level in tree and position at this level) position in linear order,
     * -1 if order is this, ... , o otherwise 1 ( o, ... , this )
     */
    int relativePosInLinOrder(StructureIndicator o) {
        if (o == null) throw new NullPointerException("Cannot compare object to null.");
        if(!this.checkStructureHolders(o))
            throw new UnsupportedOperationException("It is not allowed to compare keys from distinct structures.");
        int result;
        if(structHierarchy.level.equals(o.structHierarchy.level)){
            // At the same level -> only relative position matters
            result = Integer.compare(structHierarchy.positionAtLevel, o.structHierarchy.positionAtLevel);
        } else {
            // Otherwise there is need to find the relative position of LCA children corresponding
            // to appropriate key
            StructureIndicator LCA = getLCA(o);
            StructureIndicator representantOfThis = extendLCAWithChild(LCA); // special case
            StructureIndicator representantOfO = o.extendLCAWithChild(LCA); // special case
            if(representantOfThis.equals(LCA)) result = -1;
            else if(representantOfO.equals(LCA)) result = 1;
            else result = Integer.compare(representantOfThis.structHierarchy.positionAtLevel,
                            representantOfO.structHierarchy.positionAtLevel);
        }
        return result;
    }

    @Override
    public void registerObserver(IObserver observer) {
        if(!isObserverSetOnce) {
            this.observer = observer;
            isObserverSetOnce = true;
        } else throw new UnsupportedOperationException("Programming error! Error code 30090");
    }

    @Override
    public void notifyObserver(int lvl) {
        if (observer != null && !isObserverNotifiedOnce){
            observer.ActualizeRegister(lvl);
            isObserverNotifiedOnce = true;
        } else throw new UnsupportedOperationException("Programming error! Error code 30091");
    }
}
