package getalp.wsd.corpus.xml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import getalp.wsd.corpus.Paragraph;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;

public class XMLCorpusSequentialReaderParagraph extends XMLCorpusSequentialReader
{
    private List<Consumer<Paragraph>> callbacks = new ArrayList<>();

    private Paragraph currentParagraph;
    
    private Sentence currentSentence;
    
	public void onParagraphRead(Consumer<Paragraph> callback)
	{
	    callbacks.add(callback);
	}

    @Override
    public void readBeginParagraph(Paragraph paragraph)
    {
        currentParagraph = paragraph;
    }

    @Override
    public void readBeginSentence(Sentence sentence)
    {
        currentSentence = sentence;
        currentSentence.setParentParagraph(currentParagraph);
    }

    @Override
    public void readWord(Word word)
    {
        Word currentWord = word;
        currentWord.setParentSentence(currentSentence);
    }

    @Override
    public void readEndParagraph()
    {
        for (Consumer<Paragraph> cb : callbacks)
        {
            cb.accept(currentParagraph);
        }
    }
}
