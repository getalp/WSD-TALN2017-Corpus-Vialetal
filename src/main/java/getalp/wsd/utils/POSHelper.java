package getalp.wsd.utils;

public class POSHelper
{
    public static String processPOS(String pos)
    {
        pos = pos.toLowerCase();
        if (pos.startsWith("n"))
            return "n";
        if (pos.startsWith("v"))
            return "v";
        if (pos.startsWith("r") || pos.startsWith("adv"))
            return "r";
        if (pos.startsWith("j") || pos.startsWith("a"))
            return "a";
        else
            return "x";
    }
    
    public static String processPOS(int wordnetPOS)
    {
        if (wordnetPOS == 1) return "n";
        if (wordnetPOS == 2) return "v";
        if (wordnetPOS == 3) return "a";
        if (wordnetPOS == 4) return "r";
        if (wordnetPOS == 5) return "a";
        return "x";
    }
}
