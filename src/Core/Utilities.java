package Core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class Utilities
{
    public static Double roundTwoDigits(Double input)
    {
        DecimalFormatSymbols point = new DecimalFormatSymbols();
        point.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.##", point);
        return Double.valueOf(df.format(input));
    }

    public static boolean tryParseInt(String value)
    {
        try
        {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public static String[] readFirstLineFromTxt(String pathToCsv)
    {//Reads first line
        String row = null;
        String[] rawdata = null;
        try
        {
            BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
            row = csvReader.readLine();
            rawdata = row.split(",");
            csvReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        String[] trimmed = new String[rawdata.length];
        for(int i=0; i<rawdata.length; i++)
            trimmed[i] = rawdata[i].trim();

        return trimmed;
    }

    public static List<String[]> readAllLineFromTxt(String pathToCsv, boolean ignoreFirstLine)
    {
        /*
        Class myClass = Utilities.class;
        ClassLoader loader = myClass.getClassLoader();
        URL myURL = loader.getResource("test.csv");
        String path = myURL.getPath();
        path = path.replaceAll("%20", " ");
        System.out.println(path);*/

        //Reads all line
        String row = null;
        Integer linecounter = 0;
        List<String[]> data = new ArrayList<>();
        try
        {
            BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
            while((row = csvReader.readLine()) != null)
            {
                if(ignoreFirstLine && linecounter == 0)
                {
                    linecounter++;
                    continue;
                }


                String[] rawdata = row.split(",");
                String[] trimmed = new String[rawdata.length];
                for(int i=0; i<rawdata.length; i++)
                    trimmed[i] = rawdata[i].trim();
                data.add(trimmed);
                linecounter++;
            }
            csvReader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

}