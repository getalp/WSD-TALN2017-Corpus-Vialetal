
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.nio.file.Files;
import getalp.wsd.conversion.*;
import getalp.wsd.corpus.Annotation;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;
import getalp.wsd.corpus.xml.modifier.XMLCorpusModifierSentence;
import getalp.wsd.corpus.xml.modifier.XMLCorpusModifierWord;
import getalp.wsd.corpus.xml.reader.XMLCorpusSequentialReaderSentence;
import getalp.wsd.corpus.xml.reader.XMLCorpusSequentialReaderWord;
import getalp.wsd.corpus.xml.writer.XMLCorpusSequentialWriterSentence;
import getalp.wsd.utils.Data;
import getalp.wsd.utils.POSHelper;
import getalp.wsd.utils.Wrapper;
import getalp.wsd.wordnet.WordnetHelper;
import getalp.wsd.wordnet.WordnetMapping;

public class CreateCorpus
{
    public static void main(String[] args) throws Exception
    {
    	WordnetHelper.useWNGT = false;
        createSemcor();
        //createDSO();
        createWNGT();
        createMASC();
        createOMSTI();
        //createOntonotes();
    	createSenseval2();
    	createSenseval3Task1();
        createSemeval2007Task7();
        createSemeval2007Task17();
        createSemeval2013Task12();
    	createSemeval2015Task13();
    }

    public static void createSemcor()
    {
        System.out.println("Info : creating corpus " + Data.semcorPath);
        new SemcorConverter().convert("data/corpus/original/semcor", Data.semcorPath, 16);
        convertWordnetTags(Data.semcorPath, 16, 30);
        checkWordnetAnnotations(Data.semcorPath, 16);
        checkWordnetAnnotations(Data.semcorPath, 30);
        addLemmasAndPOS(Data.semcorPath, 30);
    }
    
    public static void createDSO()
    {
        System.out.println("Info : creating corpus " + Data.dsoPath);
        new DSOConverter().convert("data/corpus/original/dso/", Data.dsoPath, 16);
        mergeDuplicates(Data.dsoPath);
        convertWordnetTags(Data.dsoPath, 16, 30);
        checkWordnetAnnotations(Data.dsoPath, 16);
        checkWordnetAnnotations(Data.dsoPath, 30);
        addLemmasAndPOS(Data.dsoPath, 30);
    }

    public static void createWNGT()
    {
        System.out.println("Info : creating corpus " + Data.wngtPath);
        new WNGTConverter().convert("data/wordnet/30/glosstag", Data.wngtPath, 30);
        checkWordnetAnnotations(Data.wngtPath, 30);
        addLemmasAndPOS(Data.wngtPath, 30);
    }

    public static void createMASC()
    {
        System.out.println("Info : creating corpus " + Data.mascPath);
        new MASCConverter().convert("data/corpus/original/google/masc", Data.mascPath, 30);
        checkWordnetAnnotations(Data.mascPath, 30);
        addLemmasAndPOS(Data.mascPath, 30);
        removeSenseTagsWherePOSDiffers(Data.mascPath, 30);
        setLemmaAnnotationsFromFirstSenseAnnotations(Data.mascPath, 30);
        removeSenseTagsWhereLemmaDiffers(Data.mascPath, 30);
    }

    public static void createOMSTI()
    {
        System.out.println("Info : creating corpus " + Data.omstiPath);
        new OMSTIConverter().convert("data/corpus/original/omsti/30", Data.omstiPath, 30);
        mergeDuplicates(Data.omstiPath);
        cleanCorpus(Data.omstiPath);
        checkWordnetAnnotations(Data.omstiPath, 30);
        addLemmasAndPOS(Data.omstiPath, 30);
        cutInPieces(Data.omstiPath, "data/corpus/omsti_part", 220000);
        removeFile(Data.omstiPath);
    }
    
    public static void createOntonotes()
    {
        System.out.println("Info : creating corpus " + Data.ontonotesPath);
        new OntonotesConverter().convert("data/corpus/original/ontonotes/5.0/data/files/data/english", Data.ontonotesPath, 30);
        checkWordnetAnnotations(Data.ontonotesPath, 30);
        addLemmasAndPOS(Data.ontonotesPath, 30);
    }
    
    public static void createSenseval2()
    {
        System.out.println("Info : creating corpus " + Data.senseval2Path);
        new Senseval2Converter().convert("data/corpus/original/mihalcea/senseval2", Data.senseval2Path, 171);
        convertWordnetTags(Data.senseval2Path, 171, 30);
        checkWordnetAnnotations(Data.senseval2Path, 171);
        checkWordnetAnnotations(Data.senseval2Path, 30);
        addLemmasAndPOS(Data.senseval2Path, 30);
    }

