package getalp.wsd.corpus.xml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import getalp.wsd.corpus.Word;

public class XMLCorpusSequentialReaderWord extends XMLCorpusSequentialReader
{
	private List<Consumer<Word>> callbacks = new ArrayList<>();

	public void onWordRead(Consumer<Word> callback)
	{
	    callbacks.add(callback);
	}

    @Override
    public void readWord(Word word)
    {
        for (Consumer<Word> cb : callbacks)
        {
            cb.accept(word);
        }
    }
}
