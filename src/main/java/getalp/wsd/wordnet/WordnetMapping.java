package getalp.wsd.wordnet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordnetMapping
{    
    private Map<String, String> map;
    
    private WordnetHelper wnFrom;
    
    private WordnetHelper wnTo;
    
    private static final String mappingDirectory = "data/wordnet/mapping";
    
    private static final Map<String, WordnetMapping> loadedMappings = new HashMap<>();

    private WordnetMapping(WordnetHelper wnFrom, WordnetHelper wnTo)
    {
        this.wnFrom = wnFrom;
        this.wnTo = wnTo;
        load(mappingDirectory);
    }

    private void load(String mappingDirectory)
    {
        map = new HashMap<>();
        String wnFromTo = wnFrom.getVersion() + "-" + wnTo.getVersion();
        load(mappingDirectory + "/mapping-" + wnFromTo + "/wn" + wnFromTo + ".noun", "n");
        load(mappingDirectory + "/mapping-" + wnFromTo + "/wn" + wnFromTo + ".verb", "v");
        load(mappingDirectory + "/mapping-" + wnFromTo + "/wn" + wnFromTo + ".adj", "a");
        load(mappingDirectory + "/mapping-" + wnFromTo + "/wn" + wnFromTo + ".adv", "r");
    }
    
    private void load(String mappingFile, String postag)
    {
        try
        {
            loadWithException(mappingFile, postag);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private void loadWithException(String mappingFile, String postag) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader(mappingFile));
        String line;
        while ((line = br.readLine()) != null) 
        {
           String[] tokens = line.split("\\s+");
           int from = Integer.valueOf(tokens[0]);
           int bestTo = Integer.valueOf(tokens[1]);
           double bestProb = Double.valueOf(tokens[2]);
           for (int i = 3 ; i + 1 < tokens.length ; i += 2)
           {
               int candidateTo = Integer.valueOf(tokens[i]);
               double candidateProb = Double.valueOf(tokens[i+1]);
               if (candidateProb > bestProb)
               {
                   bestTo = candidateTo;
                   bestProb = candidateProb;
               }
           }
           addToMap(postag + from, postag + bestTo);
        }
        br.close();
    }
    
    private void addToMap(String fromSynsetKey, String toSynsetKey)
    {
        List<String> fromSenseKeys = wnFrom.getSenseKeyListFromSynsetKey(fromSynsetKey);
        List<String> toSenseKeys = wnTo.getSenseKeyListFromSynsetKey(toSynsetKey);
        for (String fromSenseKey : fromSenseKeys)
        {
            String bestToSenseKey = null;
            String fromLemma = fromSenseKey.substring(0, fromSenseKey.indexOf("%"));
            for (String toSenseKey : toSenseKeys)
            {
                String toLemma = toSenseKey.substring(0, toSenseKey.indexOf("%"));
                if (fromLemma.equals(toLemma))
                {
                    bestToSenseKey = toSenseKey;
                    break;
                }
            }
            if (bestToSenseKey != null)
            {
                map.put(fromSenseKey, bestToSenseKey);
            }
            else if (wnTo.isSenseKeyExists(fromSenseKey))
            {
                map.put(fromSenseKey, fromSenseKey);
            }
            else if (wnTo.isSenseKeyExists(fromSenseKey.replaceAll("%5", "%3")))
            {
                map.put(fromSenseKey, fromSenseKey.replaceAll("%5", "%3"));
            }
            else if (wnTo.isSenseKeyExists(fromSenseKey.replaceAll("%3", "%5")))
            {
                map.put(fromSenseKey, fromSenseKey.replaceAll("%3", "%5"));
            }  
        }
    }

    public static WordnetMapping wnXtoY(int versionX, int versionY)
    {
        if (!loadedMappings.containsKey(versionX + "to" + versionY))
        {
            loadedMappings.put(versionX + "to" + versionY, new WordnetMapping(WordnetHelper.wn(versionX), WordnetHelper.wn(versionY)));
        }
        return loadedMappings.get(versionX + "to" + versionY);
    }
    
    public static WordnetMapping wn16to21()
    {
        return wnXtoY(16, 21);
    }

    public static WordnetMapping wn21to30()
    {
        return wnXtoY(21, 30);
    }

    public static WordnetMapping wn30to21()
    {
        return wnXtoY(30, 21);
    }

    public static WordnetMapping wn21to16()
    {
        return wnXtoY(21, 16);
    }
    
    public String fromXtoY(String senseKeyX)
    {
        return map.get(senseKeyX);
    }
/*
    public String fromYtoX(String senseKeyY)
    {
        return map.inverse().get(senseKeyY);
    }
*/
    public static String from16to21(String senseKeyX)
    {
        return wn16to21().fromXtoY(senseKeyX);
    }

    public static String from21to16(String senseKeyX)
    {
        return wn21to16().fromXtoY(senseKeyX);
    }

    public static String from21to30(String senseKeyX)
    {
        return wn21to30().fromXtoY(senseKeyX);
    }

    public static String from30to21(String senseKeyX)
    {
        return wn30to21().fromXtoY(senseKeyX);
    }

    public static String from16to30(String senseKeyX)
    {
        return wnXtoY(16, 30).fromXtoY(senseKeyX);
    }

    public static String from30to16(String senseKeyX)
    {
        return wnXtoY(30, 16).fromXtoY(senseKeyX);
    }
}
