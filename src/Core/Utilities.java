package Core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public static List<String[]> readAllLineFromTxt(String pathToCsv)
    {
        //Reads all line
        String row;
        int linecounter = 0;
        List<String[]> data = new ArrayList<>();
        try
        {
            BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
            while ((row = csvReader.readLine()) != null)
            {
                //Check for comments and blank lines
                if(row.isEmpty() || row.startsWith("#"))
                    continue;

                String[] rawdata = row.split(",");
                String[] trimmed = new String[rawdata.length];
                for (int i = 0; i < rawdata.length; i++)
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