package DocumentParser.StructureParser;

/**
 * Represents standard element
 */
class StandardNode extends TocNonPrintableNode {
    /**
     * @param label key
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     */
    StandardNode(StructureIndicator label, String title, String content, String heading) {
        super(label, title, content, heading);
    }
}
