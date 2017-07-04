package getalp.wsd.corpus.xml.modifier;

import getalp.wsd.corpus.*;

public class XMLCorpusModifierParagraph extends XMLCorpusDuplicator
{
    private Paragraph currentParagraph;
    
    private Sentence currentSentence;
    
    public void modifyParagraph(Paragraph paragraph)
    {
        
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
        word.setParentSentence(currentSentence);
    }

    @Override
    public void readEndSentence()
    {

    }

    @Override
    public void readEndParagraph()
    {
        modifyParagraph(currentParagraph);
        super.readBeginParagraph(currentParagraph);
        for (Sentence sentence : currentParagraph.getSentences())
        {
            super.readBeginSentence(sentence);
            for (Word word : sentence.getWords())
            {
                super.readWord(word);
            }
            super.readEndSentence();
        }
        super.readEndParagraph();
    }
}
