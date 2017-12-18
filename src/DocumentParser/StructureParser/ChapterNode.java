package DocumentParser.StructureParser;

/**
 * Represent Chapter
 */
class ChapterNode extends TocPrintableNode {
    /**
     * @param label key
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     */
    ChapterNode(StructureIndicator label, String title, String content, String heading) {
        super(label, title, content, heading);
    }

    @Override
    public String complexPrint() {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        result.append(title);
        result.append("\n");
        if (super.getHeading() != null) {
            result.append(super.getHeading());
            result.append("\n");
        }
        if(content != null) {
            result.append(content);
            result.append("\n");
        }
        result.append("\n");
        return result.toString();
    }

    @Override
    public String tocPrint(int indent) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < indent; i++) result.append("  ");
        result.append(title);
        result.append("\t");
        if(super.getHeading() != null) {
            result.append(super.getHeading());
            result.append(" ");
        }
        if(content != null){
            result.append(content);
        }
        result.append("\n");
        return result.toString();
    }
}
