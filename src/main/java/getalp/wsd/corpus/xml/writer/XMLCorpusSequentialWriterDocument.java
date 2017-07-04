package getalp.wsd.corpus.xml.writer;

import getalp.wsd.corpus.*;

public class XMLCorpusSequentialWriterDocument extends XMLCorpusSequentialWriter
{
    public XMLCorpusSequentialWriterDocument(String path)
    {
        super(path);
    }
    
    public void writeHeader()
    {
        writeBeginCorpus();
    }
    
    public void writeDocument(Document document)
    {
        writeBeginDocument(document);
        for (Paragraph paragraph : document.getParagraphs())
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
        writeEndDocument();
    }
    
    public void writeFooter()
    {
        writeEndCorpus();
    }
}
