package DocumentParser.StructureParser;

import DocumentParser.IObserver;
import DocumentParser.Utils.Tuple;
//import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.print.Doc;
import java.security.InvalidKeyException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Class that provide object representation of file
 */
class StructureHolder implements IObserver {
    /**
     * Factory of trie nodes
     */
    private final NodeFactory factory = new NodeFactory();
    /**
     * All unique elements (keys -> StructureIndicators) in documents by Name
     */
    private final Map<String, StructureIndicator> sections;
    /**
     * list of all valid articles
     */
    private final List<StructureIndicator> articlesList;
    /**
     * Preserves order at levels in key hierarchy - each entry is the maximum position number at level
     */
    private final List<Integer> levelsRegister;
    /**
     * Trie that has whole document
     */
    private final Trie fileStructure;
    /**
     * Lines parsed by StructureProcessor
     */
    private final List<Tuple<String, Integer, ElementTypes>> parsedLines;
    /**
     * Only Sections that are not ordinary content
     */
    private List<Tuple<String, Integer, ElementTypes>> onlySections;
    /**
     * Pattern that decide if it is single article - not Art. 1-3. which is invalid at trie
     */
    private final Pattern singleArtPredicate = Pattern.compile("^Art\\. \\d+[a-z]*\\.$");
    /**
     * Pattern that decide if it is single article is not "uchylony"
     */
    private final Pattern annulledPredicate = Pattern.compile("^\\(uchylony\\)$");

    /**
     * Pattern that decide if string starts from "art. "
     */
    private Pattern startFromArtPredicate = Pattern.compile("^art\\. .*$");
    /**
     * Collector that helps to remove words segmentation
     */
    private final Collector<Tuple<String, Integer, ElementTypes>, StringBuilder, String> removeHyphens =
            Collector.of(
                    StringBuilder::new,
                    (j, p) -> {
                        if (j.lastIndexOf("-") == j.length() - 1 && j.length() != 0)
                            j.deleteCharAt(j.length() - 1);
                        else if (j.length() > 0) j.append(" ");
                        if(p.thd != ElementTypes.Content) j.append("\n");
                        j.append(p.fst);
                        if(p.thd != ElementTypes.Content) j.append("\n");

                    },
                    (j1, j2) -> {
                        if (j1.lastIndexOf("-") == j1.length() - 1 && j1.length() != 0)
                            j1.deleteCharAt(j1.length() - 1);
                        else if (j1.length() > 0) j1.append(" ");
                        return j1.append(j2);
                    },
                    StringBuilder::toString);

    /**
     * @param lines list of lines parsed by StructureProcessor
     */
    StructureHolder(List<Tuple<String, Integer, ElementTypes>> lines) {
        sections = new HashMap<>();
        articlesList = new LinkedList<>();
        levelsRegister = new ArrayList<>();
        fileStructure = new Trie();
        parsedLines = new ArrayList<>(lines);
        onlySections = new ArrayList<>
                (parsedLines.parallelStream()
                        .filter(t -> t.thd != ElementTypes.Content)
                        .collect(Collectors.toList()));
    }

    @Override
    public void ActualizeRegister(int lvl) {
        if (levelsRegister.size() == lvl) levelsRegister.add(1);
        else {
            // if invalid level -> IndexOutOfBoundsException (unchecked)
            if(lvl > levelsRegister.size())
                throw new IndexOutOfBoundsException("Serious error occurred." +
                        " Please contact the author. Error code 31001.");
            int order = levelsRegister.get(lvl);
            order++;
            levelsRegister.set(lvl, order);
        }
    }

    /**
     * @param lvl level in key hierarchy
     * @return max position at level
     */
    Integer getOrderAtLevel(int lvl) {
        Integer result = 0;
        if (levelsRegister.size() > lvl) result = levelsRegister.get(lvl);
        else if (lvl > levelsRegister.size() + 1)
            throw new IndexOutOfBoundsException("Serious error occurred." +
                    " Please contact the author. Error code 31000.");
        return result;
    }

