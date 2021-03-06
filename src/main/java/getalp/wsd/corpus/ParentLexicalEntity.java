package getalp.wsd.corpus;

import java.util.ArrayList;
import java.util.List;

public class ParentLexicalEntity extends LexicalEntity
{
    private List<LexicalEntity> children;
    
    protected ParentLexicalEntity()
    {
        children = new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends LexicalEntity> List<T> getChildren()
    {
        return (List<T>) children;
    }
    
    protected void addChild(LexicalEntity child)
    {
        if (children.contains(child)) return;
        children.add(child);
        child.setParent(this);
    }
    
    protected <T extends LexicalEntity> void addChildren(List<T> children)
    {
        for (LexicalEntity child : children)
        {
            addChild(child);
        }
    }
    
    protected void removeChild(LexicalEntity child)
    {
        if (!children.contains(child)) return;
        children.remove(child);
        child.setParent(null);
    }
    
    protected void removeAllChildren()
    {
        List<LexicalEntity> childrenBefore = new ArrayList<>(this.children);
        for (LexicalEntity child : childrenBefore)
        {
            removeChild(child);
        }
    }
}
