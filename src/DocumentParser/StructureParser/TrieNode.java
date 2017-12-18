package DocumentParser.StructureParser;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * abstract class TrieNode
 *
 * Represents Trie node and its action
 */
abstract class TrieNode implements IPrintable {
    /**
     * Actual key of node
     */
    public final StructureIndicator label;
    /**
     * Title of element in document structure
     */
    public final String title;
    /**
     * Heading of element in document structure
     */
    private String heading;
    /**
     * Content of element in document structure
     */
    public final String content;
    /**
     * HashMap with named children of the node
     */
    private LinkedHashMap<StructureIndicator, TrieNode> children;

    /**
     * Constructor can be accessed only from package
     * @param label Actual key of node
     * @param title Title of element in document structure
     * @param content Content of element in document structure
     * @param heading Heading of element in document structure
     */
    TrieNode(StructureIndicator label, String title, String content, String heading){
        this.label = label;
        this.content = content;
        this.title = title;
        this.heading = heading;
        children = new LinkedHashMap<>();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrieNode trieNode = (TrieNode) o;
        if (label != null ? !label.equals(trieNode.label) : trieNode.label != null) return false;
        if (content != null ? !content.equals(trieNode.content) : trieNode.content != null) return false;
        return children != null ? children.equals(trieNode.children) : trieNode.children == null;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }

    /**
    *   SEE IPrintable
     */
    public String complexPrint() {
        StringBuilder result = new StringBuilder();
        if (heading != null) {
            result.append(heading);
            result.append("\n");
        }
        result.append(title);
        result.append(" ");
        result.append(content);
        result.append("\n");
        return result.toString();
    }

    /**
     * @param child child that is supposed to be added to node
     */
    protected void addChild(TrieNode child){
        if(!children.containsKey(child.label)){
            children.put(child.label, child);
        }
    }

    /**
     * @return Iterator through all children of the node
     */
    protected Iterator<TrieNode> getAllChildrenIterator(){
        return children.values().iterator();
    }

    /**
     * Generates indentation (used in TOC print)
     * @param indent indentation level
     * @return generated indent string
     */
    protected StringBuilder generateIndentation(int indent){
        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < indent; i++) result.append("\t");
        return result;
    }

    /**
     * heading field getter
     * @return Heading of the node
     */
    final String getHeading(){
        return heading;
    }

    /**
     * heading field setter
     * @param heading new heading content
     */
    final void setHeading(String heading){
        this.heading = heading;
    }
}