    /**
     * Description:
     *  Structure generating with stacks using the proper parenthesizing structure of tree
     *  We go through list of touples in sequential order and one by one handle with
     *  document elements from onlySections list - providing the document elements with
     *  content from parsedLines list.
     *  The importance of stacks - we can store on them element that are important to
     *  distinguish level of new element in tree and insert it properly.
     *  (Clue of using stacks in description of getParentIndicator() method)
     *
     * Generate object structure of file
     * @throws DocumentStructureException in case of wrong document structure
     */
    void generateStructure() throws DocumentStructureException {
        Stack<StructureIndicator> structureElements = new Stack<>(); // parents indicators!
        Stack<Tuple<String, Integer, ElementTypes>> sectionSymbols = new Stack<>(); // parents section tuples!
        Tuple<String, Integer, ElementTypes> section;
        Tuple<String, Integer, ElementTypes> nextSection; // guardian of list of tuples
        sectionSymbols.push(onlySections.get(0)); // starting processing root
        TrieNode root = processDocumentRoot();
        fileStructure.insertToTrie(root);
        structureElements.push(root.label);
        StringBuilder heading = new StringBuilder();
        for (int i = 0; i < onlySections.size(); i++) {
            section = onlySections.get(i);
            if (i + 1 < onlySections.size()) nextSection = onlySections.get(i + 1);
            else nextSection = null;
            processElement(structureElements, sectionSymbols, section, nextSection, heading);
        }
        // cleaning stuff
        while (!sectionSymbols.empty()) sectionSymbols.pop();
        while (!structureElements.empty()) structureElements.pop();
    }


    /*
    ************************************************************************************************
    *                    Part responsible for answering queries from outside world
    ************************************************************************************************
     */

    /**
     * @return All content of document
     */
    String getAllContent() {
        return fileStructure.getAllContent(PrintTypes.Complex);
    }

    /**
     * @return TOC of whole document
     */
    String getAllTOC() {
        return fileStructure.getAllContent(PrintTypes.TOC);
    }

    /**
     * @param labelStart Start of range
     * @param labelEnd End of range
     * @return Content of range
     * @throws InvalidKeyException in case of invalid String labels
     */
    String getContentFromRange(String labelStart, String labelEnd) throws InvalidKeyException {
        StructureIndicator fromIndicator = getStructureIndicator(preProcessStringLabel(labelStart));
        StructureIndicator toIndicator = getStructureIndicator(preProcessStringLabel(labelEnd));
        if (fromIndicator.relativePosInLinOrder(toIndicator) > 0) {
            StructureIndicator tmp = fromIndicator;
            fromIndicator = toIndicator;
            toIndicator = tmp;
        }
        return fileStructure.getContentFromRange(fromIndicator, toIndicator);
    }