    public static void createSenseval3Task1()
    {
        System.out.println("Info : creating corpus " + Data.senseval3task1Path);
        new Senseval3Task1Converter().convert("data/corpus/original/mihalcea/senseval3", Data.senseval3task1Path, 171);
        convertWordnetTags(Data.senseval3task1Path, 171, 30);
        checkWordnetAnnotations(Data.senseval3task1Path, 171);
        checkWordnetAnnotations(Data.senseval3task1Path, 30);
        addLemmasAndPOS(Data.senseval3task1Path, 30);
    }

    public static void createSemeval2007Task7()
    {
        System.out.println("Info : creating corpus " + Data.semeval2007task7Path);
        new Semeval2007Task7Converter().convert("data/semeval/2007/task7", Data.semeval2007task7Path);
        convertWordnetTags(Data.semeval2007task7Path, 21, 30);
        checkWordnetAnnotations(Data.semeval2007task7Path, 21);
        checkWordnetAnnotations(Data.semeval2007task7Path, 30);
        addLemmasAndPOS(Data.semeval2007task7Path, 30);
    }

    public static void createSemeval2007Task17()
    {
        System.out.println("Info : creating corpus " + Data.semeval2007task17Path);
        new Semeval2007Task17Converter().convert("data/semeval/2007/task17", Data.semeval2007task17Path);
        convertWordnetTags(Data.semeval2007task17Path, 21, 30);
        checkWordnetAnnotations(Data.semeval2007task17Path, 21);
        checkWordnetAnnotations(Data.semeval2007task17Path, 30);
        addLemmasAndPOS(Data.semeval2007task17Path, 30);
    }

    public static void createSemeval2013Task12()
    {
        System.out.println("Info : creating corpus " + Data.semeval2013task12Path);
        new Semeval2013Task12Converter().convert("data/semeval/2013/task12", Data.semeval2013task12Path);
        checkWordnetAnnotations(Data.semeval2013task12Path, 30);
        addLemmasAndPOS(Data.semeval2013task12Path, 30);
    }

    public static void createSemeval2015Task13()
    {
        System.out.println("Info : creating corpus " + Data.semeval2015task13Path);
        new Semeval2015Task13Converter().convert("data/semeval/2015/task13", Data.semeval2015task13Path);
        checkWordnetAnnotations(Data.semeval2015task13Path, 30);
        addLemmasAndPOS(Data.semeval2015task13Path, 30);
    }

    public static void cleanCorpus(String inPath)
    {
        String inPathTmp = inPath + ".tmp.xml";
        final Pattern nonVisiblePattern = Pattern.compile("[^\\p{Graph}]");
        XMLCorpusModifierSentence inout = new XMLCorpusModifierSentence()
        {
            @Override
            public void modifySentence(Sentence sentence)
            {
                List<Word> wordsToDelete = new ArrayList<>();
                for (Word w : sentence.getWords())
                {
                    String wordValue = w.getValue();
                    wordValue = wordValue.trim();
                    if (wordValue.equals("-LCB-"))
                    {
                        wordValue = "{";
                    }
                    else if (wordValue.equals("-LRB-"))
                    {
                        wordValue = "(";
                    }
                    else if (wordValue.equals("-LSB-"))
                    {
                        wordValue = "[";
                    }
                    else if (wordValue.equals("-RCB-"))
                    {
                        wordValue = "}";
                    }
                    else if (wordValue.equals("-RRB-"))
                    {
                        wordValue = ")";
                    }
                    else if (wordValue.equals("-RSB-"))
                    {
                        wordValue = "]";
                    }
                    Matcher matcher = nonVisiblePattern.matcher(wordValue);
                    wordValue = matcher.replaceAll("");
                    w.setValue(wordValue);
                    if (wordValue.equals(""))
                    {
                        wordsToDelete.add(w);
                    }
                }
                for (Word word : wordsToDelete)
                {
                    sentence.removeWord(word);
                }
            }
        };
        
        inout.load(inPath, inPathTmp);
        moveFile(inPathTmp, inPath);
    }
    
    public static void checkDuplicates(String inputPath)
    {
        XMLCorpusSequentialReaderSentence in = new XMLCorpusSequentialReaderSentence();
        Wrapper<Integer> i = new Wrapper<>(0);
        Set<String> realSentences = new HashSet<>();
        in.onSentenceRead((Sentence s) -> 
        {
            if (realSentences.contains(s.toString()))
            {
                i.obj = i.obj + 1;
            }
            else
            {
                realSentences.add(s.toString());
            }
        });
        in.load(inputPath);
        System.out.println("Info : for corpus " + inputPath + " : " + i.obj + " duplicate sentences");
    }
    
