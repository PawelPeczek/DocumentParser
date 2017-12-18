package DocumentParser.StructureParser;

import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * Facade on whole object document structure
 */
public class FileRepresentation {

    /**
     * Structure that hold the file
     */
    private StructureHolder parsedFile;

    /**
     * @param filePath Path to document file
     * @throws DocumentStructureException in case of wrong document structure
     * @throws IOException in case of troubles with file
     */
    public FileRepresentation(String filePath) throws DocumentStructureException, IOException {
        StructureProcessor structProc = new StructureProcessor(filePath);
        parsedFile = new StructureHolder(structProc.parsedLines);
        parsedFile.generateStructure();
    }

    /**
     * @return All content of document
     */
    public String getAllContent() {
        return parsedFile.getAllContent();
    }

    /**
     * @return TOC of whole document
     */
    public String getAllTOC(){
        return parsedFile.getAllTOC();
    }

    /**
     * @param labelStart Start of range
     * @param labelEnd End of range
     * @return Content of range
     * @throws InvalidKeyException in case of invalid String labels
     */
    public String getContentFromRange(String labelStart, String labelEnd) throws InvalidKeyException {
        return parsedFile.getContentFromRange(labelStart, labelEnd);
    }

    /**
     * @param label Label of element
     * @return TOC of single element
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getTOCOfSingleElement(String label) throws InvalidKeyException {
        return parsedFile.getTOCOfSingleElement(label);
    }

    /**
     * @param label Label of element
     * @return content of single element
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getContentOfSingleElement(String label) throws InvalidKeyException {
        return parsedFile.getContentOfSingleElement(label);
    }

    /**
     * @param label number of section
     * @return Content of section in document
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getContentOfSingleSection(String label) throws InvalidKeyException {
        return parsedFile.getContentOfSingleSection(label);
    }

    /**
     * @param label number of chapter
     * @return Content of chapter
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getContentOfSingleChapter(String label) throws InvalidKeyException {
        return parsedFile.getContentOfSingleChapter(label);
    }

    /**
     * @param label number of article
     * @return Content of article
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getContentOfSingleArticle(String label) throws InvalidKeyException {
        return parsedFile.getContentOfSingleArticle(label);
    }

    /**
     * @param label number of chapter
     * @return TOC of chapter in document
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getCTOCofSingleChapter(String label) throws InvalidKeyException {
        return parsedFile.getCTOCofSingleChapter(label);
    }

    /**
     * @param label number of section
     * @return TOC of section in document
     * @throws InvalidKeyException in case of invalid String label
     */
    public String getTOCofSingleSection(String label) throws InvalidKeyException {
        return parsedFile.getTOCofSingleSection(label);
    }

    /**
     * @param from Start of range (article)
     * @param to End of range (article)
     * @return Content of articles in range
     * @throws InvalidKeyException in case of invalid String labels
     */
    public String getRangeOfArticles(String from, String to) throws InvalidKeyException {
        return parsedFile.getRangeOfArticles(from, to);
    }

}
