package getalp.wsd.corpus.xml.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import getalp.wsd.corpus.*;

public class XMLCorpusSequentialReaderDocument extends XMLCorpusSequentialReader
{
	private List<Consumer<Document>> callbacks = new ArrayList<>();

    private Document currentDocument;

    private Paragraph currentParagraph;
    
    private Sentence currentSentence;
    
	public void onDocumentRead(Consumer<Document> callback)
	{
	    callbacks.add(callback);
	}

    @Override
    public void readBeginDocument(Document document)
    {
        currentDocument = document;
    }

    @Override
    public void readBeginParagraph(Paragraph paragraph)
    {
        currentParagraph = paragraph;
        currentParagraph.setParentDocument(currentDocument);
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
    public void readEndDocument()
    {
        for (Consumer<Document> cb : callbacks)
        {
            cb.accept(currentDocument);
        }
    }

}
