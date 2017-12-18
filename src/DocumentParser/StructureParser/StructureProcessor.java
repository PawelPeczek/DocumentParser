package DocumentParser.StructureParser;

import DocumentParser.Utils.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class responsible for processing file to a form that can be further transform into object.
 */
class StructureProcessor {
    /**
     * Groups of pattern that allows to split single line with few elements of document structure
     * in it into separate lines.
     */
    private Pattern[] groupingPatterns = new Pattern[]{
            Pattern.compile("^(Art. \\d+[a-z]*(–\\d+[a-z]*)*.)( \\d+[a-z]*. )?(.*)"),
            Pattern.compile("^(\\d+[a-z]*\\.|\\d+[a-z]*\\)|[a-z]+\\)|–) (.*)")
    };

    /**
     * List of Tuples with parsed content and additional info
     */
    final List<Tuple<String, Integer, ElementTypes>> parsedLines;

    /**
     * @param filePath Path to processed file
     * @throws IOException In case of problems with file
     * @throws DocumentStructureException In case of wrong structure of document
     */
    StructureProcessor(String filePath) throws IOException, DocumentStructureException {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))){
            // Predicate of noise in document
            Pattern predicate = Pattern.compile("^©Kancelaria Sejmu.*|^\\d{4}-\\d{2}-\\d{2}$");
            List<String> lines = stream
                    .filter(e -> !predicate.matcher(e).matches() && e.length() > 1) // filtering noise
                    .flatMap(l -> groupMatches(l).stream()) // spiting elements groups
                    .collect(Collectors.toList());
            parsedLines = IntStream.range(0, lines.size())
                    .parallel()
                    // providing info about line number and type of element in line
                    .mapToObj(i -> new Tuple<>(lines.get(i), i, mapSectionType(lines.get(i))))
                    .collect(Collectors.toList());
            // If #root sections != 1 -> invalid document structure
            List<Tuple<String, Integer, ElementTypes>> rootSections =
                    parsedLines.parallelStream()
                    .filter(e -> e.thd == ElementTypes.Root) // only Roots stays
                    .collect(Collectors.toList());
            if(rootSections.size() != 1)
                throw new DocumentStructureException("Document has wrong number of root sections.");
        }
    }

    /**
     * @param line line of content
     * @return SectionType according to pattern in public field predicate of  ElementTypes
     */
    private ElementTypes mapSectionType(String line){
        ElementTypes result = ElementTypes.Content;
        for(ElementTypes t: ElementTypes.values()){
            if(t.predicate.matcher(line).matches()){
                result = t;
                break;
            }
        }
        return result;
    }

    /**
     * @param line Line of content
     * @return List of elements that were placed in one line before
     */
    private List<String> groupMatches(String line){
        List<String> res = new LinkedList<>();
        for(Pattern predicate : groupingPatterns){
            Matcher matcher = predicate.matcher(line);
            if (matcher.find()){
                // Each group = single element type -> we convert them to separate String
                for (int i = 1; i <= matcher.groupCount(); i++){
                    String f = matcher.group(i);
                    if(f != null && f.length() > 0 && !(f.charAt(0) == '–' && f.length() > 1)) res.add(f.trim());
                }
                break;
            }
        }
        // If no groups detected - stay as is
        if(res.isEmpty()) res.add(line);
        return res;
    }

}
