package DocumentParser.ArgumentsParser;


/**
 * Exception connected with parsing errors
 */
public class ArgumentsParserException extends Exception {
    public ArgumentsParserException() { super(); }
    public ArgumentsParserException(String message) { super(message); }
    public ArgumentsParserException(String message, Throwable cause) { super(message, cause); }
    public ArgumentsParserException(Throwable cause) { super(cause); }
}