    public static void mergeDuplicates(String inputPath)
    {
        String inputPathTmp = inputPath + ".tmp.xml";
    	Wrapper<Integer> total = new Wrapper<>(0);
        Wrapper<Integer> failed = new Wrapper<>(0);
        Wrapper<Integer> annotationsDifferentReplaced = new Wrapper<>(0);
        XMLCorpusSequentialReaderSentence in = new XMLCorpusSequentialReaderSentence();
        XMLCorpusSequentialWriterSentence out = new XMLCorpusSequentialWriterSentence(inputPathTmp);
        Map<String, Sentence> realSentences = new LinkedHashMap<>();
        in.onSentenceRead((Sentence s) -> 
        {
        	String sentenceAsString = s.toString();
            if (realSentences.containsKey(sentenceAsString))
            {
            	total.obj++;
                Sentence realSentence = realSentences.get(sentenceAsString);
                if (s.getWords().size() != realSentence.getWords().size())
                {
                    System.out.println("Error for sentence 1 : " + sentenceAsString + " (" + s.getWords().size() + " words)");
                    System.out.println("Error for sentence 2 : " + realSentence.toString() + " (" + realSentence.getWords().size() + " words)");
                    failed.obj++;
                    return;
                }
                for (int i = 0 ; i < s.getWords().size() ; i++)
                {
                    Word w = s.getWords().get(i);
                    Word realWord = realSentence.getWords().get(i);
                    List<Annotation> wAnnotationsCopy = new ArrayList<>(w.getAnnotations());
                    for (Annotation a : wAnnotationsCopy)
                    {
                    	if (realWord.hasAnnotation(a.getAnnotationName()) && !realWord.getAnnotationValue(a.getAnnotationName()).equals(a.getAnnotationValue()))
                    	{
                    		if (a.getAnnotationName().startsWith("wn") && a.getAnnotationName().endsWith("_key"))
                    		{
                    		    String newAnnotationValue = a.getAnnotationValue() + ";" + realWord.getAnnotationValue(a.getAnnotationName());
                    		    newAnnotationValue = removeDuplicateInAnnotationValue(newAnnotationValue);
                    			w.setAnnotation(a.getAnnotationName(), newAnnotationValue);
                    		}
                    		else
                    		{
                    		    annotationsDifferentReplaced.obj++;
                    		}
                    	}
                    }
                    w.transfertAnnotationsToCopy(realWord);
                }
            }
            else
            {
                realSentences.put(sentenceAsString, s);
            }
        });
        
        in.load(inputPath);
        out.writeHeader();
        for (Map.Entry<String, Sentence> realSentencesEntry : realSentences.entrySet())
        {
            out.writeSentence(realSentencesEntry.getValue());
        }
        out.writeFooter();
        moveFile(inputPathTmp, inputPath);
        System.out.println("Info : for corpus " + inputPath + " : " + total.obj + " duplicate sentences");
        System.out.println("Info : failed to delete " + failed.obj);
        System.out.println("Info : replaced " + annotationsDifferentReplaced.obj + " different annotations");
    }
    
    private static String removeDuplicateInAnnotationValue(String annotationValue)
    {
        return StringUtils.join(new HashSet<>(Arrays.asList(annotationValue.split(";"))).toArray(), ";");
    }

    private static MaxentTagger tagger = new MaxentTagger("data/stanford/model/english.tagger");
    
    public static void addLemmasAndPOS(String corpusPath, int wnVersion)
    {
        WordnetHelper wn = WordnetHelper.wn(wnVersion);
        XMLCorpusModifierSentence inout = new XMLCorpusModifierSentence()
        {
            public void modifySentence(Sentence sentence)
            {
                List<TaggedWord> stanfordSentence = tagger.tagSentence(toStanfordSentence(sentence));
                assert(stanfordSentence.size() != sentence.getWords().size());
                for (int i = 0 ; i < stanfordSentence.size() ; i++)
                {
                    Word word = sentence.getWords().get(i);
                    if (word.hasAnnotation("lemma") && word.hasAnnotation("pos"))
                    {
                        continue;
                    }
                    String pos = word.getAnnotationValue("pos");
                    if (pos.isEmpty())
                    {
                        pos = stanfordSentence.get(i).tag();
                    }
                    if (!pos.isEmpty())
                    {
                        String lemma = word.getAnnotationValue("lemma");
                        if (lemma.isEmpty() && !POSHelper.processPOS(pos).equals("x"))
                        {
                            lemma = wn.morphy(word.getValue(), POSHelper.processPOS(pos));
                        }
                        if (word.hasAnnotation("pos") && !lemma.isEmpty())
                        {
                            word.removeAnnotation("pos");
                        }
                        word.setAnnotation("lemma", lemma);
                        word.setAnnotation("pos", pos);
                    }
                }
            }
        };
        String corpusPathTmp = corpusPath + ".tmp.xml";
        inout.load(corpusPath, corpusPathTmp);
        moveFile(corpusPathTmp, corpusPath);
    }

