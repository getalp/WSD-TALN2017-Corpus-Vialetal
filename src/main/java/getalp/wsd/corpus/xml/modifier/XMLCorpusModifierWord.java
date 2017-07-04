package getalp.wsd.corpus.xml.modifier;

import getalp.wsd.corpus.*;

public class XMLCorpusModifierWord extends XMLCorpusDuplicator
{
    public void modifyWord(Word word)
    {
        
    }

    @Override
    public void readWord(Word word)
    {
        modifyWord(word);
        super.readWord(word);
    }
}
