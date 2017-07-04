package getalp.wsd.corpus.xml.writer;

import getalp.wsd.corpus.Word;

public class XMLCorpusSequentialWriterWord extends XMLCorpusSequentialWriter
{
    public XMLCorpusSequentialWriterWord(String path)
    {
        super(path);
    }
    
    public void writeHeader()
    {
        writeBeginCorpus();
        writeBeginDocument();
        writeBeginParagraph();
        writeBeginSentence();
    }
    
    public void writeWord(Word word)
    {
        super.writeWord(word);
    }
    
    public void writeFooter()
    {
        writeEndSentence();
        writeEndParagraph();
        writeEndDocument();
        writeEndCorpus();
    }
}
