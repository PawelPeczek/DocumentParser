package DocumentParser.StructureParser;

/**
 * Represents tre node which can be printed in TOC mode
 */
abstract class TocPrintableNode extends TrieNode {
    TocPrintableNode(StructureIndicator label, String title, String content, String heading) {
        super(label, title, content, heading);
    }
    public String tocPrint(int indent) {
        return generateIndentation(indent).toString() + title + "\n";
    }
}
