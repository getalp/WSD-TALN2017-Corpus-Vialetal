package getalp.wsd.corpus.xml.modifier;

import getalp.wsd.corpus.*;

public class XMLCorpusModifierSentence extends XMLCorpusDuplicator
{
    private Sentence currentSentence;
    
    public void modifySentence(Sentence sentence)
    {
        
    }

    @Override
    public void readBeginSentence(Sentence sentence)
    {
        currentSentence = sentence;
    }

    @Override
    public void readWord(Word word)
    {
        word.setParentSentence(currentSentence);
    }

    @Override
    public void readEndSentence()
    {
        modifySentence(currentSentence);
        super.readBeginSentence(currentSentence);
        for (Word word : currentSentence.getWords())
        {
            super.readWord(word);
        }
        super.readEndSentence();
    }
}
