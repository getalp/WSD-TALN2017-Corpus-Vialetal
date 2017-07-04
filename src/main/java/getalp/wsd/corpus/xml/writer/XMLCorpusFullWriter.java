package getalp.wsd.corpus.xml.writer;

import getalp.wsd.corpus.Corpus;
import getalp.wsd.corpus.Document;
import getalp.wsd.corpus.Paragraph;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;

public class XMLCorpusFullWriter
{
    public void save(Corpus corpus, String path)
    {
        XMLCorpusSequentialWriter out = new XMLCorpusSequentialWriter(path);
        out.writeBeginCorpus(corpus);
        for (Document document : corpus.getDocuments())
        {
            out.writeBeginDocument(document);
            for (Paragraph paragraph : document.getParagraphs())
            {
                out.writeBeginParagraph(paragraph);
                for (Sentence sentence : paragraph.getSentences())
                {
                    out.writeBeginSentence(sentence);
                    for (Word word : sentence.getWords())
                    {
                        out.writeWord(word);
                    }
                    out.writeEndSentence();
                }
                out.writeEndParagraph();
            }
            out.writeEndDocument();
        }
        out.writeEndCorpus();
    }
}
