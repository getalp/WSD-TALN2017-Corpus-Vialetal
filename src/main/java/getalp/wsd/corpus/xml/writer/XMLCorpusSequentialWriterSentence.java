package getalp.wsd.corpus.xml.writer;

import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;

public class XMLCorpusSequentialWriterSentence extends XMLCorpusSequentialWriter
{
    public XMLCorpusSequentialWriterSentence(String path)
    {
        super(path);
    }
    
    public void writeHeader()
    {
        writeBeginCorpus();
        writeBeginDocument();
        writeBeginParagraph();
    }
    
    public void writeSentence(Sentence sentence)
    {
        writeBeginSentence(sentence);
        for (Word word : sentence.getWords())
        {
            writeWord(word);
        }
        writeEndSentence();
    }
    
    public void writeFooter()
    {
        writeEndParagraph();
        writeEndDocument();
        writeEndCorpus();
    }
}
