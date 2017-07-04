package getalp.wsd.corpus.xml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;

public class XMLCorpusSequentialReaderSentence extends XMLCorpusSequentialReader
{
	private List<Consumer<Sentence>> callbacks = new ArrayList<>();

    private Sentence currentSentence;
 
	public void onSentenceRead(Consumer<Sentence> callback)
	{
	    callbacks.add(callback);
	}

	@Override
    public void readBeginSentence(Sentence sentence)
    {
        currentSentence = sentence;
    }

    @Override
    public void readWord(Word word)
    {
        Word currentWord = word;
        currentWord.setParentSentence(currentSentence);
    }

    @Override
    public void readEndSentence()
    {
        for (Consumer<Sentence> cb : callbacks)
        {
            cb.accept(currentSentence);
        }
    }
}
