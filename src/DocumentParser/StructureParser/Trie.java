package DocumentParser.StructureParser;

import DocumentParser.IObserver;
import java.util.HashMap;
import java.util.Iterator;

/**
 * class Trie
 *
 * Contains custom trie implementation.
 */
class Trie {
    private TrieNode trieRoot;
    private HashMap<StructureIndicator, TrieNode> entriesInTrie;

    Trie() {
        trieRoot = null;
        entriesInTrie = new HashMap<>();
    }

    /**
     * Method changes node's heading
     * @param label node that has to be change
     * @param heading new heading
     */
    void updateElementHeading(StructureIndicator label, String heading) {
        TrieNode node = entriesInTrie.get(label);
        node.setHeading(heading);
    }

    String getElementHeading(StructureIndicator label){
        return entriesInTrie.get(label).getHeading();
    }
    /**
     * @param node New trie node that is expected to be added to trie
     */
    void insertToTrie(TrieNode node) {
        if (node == null) throw new IllegalArgumentException("Serious error occurred." +
                " Please contact the author. Error code 29000.");
        TrieNode parent = null;
        if (trieRoot == null) {
            if (node.label.parentIndicator == null) {
                trieRoot = node;
            } else {
                throw new IllegalArgumentException("Serious error occurred. Please contact the author. Error code 29001.");
            }
        } else {
            if (node.label.parentIndicator == null) {
                throw new IllegalArgumentException("Serious error occurred." +
                        " Please contact the author. Error code 29002.");
            } else {
                // Existence of parent is ensured by the process of generating document -
                // if no parent found -> stack error
                parent = entriesInTrie.get(node.label.parentIndicator);
                parent.addChild(node);
            }
        }
        entriesInTrie.put(node.label, node);
    }

    /**
     * @param mode Printing mode - decides which print function on node to use.
     * @return String with required content.
     */
    String getAllContent(PrintTypes mode) {
        return trieRoot != null ? preOrderTraversal(trieRoot, mode).toString() : "";
    }

    /**
     * @param label Key of node to print.
     * @param mode Printing mode - decides which print function on node to use.
     * @return String with required content.
     */
    String getSingleSection(StructureIndicator label, PrintTypes mode) {
        // Check against label null pointers - done in StructureHolder class. Existence of such a key in entriesInTrie
        // ensured while inserting to trie (and to sections HashMap in StructureHolder at the same time).
        return preOrderTraversal(entriesInTrie.get(label), mode, label.getLevelInHierarchy()).toString();
    }

    /**
     * @param labelStart Key of first node in range to print.
     * @param labelEnd Key of second node in range to print.
     * @return String with required content.
     */
    String getContentFromRange(StructureIndicator labelStart, StructureIndicator labelEnd) {
        // Check against label null pointers - done in StructureHolder class. Existence of such a key in entriesInTrie
        // ensured while inserting to trie (and to sections HashMap in StructureHolder at the same time).
        return preOrderTraversal(entriesInTrie.get(labelStart.getLCA(labelEnd)), entriesInTrie.get(labelStart),
                entriesInTrie.get(labelEnd)).toString();
    }


    /**
     * @param root Starting node
     * @param from Left boundary (inclusive)
     * @param to Right boundary (exclusive)
     * @return String with required content.
     */
    private StringBuilder preOrderTraversal(TrieNode root, TrieNode from, TrieNode to) {
        StringBuilder result = new StringBuilder();
        if (root != null) {
            if (inRange(root, from, to)) result.append(root.complexPrint());
            Iterator<TrieNode> children = root.getAllChildrenIterator();
            while (children.hasNext()) {
                result.append(preOrderTraversal(children.next(), from, to));
            }
        }
        return result;
    }

    /**
     * @param node node to check
     * @param from left boundary (inclusive)
     * @param to right boundary (exclusive)
     * @return result of comparision
     */
    private boolean inRange(TrieNode node, TrieNode from, TrieNode to) {
        return node.label.relativePosInLinOrder(to.label) < 0 && node.label.relativePosInLinOrder(from.label) >= 0;
    }

    /**
     * @param node node to start with
     * @param mode Printing mode - decides which print function on node to use.
     * @param offset indentation level (sometimes not used information)
     * @return String with required content.
     */
    private StringBuilder preOrderTraversal(TrieNode node, PrintTypes mode, int offset) {
        StringBuilder result = new StringBuilder();
        if (mode == PrintTypes.Complex) result.append(node.complexPrint());
        else result.append(node.tocPrint(node.label.getLevelInHierarchy() - offset));
        Iterator<TrieNode> it = node.getAllChildrenIterator();
        while (it.hasNext()) {
            result.append(preOrderTraversal(it.next(), mode, offset));
        }
        return result;
    }

    /**
     * @param node node to start with
     * @param mode Printing mode - decides which print function on node to use.
     * @return String with required content.
     */
    private StringBuilder preOrderTraversal(TrieNode node, PrintTypes mode) {
        return this.preOrderTraversal(node, mode, 0);
    }
}