    /**
     * @param label Label of element
     * @return TOC of single element
     * @throws InvalidKeyException in case of invalid String label
     */
    String getTOCOfSingleElement(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label)), PrintTypes.TOC);
    }

    /**
     * @param label Label of element
     * @return content of single element
     * @throws InvalidKeyException in case of invalid String label
     */
    String getContentOfSingleElement(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label)), PrintTypes.Complex);
    }

    /**
     * @param label number of section
     * @return Content of section in document
     * @throws InvalidKeyException in case of invalid String label
     */
    String getContentOfSingleSection(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label, LabelTypes.Section)),
                        PrintTypes.Complex);
    }

    /**
     * @param label number of chapter
     * @return Content of chapter
     * @throws InvalidKeyException in case of invalid String label
     */
    String getContentOfSingleChapter(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label, LabelTypes.Chapter)),
                        PrintTypes.Complex);
    }

    /**
     * @param label number of article
     * @return Content of article
     * @throws InvalidKeyException in case of invalid String label
     */
    String getContentOfSingleArticle(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label, LabelTypes.Article)),
                        PrintTypes.Complex);
    }

    /**
     * @param label number of chapter
     * @return TOC of chapter in document
     * @throws InvalidKeyException in case of invalid String label
     */
    String getCTOCofSingleChapter(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label, LabelTypes.Chapter)),
                        PrintTypes.TOC);
    }

    /**
     * @param label number of section
     * @return TOC of section in document
     * @throws InvalidKeyException in case of invalid String label
     */
    String getTOCofSingleSection(String label) throws InvalidKeyException {
        return fileStructure
                .getSingleSection(getStructureIndicator(preProcessStringLabel(label, LabelTypes.Section)),
                        PrintTypes.TOC);
    }

    /**
     * @param from Start of range (article)
     * @param to End of range (article)
     * @return Content of articles in range
     * @throws InvalidKeyException in case of invalid String labels
     */
    String getRangeOfArticles(String from, String to) throws InvalidKeyException {
        String extErrMsg = "Entered article number is either out of document range or the article was annulled.";
        StructureIndicator fromIndicator = getStructureIndicator(preProcessStringLabel(from, LabelTypes.Article),
                extErrMsg);
        StructureIndicator toIndicator = getStructureIndicator(preProcessStringLabel(to, LabelTypes.Article),
                extErrMsg);
        // Swapping in case of wrong order
        if (fromIndicator.relativePosInLinOrder(toIndicator) > 0) {
            StructureIndicator tmp = fromIndicator;
            fromIndicator = toIndicator;
            toIndicator = tmp;
        }
        StringBuilder result = new StringBuilder();
        boolean fromOccurred = false;
        // Existence of both StructureIndicator (Exception thrown if not) ensures finite loop
        for (StructureIndicator currIndicator : articlesList) {
            if (!fromOccurred) {
                if (currIndicator.equals(fromIndicator)) {
                    fromOccurred = true;
                    result.append(fileStructure.getSingleSection(currIndicator, PrintTypes.Complex));
                }
            } else {
                result.append(fileStructure.getSingleSection(currIndicator, PrintTypes.Complex));
                if (currIndicator.equals(toIndicator)) break;
            }
        }
        return result.toString();
    }

    /**
     * @param label String label of element
     * @param extMsg Message that extends error communicate
     * @return StructureIndicator according to label
     * @throws InvalidKeyException in case of invalid String label
     */
    private StructureIndicator getStructureIndicator(String label, String extMsg) throws InvalidKeyException {
        StructureIndicator result = sections.get(label);
        if(result == null) throw new InvalidKeyException("There is no such element as " + label + " in the document. "
                + extMsg);
        return result;
    }

    /**
     * @param label String label of element
     * @return StructureIndicator according to label
     * @throws InvalidKeyException in case of invalid String label
     */
    private StructureIndicator getStructureIndicator(String label) throws InvalidKeyException {
        return this.getStructureIndicator(label, "");
    }

    /**
     * @param label string label
     * @param type type of label
     * @return string label in form appropriate to further processing
     */
    private String preProcessStringLabel(String label, LabelTypes type) {
        StringBuilder result = new StringBuilder();
        switch (type) {
            case Article:
                result.append("art. ").append(label.toLowerCase()).append(".");
                break;
            case Chapter:
                result.append("., rozdział ").append(label.toLowerCase());
                break;
            case Section:
                result.append("., dział ").append(label.toLowerCase());
                break;
        }
        result.append(",");
        return result.toString();
    }

    /**
     * @param label string label
     * @return string label in form appropriate to further processing
     */
    private String preProcessStringLabel(String label) {
        // This approach let user even to miss the name of element as long as the number structure is correct
        // i.e. if someone input was art. 1., ust. 1., ust. 1), ust a) -> this will be converted to art. 1., 1., 1), a)
        String result = label.toLowerCase().replaceAll("(ust. )|(pkt. )|(lit. )|(ust )|(pkt )|(lit )", "") + ",";
        // if doesn't start from art -> then need ., to consistency with parsed structure keys
        if (!startFromArtPredicate.matcher(result).matches() && result.charAt(0) != '.') result = "., " + result;
        return result;
    }



    /*
    ************************************************************************************************
    *                           Part responsible for generating structure
    ************************************************************************************************
     */

    /**
     * Description:
     *  While processing document to object structure the program has to decide whether the new encountered
     *  element is a child or a sibling in document tree. If we have the same type of element -> we have sibling.
     *  Otherwise we have to check iw we go deeper in tree or not. If not we have to find proper sibling in tree
     *  by popping fro stacks until find the sibling.
     *
     * @param structureElements stack of StructureIndicators processed before while generating parsed structure
     * @param sectionSymbols stack of Tuples processed before while generating parsed structure
     * @param section Currently processed section
     * @return Parent indicator of section
     */
    private StructureIndicator getParentIndicator(Stack<StructureIndicator> structureElements,
                                                  Stack<Tuple<String, Integer, ElementTypes>> sectionSymbols,
                                                  Tuple<String, Integer, ElementTypes> section) {
        StructureIndicator parentStructInd;
        if (section.thd == sectionSymbols.peek().thd) {
            // Sibling in trie
            structureElements.pop();
            sectionSymbols.pop();
            parentStructInd = structureElements.peek();
        } else {
            if (section.thd.ordinal() > sectionSymbols.peek().thd.ordinal()) {
                // we go deeper in trie
                parentStructInd = structureElements.peek();
            } else {
                // popping everything to get the sibling at stack
                // ROOT Node is guard - if there is one root at the stack (guaranteed earlier in code)
                // we cannot have section with ordinal lover than root - equality is max we can achieve
                // so that try to peeking from empty stack should never occur.
                while (section.thd.ordinal() < sectionSymbols.peek().thd.ordinal()) {
                    sectionSymbols.pop();
                    structureElements.pop();
                }
                // popping also the sibling but with root preservation
                // Root preservation may be needed when elements document structure
                // are in wrong order - and then if the program cannot find
                // the proper parent - end up with pointing to root
                if(sectionSymbols.peek().thd != ElementTypes.Root){
                    sectionSymbols.pop();
                    structureElements.pop();
                }
                parentStructInd = structureElements.peek();
            }
        }
        return parentStructInd;
    }


    /**
     * Single structure element processing
     *  Headings are not indexed document element - we have to attach them to previous Chapter/Section
     *  or to the nex element (its a Title within Chapter/Section)
     *  After that we have to decide if we can put the element on stacks -> Special case Art. a-b. - not allowed
     *  After we decided we have to build a node.
     *  Building SectionIndicator - differ when we do it for article and other type of elements because
     *  articles are indexed. All we do - we adjust the Indicator so that it will be easy to build Indicators
     *  from user-provided String labels in the future
     *  We build title and content (with nex section sentinel we know the boundary)
     *  The last check is against '(uchylony)' pattern in content
     *
     * @param structureElements stack of StructureIndicators processed before while generating parsed structure
     * @param sectionSymbols stack of Tuples processed before while generating parsed structure
     * @param section Currently processed section
     * @param nextSection basically list Sentinel
     * @param heading heading that may have to be assigned to next element
     */
    private void processElement(Stack<StructureIndicator> structureElements,
                                Stack<Tuple<String, Integer, ElementTypes>> sectionSymbols,
                                Tuple<String, Integer, ElementTypes> section,
                                Tuple<String, Integer, ElementTypes> nextSection,
                                StringBuilder heading) throws DocumentStructureException {

        StringBuilder title = new StringBuilder();
        StringBuilder content = new StringBuilder();
        StructureIndicator currStructInd;
        if (section.thd == ElementTypes.Heading) {
            // Headings are never standalone trie node
            if ((sectionSymbols.peek().thd == ElementTypes.Chapter ||
                    sectionSymbols.peek().thd == ElementTypes.Section)
                    && fileStructure.getElementHeading(structureElements.peek()) == null) {
                // After preprocessing DocumentRoot only Heading and Chapter may be placed before its
                // Heading and if another heading in row (element heading is not null) -> then heading is connected
                // with the following element and should be preserved to the next call of processElement in
                // StringBuilder object
                fileStructure.updateElementHeading(structureElements.peek(), section.fst);
            } else {
                heading.append(section.fst); // saving heading for the next element
            }
        } else if (section.thd != ElementTypes.Article || (singleArtPredicate.matcher(section.fst).matches())) {
            // We encountered valid document element
            StructureIndicator parentStructInd = getParentIndicator(structureElements, sectionSymbols, section);
            StringBuilder sectionIndicator = new StringBuilder();
            // Distinction that are connected with a fact that we have articles indexed not only top level elements
            if (section.thd == ElementTypes.Article) sectionIndicator.append(section.fst.toLowerCase()).append(",");
            else sectionIndicator.append(parentStructInd.indicator).append(" ")
                    .append(section.fst.toLowerCase()).append(",");

            currStructInd = new StructureIndicator(sectionIndicator.toString(), parentStructInd, this);
            title.append(section.fst);
            int endIdx = nextSection == null ? parsedLines.size() : nextSection.snd;
            content.append(parsedLines.subList(section.snd + 1, endIdx).stream().collect(removeHyphens));

            if (!annulledPredicate.matcher(content).matches()) {
                // Only if element is not annulled we put them into structure
                // We do not allow the same indexed elements
                if(sections.get(currStructInd.indicator) != null)
                    throw new DocumentStructureException("In a document exists at least two identically indexed elements.");
                sectionSymbols.push(section);
                structureElements.push(currStructInd);
                TrieNode node = factory.createNode(section.thd, currStructInd, title.toString(),
                        content.toString().equals("") ? null : content.toString(),
                        heading.toString().equals("") ? null : heading.toString());
                sections.put(currStructInd.indicator, currStructInd);
                if (section.thd == ElementTypes.Article) articlesList.add(currStructInd);
                fileStructure.insertToTrie(node);
                if(heading.length() != 0) heading.delete(0,heading.length());
            }
        }
    }

    /**
     * Description:
     *  Building proper document root differs from other types of elements
     *  We have other types of fields in RootNode so that we have to try to provide these information
     *  With fromDayPredicate and aboutPredicate patterns we can detect characteristic keywords
     *  that determine particular types of lines
     *  startIndex indicator point to the start of content
     *  It may happen that there is no ordinary content in Root Node - but that's fine
     *
     * IMPORTANT!
     * The only content the root can hold is a standard content with Headings...
     * All other kinds of introductions to a law will be  inserted not to Document Root but to descendants!
     * @return TrieNode of root element
     * @throws DocumentStructureException in case of not starting with root element
     */
    private TrieNode processDocumentRoot() throws DocumentStructureException {
        StructureIndicator root = new StructureIndicator(".,", null, this);
        if (onlySections.get(0).thd != ElementTypes.Root)
            throw new DocumentStructureException("Document doesn't start with its name (required for valid documents).");
        StringBuilder title = new StringBuilder("");
        StringBuilder origin = new StringBuilder("");
        StringBuilder heading = new StringBuilder("");
        Tuple<String, Integer, ElementTypes> firstSection = onlySections.get(0);
        // Sometimes may appear the origin info
        for (int i = 0; i < firstSection.snd; i++) {
            origin.append(parsedLines.get(i).fst);
            origin.append("\n");
        }
        title.append(firstSection.fst);
        // Indicator at onlySections
        int i = 1;
        while (parsedLines.get(firstSection.snd + i).thd == ElementTypes.Heading) {
            heading.append(parsedLines.get(i).fst);
            heading.append("\n");
            i++;
        }
        if (!heading.toString().equals(""))
            heading.deleteCharAt(heading.length() - 1); // deleting last \n - for clarity
        Pattern fromDayPredicate = Pattern.compile("^z dnia \\d{1,2}.? \\p{Ll}{4,} \\d{4} ?r.$");
        Pattern aboutPredicate = Pattern.compile("^o .*$");
        int startIndex = parsedLines.get(firstSection.snd + i - 1).snd + 1;
        StringBuilder date = new StringBuilder();
        StringBuilder about = new StringBuilder();
        StringBuilder content = new StringBuilder();
        if (fromDayPredicate.matcher(parsedLines.get(startIndex).fst).matches()) {
            date.append(parsedLines.get(startIndex).fst);
            startIndex++;
        }
        if (aboutPredicate.matcher(parsedLines.get(startIndex).fst).matches()) {
            about.append(parsedLines.get(startIndex).fst);
            startIndex++;
        }
        while(i < onlySections.size() && onlySections.get(i).thd == ElementTypes.Heading) i++;
        content.append(parsedLines.subList(startIndex,
                i < onlySections.size() ? onlySections.get(i).snd : parsedLines.size())
                .stream()
                .collect(removeHyphens));
        onlySections = new ArrayList<>(onlySections.subList(i, onlySections.size()));
        sections.put(root.indicator, root);
        return factory.createNode(ElementTypes.Root, root, title.toString(),
                content.toString(), heading.toString().equals("") ? null : heading.toString(),
                origin.toString().equals("") ? null : origin.toString(),
                date.toString().equals("") ? null : date.toString(),
                about.toString().equals("") ? null : about.toString());
    }

}
