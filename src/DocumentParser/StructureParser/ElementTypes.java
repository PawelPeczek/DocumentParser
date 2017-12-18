package DocumentParser.StructureParser;

import java.util.regex.Pattern;

/**
 * All valid section types
 */
enum ElementTypes {
    Root(Pattern.compile("^USTAWA|KONSTYTUCJA$")),
    Section(Pattern.compile("^(DZIAŁ|Dział) [IVXCD]+[A-Za-z]*$")),
    Chapter(Pattern.compile("^(ROZDZIAŁ|Rozdział) ([IVXCD]+|\\d+)[A-Za-z]*$")),
    // Always part of previous/following section - as a part of title or content (depends on context) - not an index
    Heading(Pattern.compile("^[\\p{Lu}][\\p{Lu}\\s\\p{Punct}–0-9]*$")),
    Article(Pattern.compile("^Art\\. \\d+[a-z]*(–\\d+[a-z]*)*\\.$")),
    Paragraph(Pattern.compile("^\\d+[a-z]*\\.$")),
    Point(Pattern.compile("^\\d+[a-z]*\\)$")),
    Letter(Pattern.compile("^[a-z]+\\)$")),
    AggregateSection(Pattern.compile("–")),
    Content(Pattern.compile("^.+$"));
    /**
     * Pattern that are connected with section type
     */
    public final Pattern predicate;

    /**
     * @param p pattern that will be predicate for the section type
     */
    ElementTypes(Pattern p){
        predicate = p;
    }
}


