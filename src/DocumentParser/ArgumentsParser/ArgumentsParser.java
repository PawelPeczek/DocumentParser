package DocumentParser.ArgumentsParser;

import DocumentParser.RunningMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for parsing command-line parameters
 */
public class ArgumentsParser {
    /**
     * Running mode of current instantiation of program
     */
    private RunningMode mode;

    /**
     * Path to document file
     */
    private String filePath;
    /**
     * range start element (if exists - depends on mode)
     */
    private String rangeStart;
    /**
     * range end element (if exists - depends on mode)
     */
    private String rangeEnd;
    /**
     * single element label (if exists - depends on mode)
     */
    private String specificElement;

    /**
     * checks whether good mode pattern -m=code where code is [a-z]{1,4}
     */
    private Pattern modePredicate = Pattern.compile("^-m=([a-z]{1,4})$");

    /**
     * @param args command-line arguments
     * @throws ArgumentsParserException in case of parsing error
     */
    public ArgumentsParser(String args[]) throws ArgumentsParserException{
        setRunningMode(args);
        if(mode != RunningMode.helpMode) filePath = args[0];
        parseArgumentsByMode(args);
    }

    /**
     * Set appropriate running mode
     * @param args command-line args
     * @throws ArgumentsParserException in case of parsing error
     */
    private void setRunningMode(String[] args) throws ArgumentsParserException {
        if(args.length == 0) throw new ArgumentsParserException("No arguments provided. Check -h.");
        if(args.length > 1){
            Matcher matcher = modePredicate.matcher(args[1]);
            if(matcher.matches()){
                String mode = matcher.group(1).toLowerCase();
                switch (mode){
                    case "sa":
                        this.mode = RunningMode.singleArticleMode;
                        break;
                    case "ar":
                        this.mode = RunningMode.articleRangeMode;
                        break;
                    case "se":
                        this.mode = RunningMode.specificElementMode;
                        break;
                    case "toca":
                        this.mode = RunningMode.allTOCMode;
                        break;
                    case "tocc":
                        this.mode = RunningMode.chapterTOCMode;
                        break;
                    case "tocs":
                        this.mode = RunningMode.sectionTOCMode;
                        break;
                    case "c":
                        this.mode = RunningMode.chapterMode;
                        break;
                    case "s":
                        this.mode = RunningMode.sectionMode;
                        break;
                    case "ext":
                        this.mode = RunningMode.documentRangeMode;
                        break;
                    case "ac":
                        this.mode = RunningMode.allContent;
                        break;
                    case "toce":
                        this.mode = RunningMode.elementTOCMode;
                        break;
                    default:
                        throw new ArgumentsParserException("Wrong mode! Try to use --h to see the valid modes list.");
                }
            } else throw new ArgumentsParserException("Wrong second argument pattern. Valid one is -m=mode. " +
                    "Mode is string of characters with length from range [1;4].");
        } else {
            if(args[0].equals("-h")) this.mode = RunningMode.helpMode;
            else throw new ArgumentsParserException("Wrong parameters passed to the program. Try to use -h to" +
                    " see valid parameters.");
        }
    }

    /**
     * parse rest of arguments according to mode
     * @param args command-line args
     * @throws ArgumentsParserException in case of parsing error
     */
    private void parseArgumentsByMode(String[] args) throws ArgumentsParserException {
        boolean exFlag = false;
        String exMessage = null;
        switch (mode){
            case helpMode:
                if(args.length != 1){
                    exFlag = true;
                    exMessage = "With Help mode - no other arguments are allowed.";
                }
                break;
            case documentRangeMode:
            case articleRangeMode:
                if(args.length != 4) {
                    exFlag = true;
                    exMessage = "With ArticlesRange and DocumentRange mode expected arguments are two article numbers.";
                } else {
                    rangeStart = args[2];
                    rangeEnd = args[3];
                }
                break;
            case allContent:
            case allTOCMode:
                if(args.length != 2){
                    exFlag = true;
                    exMessage = "With AllTOC and AllContent mode no more arguments are allowed.";
                }
                break;
            case singleArticleMode:
            case specificElementMode:
            case chapterTOCMode:
            case sectionTOCMode:
            case chapterMode:
            case sectionMode:
            case elementTOCMode:
                if(args.length != 3){
                    exFlag = true;
                    exMessage = "With Chapter, ChapterTOC, Section, SectionTOC," +
                            "SpecificElement, ElementTOC, SingleArticle and DocumentRange modes only " +
                            "one argument (section identifier) is allowed.";
                } else specificElement = args[2];
                break;
        }
        if(exFlag) throw new ArgumentsParserException(exMessage);
    }


    /**
     * @return current running mode
     */
    public RunningMode getMode() {
        return mode;
    }

    /**
     * @return current file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return current range start
     */
    public String getRangeStart() {
        return rangeStart;
    }

    /**
     * @return current range end
     */
    public String getRangeEnd() {
        return rangeEnd;
    }

    /**
     * @return current specific element field
     */
    public String getSpecificElement() {
        return specificElement;
    }
}
