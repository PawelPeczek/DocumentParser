package DocumentParser.StructureParser;

/**
 * Represents information about hierarchy in key space
 */
class StructureHierarchy {
    /**
     * Level in hierarchy
     */
    final Integer level;
    /**
     * Position of Key at its level
     */
    final Integer positionAtLevel;

    /**
     * @param parent parent of key
     * @param structHolder object that aggregates key
     */
    StructureHierarchy(StructureIndicator parent, StructureHolder structHolder) {
        if(parent == null) level = 0;
        else level = parent.getLevelInHierarchy() + 1;
        positionAtLevel = structHolder.getOrderAtLevel(level) + 1;
    }
}
