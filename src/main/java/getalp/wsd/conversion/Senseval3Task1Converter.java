package getalp.wsd.conversion;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import getalp.wsd.corpus.Document;
import getalp.wsd.corpus.Paragraph;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;
import getalp.wsd.corpus.xml.writer.XMLCorpusSequentialWriterDocument;
import getalp.wsd.utils.POSHelper;
import getalp.wsd.xml.SAXBasicHandler;

public class Senseval3Task1Converter extends SAXBasicHandler
{
    private XMLCorpusSequentialWriterDocument out;
    
    private Document currentDocument;
    
    private Paragraph currentParagraph;
    
    private Sentence currentSentence;
    
    private Word currentWord;

    private int wnVersion;

    public void convert(String inputPath, String outputPath, int wnVersion)
    {
        out = new XMLCorpusSequentialWriterDocument(outputPath);
        this.wnVersion = wnVersion;
        try
        {
            XMLReader saxReader = XMLReaderFactory.createXMLReader();
            saxReader.setContentHandler(this);
            out.writeHeader();
            saxReader.parse(inputPath + "/d000.xml");
            saxReader.parse(inputPath + "/d001.xml");
            saxReader.parse(inputPath + "/d002.xml");
            out.writeFooter();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
    {
        if (localName.equals("context"))
        {
            currentDocument = new Document();
            currentDocument.setAnnotation("id", atts.getValue("filename"));
            currentParagraph = new Paragraph(currentDocument);
        }
        else if (localName.equals("s"))
        {
            currentSentence = new Sentence(currentParagraph);
        }
        else if (localName.equals("wf"))
        {
            currentWord = new Word("null", currentSentence);
            currentWord.setAnnotation("id", atts.getValue("id"));
            String lemma = atts.getValue("lemma");
            if (lemma != null && lemma.equals("UNKNOWN")) lemma = null;
            currentWord.setAnnotation("lemma", lemma);
            String pos = POSHelper.processPOS(atts.getValue("pos"));
            if (pos.equals("x")) pos = "";
            String senseTag = "";
            if (lemma != null && atts.getValue("lexsn") != null && !atts.getValue("wnsn").equals("-1"))
            {
                pos = POSHelper.processPOS(Integer.valueOf(atts.getValue("lexsn").substring(0, 1)));
                if (pos.equals("x")) pos = "";
                String[] senseKeys = atts.getValue("lexsn").split(";");
                String newSenseKey = "";
                for (String senseKey : senseKeys)
                {
                    newSenseKey += ";" + lemma + "%" + senseKey;
                }
                senseTag = newSenseKey.substring(1);
            }
            currentWord.setAnnotation("pos", pos);
            currentWord.setAnnotation("wn" + wnVersion + "_key", senseTag);
            resetAndStartSaveCharacters();
        }
        else if (localName.equals("punc"))
        {
            currentWord = new Word(currentSentence);
            resetAndStartSaveCharacters();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if (localName.equals("context"))
        {
            out.writeDocument(currentDocument);
        }
        else if (localName.equals("wf"))
        {
            currentWord.setValue(getAndStopSaveCharacters());
        }
        else if (localName.equals("punc"))
        {
            currentWord.setValue(getAndStopSaveCharacters());
        }
    }
}
