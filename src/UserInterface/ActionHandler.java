package UserInterface;

import DocumentParser.ArgumentsParser.ArgumentsParser;
import DocumentParser.RunningMode;
import DocumentParser.StructureParser.FileRepresentation;
import DocumentParser.StructureParser.DocumentStructureException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;

/**
 * class ActionHandler
 *
 * Description:
 * Class responsible for firing methods according to program mode selected by user.
 */
public class ActionHandler {
    private ArgumentsParser parser;
    private OutputPresenter view;
    private FileRepresentation fileRepresentation;

    /**
     * Constructor
     * @param parser ArgumentParser object with parsed command-line arguments
     * @throws IOException in case of problem with file (filename from parser)
     * @throws DocumentStructureException in case of wrong file structure
     */
    public ActionHandler(ArgumentsParser parser) throws IOException, DocumentStructureException {
        this.parser = parser;
        view = new OutputPresenter();
        if(parser.getMode() != RunningMode.helpMode)
            fileRepresentation = new FileRepresentation(parser.getFilePath());
    }

    /**
     * Proceed action selected by user.
     * @throws InvalidKeyException in case of passing un-existing element name
     */
    public void proceedUserAction() throws FileNotFoundException, InvalidKeyException {
        if(parser.getMode() == RunningMode.helpMode) view.presentHelp();
        else {
            String output = null;
            switch (parser.getMode()) {
                case singleArticleMode:
                    output = fileRepresentation.getContentOfSingleArticle(parser.getSpecificElement());
                    break;
                case articleRangeMode:
                    output = fileRepresentation.getRangeOfArticles(parser.getRangeStart(), parser.getRangeEnd());
                    break;
                case specificElementMode:
                    output = fileRepresentation.getContentOfSingleElement(parser.getSpecificElement());
                    break;
                case allTOCMode:
                    output = fileRepresentation.getAllTOC();
                    break;
                case chapterTOCMode:
                    output = fileRepresentation.getCTOCofSingleChapter(parser.getSpecificElement());
                    break;
                case sectionTOCMode:
                    output = fileRepresentation.getTOCofSingleSection(parser.getSpecificElement());
                    break;
                case chapterMode:
                    output = fileRepresentation.getContentOfSingleChapter(parser.getSpecificElement());
                    break;
                case sectionMode:
                    output = fileRepresentation.getContentOfSingleSection(parser.getSpecificElement());
                    break;
                case documentRangeMode:
                    output = fileRepresentation.getContentFromRange(parser.getRangeStart(), parser.getRangeEnd());
                    break;
                case allContent:
                    output = fileRepresentation.getAllContent();
                    break;
                case elementTOCMode:
                    output = fileRepresentation.getTOCOfSingleElement(parser.getSpecificElement());
                    break;
            }
            view.presentOutput(output);
        }


    }
}
