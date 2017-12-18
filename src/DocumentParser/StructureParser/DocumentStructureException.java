package DocumentParser.StructureParser;

/**
 * Exception connected with wrong document structure
 */
public class DocumentStructureException extends Exception {
    public DocumentStructureException() { super(); }
    public DocumentStructureException(String message) { super(message); }
    public DocumentStructureException(String message, Throwable cause) { super(message, cause); }
    public DocumentStructureException(Throwable cause) { super(cause); }
}
