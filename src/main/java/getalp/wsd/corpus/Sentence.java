package getalp.wsd.corpus;

import java.util.List;

public class Sentence extends ParentLexicalEntity
{
	public Sentence()
	{

	}

	public Sentence(String value)
	{
	    addWordsFromString(value);
	}
	
	public Sentence(Paragraph parentParagraph)
	{
		setParentParagraph(parentParagraph);
	}
	
	public Sentence(String value, Paragraph parentParagraph)
	{
		setParentParagraph(parentParagraph);
        addWordsFromString(value);
	}
	
	public void addWord(Word word)
	{
	    addChild(word);
	}
	
	public void removeWord(Word word)
	{
	    removeChild(word);
	}
	
	public void removeAllWords()
	{
	    removeAllChildren();
	}
	
    public List<Word> getWords()
	{
		return getChildren();
	}
	
	public Paragraph getParentParagraph()
	{
		return getParent();
	}
	
	public Document getParentDocument()
	{
	    return getParentParagraph().getParentDocument();
	}

	public void setParentParagraph(Paragraph parentParagraph)
	{
		setParent(parentParagraph);
	}
    
    public Sentence clone()
    {
        Sentence newSentence = new Sentence();
        transfertWordsToCopy(newSentence);
        transfertAnnotationsToCopy(newSentence);
        return newSentence;
    }
    
    public void transfertWordsToCopy(Sentence other)
    {
        for (Word word : getWords())
        {
            other.addWord(word.clone());
        }
    }
    
    public void addWordsFromString(String value)
    {
        String[] wordsArray = value.split("\\s+");
        for (String wordInArray : wordsArray)
        {
            addWord(new Word(wordInArray));
        }
    }
    
	public String toString()
	{
		String ret = "";
		for (Word word : getWords())
		{
			ret += word.toString() + " ";
		}
		return ret.trim();
	}
}
