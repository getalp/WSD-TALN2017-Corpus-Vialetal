package getalp.wsd.corpus.xml.modifier;

import getalp.wsd.corpus.*;
import getalp.wsd.corpus.xml.reader.XMLCorpusSequentialReader;
import getalp.wsd.corpus.xml.writer.XMLCorpusSequentialWriter;

public class XMLCorpusDuplicator extends XMLCorpusSequentialReader
{
    private XMLCorpusSequentialWriter out;

    public void setOutputPath(String outputPath)
    {
        out = new XMLCorpusSequentialWriter(outputPath);
    }
    
    public void load(String inputPath, String outputPath)
    {
        out = new XMLCorpusSequentialWriter(outputPath);
        super.load(inputPath);
    }
    
    @Override
    public void readBeginCorpus(Corpus corpus)
    {
        out.writeBeginCorpus(corpus);
    }

    @Override
    public void readBeginDocument(Document document)
    {
        out.writeBeginDocument(document);
    }

    @Override
    public void readBeginParagraph(Paragraph paragraph)
    {
        out.writeBeginParagraph(paragraph);
    }

    @Override
    public void readBeginSentence(Sentence sentence)
    {
        out.writeBeginSentence(sentence);
    }

    @Override
    public void readWord(Word word)
    {
        out.writeWord(word);
    }

    @Override
    public void readEndSentence()
    {
        out.writeEndSentence();
    }

    @Override
    public void readEndParagraph()
    {
        out.writeEndParagraph();
    }

    @Override
    public void readEndDocument()
    {
        out.writeEndDocument();
    }

    @Override
    public void readEndCorpus()
    {
        out.writeEndCorpus();
    }
}
