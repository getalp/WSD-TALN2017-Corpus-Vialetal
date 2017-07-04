package getalp.wsd.corpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalEntity
{
    private Map<String, String> annotationsAsMap;

    private List<Annotation> annotationsAsList;

    private ParentLexicalEntity parent;
    
    public LexicalEntity()
    {
        annotationsAsList = new ArrayList<>();
        annotationsAsMap = new HashMap<>();
        parent = null;
    }

    public List<Annotation> getAnnotations()
    {
        return annotationsAsList;
    }

    public String getAnnotationValue(String annotationName)
    {
        if (!annotationsAsMap.containsKey(annotationName)) return "";
        return annotationsAsMap.get(annotationName);
    }

    public List<String> getAnnotationValues(String annotationName, String delimiter)
    {
        if (!annotationsAsMap.containsKey(annotationName)) return Collections.emptyList();
        return Arrays.asList(annotationsAsMap.get(annotationName).split(delimiter));
    }

    public void setAnnotation(String annotationName, String annotationValue)
    {
    	if (annotationName == null || annotationName.equals("") || annotationValue == null || annotationValue.equals("")) return;
        int oldIndex = getAnnotationIndex(annotationName);
        if (hasAnnotation(annotationName))
        {
        	removeAnnotation(annotationName);
        }
        if (oldIndex == -1)
        {
            annotationsAsList.add(new Annotation(this, annotationName, annotationValue));
        }
        else
        {
            annotationsAsList.add(oldIndex, new Annotation(this, annotationName, annotationValue));
        }
        annotationsAsMap.put(annotationName, annotationValue);
    }
    
    public void removeAnnotation(String annotationName)
    {
    	annotationsAsList.removeIf((Annotation a) -> { return a.getAnnotationName().equals(annotationName); });
    	annotationsAsMap.remove(annotationName);
    }
    
    public boolean isAnnotated()
    {
        return !annotationsAsList.isEmpty();
    }
    
    public boolean hasAnnotation(String annotationName)
    {
        return annotationsAsMap.containsKey(annotationName);
    }
    
    public int getAnnotationIndex(String annotationName)
    {
        for (int i = 0 ; i < annotationsAsList.size() ; i++)
        {
            if (annotationsAsList.get(i).getAnnotationName().equals(annotationName))
            {
                return i;
            }
        }
        return -1;
    }
    
    public void transfertAnnotationsToCopy(LexicalEntity copy)
    {
    	for (Annotation a : this.annotationsAsList)
    	{
    		copy.setAnnotation(a.getAnnotationName(), a.getAnnotationValue());
    	}
    }

    protected void setParent(ParentLexicalEntity parent)
    {
        if (this.parent == parent) return;
        if (this.parent != null)
        {
            this.parent.removeChild(this);
        }
        this.parent = parent;
        if (this.parent != null)
        {
            parent.addChild(this);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends ParentLexicalEntity> T getParent()
    {
        return (T) parent;
    }
}
