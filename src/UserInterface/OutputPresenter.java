package UserInterface;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * class OutputPresenter
 *
 * Description:
 * Presenting program output with separation from whole processing
 * done by other program parts.
 */
public class OutputPresenter {
    /**
     * method presentHelp
     *
     * Description:
     * Method presents help page.
     */
    public void presentHelp(){
        System.out.println("\nHELP PAGE:\n");
        System.out.println("-h\t\tOption that shows help page.");
        System.out.println("filename -m=mode\t\tOption that allows to parse document from filename and receive " +
                "output according to selected mode.");
        System.out.println("MODES LIST and its valid parameters:");
        System.out.println("\tac\t\tWhole document content.");
        System.out.println("\tsa art_number\t\tSingle article ex -m=sa 123a");
        System.out.println("\tar art_number art_number\t\tRange of articles ex -m=ar 123a 151");
        System.out.println("\tse element_full_coord*\t\tSingle element ex -m=se Art. 2., ust. 1., lit c)");
        System.out.println("\t*valid coordinate starts with indexed element (Chapter/section or article) and its parts" +
                "are separated with ', '.");
        System.out.println("\tc chapter_num\t\tChapter content (if there are chapters in document and there are no " +
                "Sections), ex -m=c 1");
        System.out.println("\ts chapter_num\t\tSection content (if there are sections in document), ex -m=s I");
        System.out.println("\text from_coord to_coord\t\tDocument content from from_coord to to_coord " +
                "(both full valid coordinates).");
        System.out.println("\ttoca\t\tTOC of whole content.");
        System.out.println("\ttocc chapter_num\t\tTOC of given chapter (restrictions - see c), ex -m=tocc 1");
        System.out.println("\ttocs chapter_num\t\tTOC of given section (restrictions - see s), ex -m=tocs I");
        System.out.println("\ttoce elem_coord\t\tTOC of given element (element - full valid coordinate).");
    }

    /**
     * method presentOutput
     *
     * Description:
     * Method presents program output.
     */
    public void presentOutput(String output) throws FileNotFoundException {
        System.out.println(output);
    }

    /**
     * method presentFileEx
     *
     * Description:
     * Method presents error page connected with exceptions caused by file handlinig.
     */
    public void presentFileEx(String msg){
        msg = msg == null ? "Cause unknown." : msg;
        System.out.println("ERROR!\nThis message is connected with filename given as an program argument. Check " +
                "if the file is available at location and has appropriate permission mode:\n" + msg);
    }

    /**
     * method presentDocStructEx
     *
     * Description:
     * Method presents error messege connected with DocumentStructureException
     */
    public void presentDocStructEx(String msg){
        msg = msg == null ? "Cause unknown." : msg;
        System.out.println("ERROR!\nDocument structure is invalid. See details below and " +
                "contact pawel.m.peczek@gmail.com if necessary:\n" + msg);
    }

    /**
     * method presentUnknownEx
     *
     * Description:
     * Method presents error messege connected with InvalidKeyException
     */
    public void presentInvalidKeyEx(String msg){
        msg = msg == null ? "Cause unknown." : msg;
        System.out.println("ERROR!\nDocument element name passed as argument is invalid. " +
                "See details below:\n" + msg);
    }

    /**
     * method presentUnknownEx
     *
     * Description:
     * Method presents error message connected with unknown errors.
     */
    public void presentUnknownEx(String msg){
        msg = msg == null ? "Cause unknown." : msg;
        System.out.println("ERROR!\nSomething went wrong! See details below and " +
                "contact pawel.m.peczek@gmail.com immediately (if error code is presented please" +
                " include it, otherwise include short description of problem) with document you" +
                " were trying to parse attached.):\n" + msg);
    }

    /**
     * method presentArgParsEx
     *
     * Description:
     * Method presents error message connected with ArgumentParserException
     */
    public void presentArgParsEx(String msg){
        msg = msg == null ? "Cause unknown." : msg;
        System.out.println("ERROR!\nSomething wrong happened with arguments passed to the program. " +
                "See details below:\n" + msg);
    }
}
