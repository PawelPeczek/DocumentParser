package DocumentParser.StructureParser;

/**
 * Interface that allows to be printed in two ways
 */
interface IPrintable {
    /**
     * @return Full content of nodes
     */
    String complexPrint();

    /**
     * @param indent TOC indentation of element
     * @return Content of element in TOC context
     */
    String tocPrint(int indent);
}
