package DocumentParser.StructureParser;

/**
 * represents Root of document
 */
class RootNode extends TocPrintableNode{

    /**
     * Info about origin of document
     */
    private final String originInfo;
    /**
     * date of document
     */
    private final String date;
    /**
     * optional info about element
     */
    private final String about;

    /**
     * @param label key
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     * @param originInfo Info about origin of document
     * @param date date of document
     * @param about optional info about element
     */
    RootNode(StructureIndicator label, String title, String content, String heading, String originInfo,
                    String date, String about) {
        super(label, title, content, heading);
        this.originInfo = originInfo;
        this.date = date;
        this.about = about;
    }

    public String complexPrint() {
        StringBuilder result = new StringBuilder();
        if(originInfo != null){
            result.append("Źródło dokumentu: ");
            result.append(originInfo);
            result.append("\n");
        }
        result.append(title);
        result.append("\n");
        if(super.getHeading() != null){
            result.append(super.getHeading());
            result.append("\n");
        }
        if(date != null){
            result.append(date);
            result.append("\n");
        }
        if(about != null){
            result.append(about);
            result.append("\n");
        }
        result.append(content);
        result.append("\n\n");
        return result.toString();
    }

    @Override
    public String tocPrint(int indent) {
        StringBuilder result = new StringBuilder("");
        result.append(generateIndentation(indent));
        result.append(title);
        result.append(" ");
        if(super.getHeading() != null) result.append(super.getHeading());
        if(about != null) result.append(about);
        result.append("\n");
        return result.toString();
    }
}
