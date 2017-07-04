package getalp.wsd.conversion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;
import getalp.wsd.corpus.xml.writer.XMLCorpusSequentialWriterSentence;
import getalp.wsd.utils.PercentProgressDisplayer;
import getalp.wsd.utils.tuples.Triplet;
import getalp.wsd.wordnet.WordnetHelper;

public class OntonotesConverter
{
    private int wnVersion;
    
    public void convert(String inputPath, String outputPath, int wnVersion)
    {
        this.wnVersion = wnVersion;
        try
        {
            loadWithExceptions(inputPath, outputPath);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private void loadWithExceptions(String inputPath, String ouputPath) throws Exception
    {
        String pathAnnotations = inputPath + "/annotations";
        String pathSenseMapping = inputPath + "/metadata/sense-inventories/";
        Map<String, List<String>> senseMapWN = loadAllSenseMapping(pathSenseMapping);
        List<String> wordsList = loadWordList(pathAnnotations);
        loadAllOnf(ouputPath, senseMapWN, wordsList);
    }

    private void loadAllOnf(String output, Map<String, List<String>> senseMapWN, List<String> wordsList) throws Exception
    {
        XMLCorpusSequentialWriterSentence out = new XMLCorpusSequentialWriterSentence(output);
        out.writeHeader();
        PercentProgressDisplayer progress = new PercentProgressDisplayer(wordsList.size());
        for (int i = 0; i < wordsList.size(); i++)
        {
            if (progress.refresh(i)) System.out.print("Info : Ontonotes loading... " + progress.percent + "%\r");
            Map<Integer, Map<Integer, Triplet<String, String, String>>> senseMapON = loadSense(wordsList.get(i) + ".sense");
            loadOnf(wordsList.get(i) + ".onf", out, senseMapON, senseMapWN);
        }
        System.out.println();
        out.writeFooter();
    }
    
    private List<String> loadWordList(String pathAnnotations) throws Exception
    {
        Set<String> words = new HashSet<>();
        Stream<Path> paths = Files.find(Paths.get(pathAnnotations), Integer.MAX_VALUE, new BiPredicate<Path,BasicFileAttributes>()
        {
            @Override
            public boolean test(Path arg0, BasicFileAttributes arg1)
            {
                if (arg0.toString().endsWith(".sense"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
        paths.forEach(filePath ->
        {
            words.add(filePath.toString().substring(0, filePath.toString().lastIndexOf(".")));
        });
        paths.close();
        List<String> wordsList = new ArrayList<>(words);
        Collections.sort(wordsList);
        return wordsList;
    }

    private Map<Integer, Map<Integer, Triplet<String, String, String>>> loadSense(String path) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        Map<Integer, Map<Integer, Triplet<String, String, String>>> senseMap = new HashMap<>();
        while ((line = br.readLine()) != null)
        {
            String[] lineSplitted = line.trim().split("\\s+");
            int sentenceIndex = Integer.valueOf(lineSplitted[1]);
            int wordIndex = Integer.valueOf(lineSplitted[2]);
            String lemma = lineSplitted[3].substring(0, lineSplitted[3].lastIndexOf("-"));
            String pos = lineSplitted[3].substring(lineSplitted[3].lastIndexOf("-") + 1);
            String sense = lineSplitted[lineSplitted.length - 1];
            if (!senseMap.containsKey(sentenceIndex))
            {
                senseMap.put(sentenceIndex, new HashMap<>());
            }
            senseMap.get(sentenceIndex).put(wordIndex, new Triplet<>(lemma, pos, sense));
        }
        br.close();
        return senseMap;
    }
    
    private void loadOnf(String path, XMLCorpusSequentialWriterSentence out, Map<Integer, Map<Integer, Triplet<String, String, String>>> senseMap, Map<String, List<String>> senseMapWN) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        boolean inSentence = false;
        String beginStr = "Treebanked sentence:";
        String stopStr = "Speaker information:";
        String currentSentenceStr = "";
        int currentSentenceIndex = 0;
        while ((line = br.readLine()) != null)
        {
            if (inSentence)
            {
                if (line.trim().equals(stopStr))
                {
                    if (senseMap.containsKey(currentSentenceIndex))
                    {
                        Sentence currentSentence = new Sentence();
                        String[] wordsStr = currentSentenceStr.trim().split("\\s+");
                        int currentWordIndex = 0;
                        for (String wordStr : wordsStr)
                        {
                            if (wordStr.contains("*")) continue;
                            if (wordStr.equals("/.")) wordStr = ".";
                            Word currentWord = new Word(wordStr);
                            if (senseMap.containsKey(currentSentenceIndex) && senseMap.get(currentSentenceIndex).containsKey(currentWordIndex))
                            {
                                String lemma = senseMap.get(currentSentenceIndex).get(currentWordIndex).first;
                                String pos = senseMap.get(currentSentenceIndex).get(currentWordIndex).second;
                                String senseKeyON = senseMap.get(currentSentenceIndex).get(currentWordIndex).third;
                                senseKeyON = lemma + "%" + pos + "#" + senseKeyON;
                                currentWord.setAnnotation("lemma", lemma);
                                currentWord.setAnnotation("pos", pos);
                                if (!senseMapWN.get(senseKeyON).isEmpty())
                                {
                                    String concatSenseKeyWN = "";
                                    for (String senseKeyWN : senseMapWN.get(senseKeyON))
                                    {
                                        concatSenseKeyWN += ";" + senseKeyWN;
                                    }
                                    concatSenseKeyWN = concatSenseKeyWN.substring(1);
                                    currentWord.setAnnotation("wn" + wnVersion + "_key", concatSenseKeyWN);
                                }
                            }
                            currentSentence.addWord(currentWord);
                            currentWordIndex++;
                        }
                        out.writeSentence(currentSentence);
                    }
                    currentSentenceIndex += 1;
                    inSentence = false;
                }
                else
                {
                    currentSentenceStr += " " + line;
                }
            }
            else
            {
                if (line.trim().equals(beginStr))
                {
                    line = br.readLine();
                    inSentence = true;
                    currentSentenceStr = "";
                }
            }
        }
        br.close();
    }
    
    public Map<String, List<String>> loadAllSenseMapping(String senseMappingFolderPath) throws Exception
    {
        Map<String, List<String>> map = new HashMap<>();
        List<String> fileList = new ArrayList<>();
        Files.list(Paths.get(senseMappingFolderPath)).forEach(filePath ->
        {
            if (filePath.toString().endsWith(".xml") && 
                !filePath.toString().endsWith("noun_grouping_template.xml") && 
                !filePath.toString().endsWith("fracture-v.xml"))
            {
                fileList.add(filePath.toString());
            }
        });
        Collections.sort(fileList);
        for (String file : fileList)
        {
            Map<String, List<String>> map2 = loadSenseMapping(file);
            map.putAll(map2);
        }
        WordnetHelper wn = WordnetHelper.wn(wnVersion);
        for (List<String> mapEntry : map.values())
        {
            List<String> realMapEntry = new ArrayList<>();
            for (String senseNumber : mapEntry)
            {
                realMapEntry.add(wn.getSenseKeyFromSenseNumber(senseNumber));
            }
            mapEntry.clear();
            mapEntry.addAll(realMapEntry);
        }
        return map;
    }


    public static class SenseMappingXMLHandler extends DefaultHandler
    {
        public String wordKey;
        
        public String lemma;
        
        public String pos;
        
        public boolean saveCharacters;
        
        public String currentCharacters;
        
        public String currentONSenseKey;
        
        public Map<String, List<String>> ONSenseKeyToWNSenseKey = new HashMap<>();
        
        public boolean insideWNSense;
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
        {
            if (localName.equals("sense"))
            {
                currentONSenseKey = lemma + "%" + pos + "#" + atts.getValue("n");
                ONSenseKeyToWNSenseKey.put(currentONSenseKey, new ArrayList<>());
            }
            else if (localName.equals("wn"))
            {
                if (atts.getValue("lemma") == null && atts.getValue("version").equals("3.0"))
                {
                    insideWNSense = true;
                    saveCharacters = true;
                    currentCharacters = "";
                }
                else
                {
                    insideWNSense = false;
                }
            }
        }
        
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if (localName.equals("wn") && insideWNSense)
            {
                String[] WNSenses = currentCharacters.trim().split(",");
                for (String wnsense : WNSenses)
                {
                    if (wnsense.isEmpty()) continue;
                    ONSenseKeyToWNSenseKey.get(currentONSenseKey).add(lemma + "%" + pos + "#" + Integer.valueOf(wnsense));
                }
                saveCharacters = false;
                insideWNSense = false;
            }
        }
        
        @Override
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (saveCharacters)
            {
                currentCharacters += new String(ch, start, length);
            }
        }
    }
        
    public Map<String, List<String>> loadSenseMapping(String filePath) throws Exception
    {
        XMLReader saxReader = XMLReaderFactory.createXMLReader();
        SenseMappingXMLHandler handler = new SenseMappingXMLHandler();
        handler.wordKey = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".xml"));
        handler.lemma = handler.wordKey.substring(0, handler.wordKey.lastIndexOf("-"));
        handler.pos = handler.wordKey.substring(handler.wordKey.lastIndexOf("-") + 1);
        saxReader.setContentHandler(handler);
        saxReader.parse(filePath);
        return handler.ONSenseKeyToWNSenseKey;
    }

}
