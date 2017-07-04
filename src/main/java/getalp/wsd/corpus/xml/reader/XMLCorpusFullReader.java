package getalp.wsd.corpus.xml.reader;

import getalp.wsd.corpus.Corpus;
import getalp.wsd.corpus.Document;
import getalp.wsd.corpus.Paragraph;
import getalp.wsd.corpus.Sentence;
import getalp.wsd.corpus.Word;

public class XMLCorpusFullReader
{
	public Corpus load(String path) 
    {
	    SequentialReader reader = new SequentialReader();
	    reader.load(path);
	    return reader.currentCorpus;
    }

	private class SequentialReader extends XMLCorpusSequentialReader 
	{
	    private Corpus currentCorpus;
	    
	    private Document currentDocument;
	    
	    private Paragraph currentParagraph;
	    
	    private Sentence currentSentence;
	    
	    private Word currentWord;

	    @Override
	    public void readBeginCorpus(Corpus corpus)
	    {
	        currentCorpus = corpus;
	    }

	    @Override
	    public void readBeginDocument(Document document)
	    {
	        currentDocument = document;
	        currentDocument.setParentCorpus(currentCorpus);
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
	        currentWord = word;
	        currentWord.setParentSentence(currentSentence);
	    }
	}
}
