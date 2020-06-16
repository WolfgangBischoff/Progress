package Core;

import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.File;
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
                if (row.isEmpty() || row.startsWith("#"))
                    continue;

                //String[] rawdata = row.split(",");
                String[] rawdata = row.split(";");
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


    public static Element readXMLFile(String file_path)
    {
        //https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            File file = new File(file_path);
            Document doc = builder.parse(file);
            //System.out.println(doc.getDocumentElement());
            return doc.getDocumentElement();
        } catch (ParserConfigurationException | SAXException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            throw new IllegalArgumentException("File does not exist: " + file_path);
        }

        throw new RuntimeException("Uncatched Exception for path: " + file_path);
    }

    public static Image readImage(String path)
    {
        return new Image(path);
    }

}