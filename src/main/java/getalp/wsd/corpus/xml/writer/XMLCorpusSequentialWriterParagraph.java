package getalp.wsd.corpus.xml.writer;

import getalp.wsd.corpus.*;

public class XMLCorpusSequentialWriterParagraph extends XMLCorpusSequentialWriter
{
    public XMLCorpusSequentialWriterParagraph(String path)
    {
        super(path);
    }
    
    public void writeHeader()
    {
        writeBeginCorpus();
        writeBeginDocument();
    }
    
    public void writeParagraph(Paragraph paragraph)
    {
        writeBeginParagraph(paragraph);
        for (Sentence sentence : paragraph.getSentences())
        {
            writeBeginSentence(sentence);
            for (Word word : sentence.getWords())
            {
                writeWord(word);
            }
            writeEndSentence();
        }
        writeEndParagraph();
    }
    
    public void writeFooter()
    {
        writeEndDocument();
        writeEndCorpus();
    }
}