    private static List<HasWord> toStanfordSentence(Sentence sentence)
    {
        List<HasWord> stanfordSentence = new ArrayList<>();
        for (Word word : sentence.getWords())
        {
            stanfordSentence.add(new edu.stanford.nlp.ling.Word(word.getValue()));
        }
        return stanfordSentence;
    }
    
    public static void convertWordnetTags(String inputPath, int wnVersionIn, int wnVersionOut)
    {
        String inputPathTmp = inputPath + ".tmp.xml";
        
        Wrapper<Integer> countTotal = new Wrapper<>(0);
        Wrapper<Integer> countFailed = new Wrapper<>(0);
        
        String wnInTag = "wn" + wnVersionIn + "_key";
        String wnOutTag = "wn" + wnVersionOut + "_key";

        XMLCorpusModifierWord inout = new XMLCorpusModifierWord()
        {
            public void modifyWord(Word word)
            {
                if (word.hasAnnotation(wnInTag) && !word.hasAnnotation(wnOutTag))
                {
                    String wnInKey = word.getAnnotationValue(wnInTag);
                    word.setAnnotation(wnOutTag, getNewSenseKey(wnInKey, WordnetMapping.wnXtoY(wnVersionIn, wnVersionOut)));
                    countTotal.obj++;
                    if (!word.hasAnnotation(wnOutTag))
                    {
                        //System.out.println("Failed to convert WN" + wnVersionIn + " tag " + wnInKey + " to WN" + wnVersionOut);
                        countFailed.obj++;
                    }
                }
            }
        };
        
        inout.load(inputPath, inputPathTmp);
        moveFile(inputPathTmp, inputPath);

        System.out.print(countTotal.obj + " total WN" + wnVersionIn + " annotations ");
        System.out.print(" - " + (countTotal.obj - countFailed.obj) + " succeded to convert to WN" + wnVersionOut);
        System.out.println(" - " + countFailed.obj + " failed to convert to WN" + wnVersionOut);
    }

    public static void checkWordnetAnnotations(String corpusPath, int wnVersion)
    {
        XMLCorpusSequentialReaderWord corpus = new XMLCorpusSequentialReaderWord();

        WordnetHelper wn = WordnetHelper.wn(wnVersion);
        String wnTag = "wn" + wnVersion + "_key";

        Wrapper<Integer> count = new Wrapper<>(0);
        Wrapper<Integer> fail = new Wrapper<>(0);
        
        corpus.onWordRead((Word word) -> 
        {
            String wnKey = word.getAnnotationValue(wnTag);
            if (!wnKey.isEmpty())
            {
                count.obj++;
                String[] senseKeys = wnKey.split(";");
                for (String senseKey : senseKeys)
                {
                    if (!wn.isSenseKeyExists(senseKey))
                    {
                        fail.obj++;
                        //System.out.println("Error : " + senseKey + " is not a valid WN" + wnVersion + " sense key");
                    }
                }
            }
        });
        
        corpus.load(corpusPath);

        System.out.println(count.obj + " total WN" + wnVersion + " annotations - " + fail.obj + " incorrect");
    }

    private static String getNewSenseKey(String rawSenseKey, WordnetMapping mapping)
    {
        String[] senseKeys = rawSenseKey.split(";");
        List<String> newSenseKeys = new ArrayList<>();
        for (String senseKey : senseKeys)
        {
            String mappedSenseKey = mapping.fromXtoY(senseKey);
            if (!(mappedSenseKey == null || mappedSenseKey.isEmpty() || newSenseKeys.contains(mappedSenseKey)))
            {
                newSenseKeys.add(mappedSenseKey);
            }
        }
        if (newSenseKeys.isEmpty()) return "";
        else return StringUtils.join(newSenseKeys, ";");
    }
    
