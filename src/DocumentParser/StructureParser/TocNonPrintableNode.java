package DocumentParser.StructureParser;

/**
 * Represents tre node which cannot be printed in TOC mode
 */
abstract class TocNonPrintableNode extends TrieNode{
    TocNonPrintableNode(StructureIndicator label, String title, String content, String heading) {
        super(label, title, content, heading);
    }
    public final String tocPrint(int indent) {
        return "";
    }
}
