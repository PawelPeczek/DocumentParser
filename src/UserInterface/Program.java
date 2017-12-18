package UserInterface;

import DocumentParser.ArgumentsParser.ArgumentsParser;
import DocumentParser.ArgumentsParser.ArgumentsParserException;
import DocumentParser.StructureParser.DocumentStructureException;

import java.io.*;
import java.security.InvalidKeyException;

public class Program {
    public static void main(String[] args){
        OutputPresenter view = new OutputPresenter();
        try {
            ArgumentsParser argParser = new ArgumentsParser(args);
            ActionHandler actHandler = new ActionHandler(argParser);
            actHandler.proceedUserAction();
        } catch(ArgumentsParserException e){
            view.presentArgParsEx(e.getMessage());
        } catch (IOException e) {
            view.presentFileEx(e.getMessage());
        } catch (InvalidKeyException e) {
            view.presentInvalidKeyEx(e.getMessage());
        } catch (DocumentStructureException e) {
            view.presentDocStructEx(e.getMessage());
        } catch (Exception e){
            view.presentUnknownEx(e.getMessage());
        }
    }
}
