package DocumentParser.StructureParser;


/**
 * Represents Article
 */
class ArticleNode extends TocPrintableNode {
    /**
     * @param label key
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     */
    ArticleNode(StructureIndicator label, String title, String content, String heading) {
        super(label, title, content, heading);
    }

    @Override
    public String complexPrint() {
        StringBuilder result = new StringBuilder();
        if (super.getHeading() != null) {
            result.append(super.getHeading());
            result.append("\n");
        }
        result.append(title);
        if(content != null){
            result.append("\n");
            result.append(content);
        }
        result.append("\n");
        return result.toString();
    }

    public String tocPrint(int indent) {
        StringBuilder result = new StringBuilder("");
        if(super.getHeading() != null){
            for (int i = 0; i < indent; i++) result.append("  ");
            result.append(super.getHeading());
            result.append("\n");
        }
        return result.toString();
    }
}
