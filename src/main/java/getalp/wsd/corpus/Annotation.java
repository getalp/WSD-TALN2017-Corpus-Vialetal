package getalp.wsd.corpus;

public class Annotation
{
	private LexicalEntity targetEntity;
	
	private String annotationName;
	
	private String annotationValue;

    public Annotation(String name, String value)
    {
        this(null, name, value);
    }

	public Annotation(LexicalEntity target, String name, String value)
	{
		this.targetEntity = target;
		if (name == null) this.annotationName = "";
		else this.annotationName = name;
		if (value == null) this.annotationValue = "";
		else this.annotationValue = value;
	}

    public LexicalEntity getTargetEntity()
    {
        return targetEntity;
    }

    public String getAnnotationName()
    {
        return annotationName;
    }

    public String getAnnotationValue()
    {
        return annotationValue;
    }
    
    public String toString()
    {
        return annotationName + "=" + annotationValue;
    }

}
