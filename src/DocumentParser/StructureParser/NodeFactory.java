package DocumentParser.StructureParser;

/**
 * Responsible for creating trie nodes
 */
class NodeFactory {
    /**
     * @param type type of element
     * @param label key of node
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     * @param origin origin info about element
     * @param date date info about element
     * @param about additional about info
     * @return TrieNode that can be inserted to trie
     */
    TrieNode createNode(ElementTypes type, StructureIndicator label, String title, String content,
                        String heading, String origin, String date, String about){
        TrieNode result = null;
        switch (type) {
            case Root:
                result = new RootNode(label, title, content, heading, origin, date, about);
                break;
            case Section:
            case Chapter:
                result = new ChapterNode(label, title, content, heading);
                break;
            case Article:
                result = new ArticleNode(label, title, content, heading);
                break;
            case Paragraph:
            case Point:
            case Letter:
            case AggregateSection:
                result = new StandardNode(label, title, content, heading);
        }
        return result;
    }

    /**
     * @param type type of element
     * @param label key of node
     * @param title title of element
     * @param content content of element
     * @return TrieNode that can be inserted to trie
     */
    TrieNode createNode(ElementTypes type, StructureIndicator label, String title, String content){
        return this.createNode(type, label, title, content, null, null, null, null);
    }

    /**
     * @param type type of element
     * @param label key of node
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     * @return TrieNode that can be inserted to trie
     */
    TrieNode createNode(ElementTypes type, StructureIndicator label, String title, String content, String heading){
        return this.createNode(type, label, title, content, heading, null, null, null);
    }

    /**
     * @param type type of element
     * @param label key of node
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     * @param origin origin info about element
     * @return TrieNode that can be inserted to trie
     */
    TrieNode createNode(ElementTypes type, StructureIndicator label, String title, String content,
                        String heading, String origin){
        return this.createNode(type, label, title, content, heading, origin, null, null);
    }

    /**
     * @param type type of element
     * @param label key of node
     * @param title title of element
     * @param content content of element
     * @param heading heading of element
     * @param origin origin info about element
     * @param date date info about element
     * @return TrieNode that can be inserted to trie
     */
    TrieNode createNode(ElementTypes type, StructureIndicator label, String title, String content,
                        String heading, String origin, String date){
        return this.createNode(type, label, title, content, heading, origin, date, null);
    }

}