    public static void removeSenseTagsWherePOSDiffers(String inputPath, int wordnetVersion)
    {
        String inputPathTmp = inputPath + ".tmp.xml";
        String senseTag = "wn" + wordnetVersion + "_key";
        XMLCorpusModifierWord inout = new XMLCorpusModifierWord()
        {
            public void modifyWord(Word word)
            {
                if (word.hasAnnotation(senseTag))
                {
                    String pos = POSHelper.processPOS(word.getAnnotationValue("pos"));
                    String newSenseKey = "";
                    String[] senses = word.getAnnotationValue(senseTag).split(";");
                    for (String sense : senses)
                    {
                        String sensePos = POSHelper.processPOS(Integer.valueOf(sense.substring(sense.indexOf("%") + 1, sense.indexOf("%") + 2)));
                        if (sensePos.equals(pos))
                        {
                            newSenseKey += sense + ";";
                        }
                    }
                    if (!newSenseKey.isEmpty())
                    {
                        newSenseKey = newSenseKey.substring(0, newSenseKey.length() - 1);
                        word.setAnnotation(senseTag, newSenseKey);
                    }
                    else
                    {
                        word.removeAnnotation(senseTag);
                    }
                }
            }
        };
        inout.load(inputPath, inputPathTmp);
        moveFile(inputPathTmp, inputPath);
    }

    public static void setLemmaAnnotationsFromFirstSenseAnnotations(String inputPath, int wordnetVersion)
    {
        String inputPathTmp = inputPath + ".tmp.xml";
        String senseTag = "wn" + wordnetVersion + "_key";
        XMLCorpusModifierWord inout = new XMLCorpusModifierWord()
        {
            public void modifyWord(Word word)
            {
                if (word.hasAnnotation(senseTag))
                {
                    String senseKey = word.getAnnotationValue(senseTag);
                    String lemmaFromSense = senseKey.substring(0, senseKey.indexOf("%"));
                    if (!word.getAnnotationValue("lemma").equals(lemmaFromSense))
                    {
                        word.setAnnotation("lemma", lemmaFromSense);
                    }
                }
            }
        };
        inout.load(inputPath, inputPathTmp);
        moveFile(inputPathTmp, inputPath);
    }

    public static void removeSenseTagsWhereLemmaDiffers(String inputPath, int wordnetVersion)
    {
        String inputPathTmp = inputPath + ".tmp.xml";
        String senseTag = "wn" + wordnetVersion + "_key";
        XMLCorpusModifierWord inout = new XMLCorpusModifierWord()
        {
            public void modifyWord(Word word)
            {
                if (word.hasAnnotation(senseTag))
                {
                    String lemma = word.getAnnotationValue("lemma");
                    String newSenseKey = "";
                    String[] senses = word.getAnnotationValue(senseTag).split(";");
                    for (String sense : senses)
                    {
                        String senseLemma = sense.substring(0, sense.indexOf("%"));
                        if (senseLemma.equals(lemma))
                        {
                            newSenseKey += sense + ";";
                        }
                    }
                    if (!newSenseKey.isEmpty())
                    {
                        newSenseKey = newSenseKey.substring(0, newSenseKey.length() - 1);
                        word.setAnnotation(senseTag, newSenseKey);
                    }
                    else
                    {
                        word.removeAnnotation(senseTag);
                    }
                }
            }
        };
        inout.load(inputPath, inputPathTmp);
        moveFile(inputPathTmp, inputPath);
    }

    public static void cutInPieces(String inputPath, String outputPath, int maxSentenceCount)
    {
        XMLCorpusSequentialReaderSentence in = new XMLCorpusSequentialReaderSentence();
        Wrapper<Integer> currentSentenceCount = new Wrapper<>(maxSentenceCount);
        Wrapper<Integer> currentPart = new Wrapper<>(0);
        Wrapper<XMLCorpusSequentialWriterSentence> out = new Wrapper<>(null);
        in.onSentenceRead((Sentence s) ->
        {
            if (currentSentenceCount.obj >= maxSentenceCount)
            {
                if (out.obj != null) out.obj.writeFooter();
                currentSentenceCount.obj = 0;
                out.obj = new XMLCorpusSequentialWriterSentence(outputPath + currentPart.obj + ".xml");
                out.obj.writeHeader();
                currentPart.obj += 1;
            }
            out.obj.writeSentence(s);
            currentSentenceCount.obj += 1;
        });
        in.load(inputPath);
        out.obj.writeFooter();
    }
    
    public static void moveFile(String from, String to)
    {
    	try
    	{
    		Files.move(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
    	}
    	catch (Exception e)
    	{
    		throw new RuntimeException(e);
    	}
    }
    
    public static void removeFile(String filepath)
    {
        try
        {
            Files.delete(Paths.get(filepath));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
