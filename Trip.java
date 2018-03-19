import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

public class Trip {

    private Date date;
    private int sequence;
    private Client client;
    private Address fromAddress;
    private Address toAddress;
    private double distance; // in km

    public Trip(Date d, int s, Client c, Address f, Address t) {
        this.date = d;
        this.sequence = s;
        this.client = c;
        this.fromAddress = f;
        this.toAddress = t; 
        this.distance = 0.0;
    }

    public String toString() {
        return date.toString() + " " + client.getName() + " " + fromAddress.toString() + " " + toAddress.toString();
    }

    public String toCSV() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        String formattedDate = sdf.format(date);

        return formattedDate + ", " + client.getId() + ", " + fromAddress.toString() + ", " + toAddress.toString() + ", " + distance;
    }

    public void calcDistance() {
        this.distance = getDistance();
    }

    public double getDistance()
    {
        String from = fromAddress.toString();
        String to = toAddress.toString();

        String apikey = "AIzaSyCFDvRn-tkefR_tM0RELAuDMp1acbhkKr8";
        String requestUrl = "https://maps.googleapis.com/maps/api/distancematrix/xml?units=metric&origins="+from.replace(" ", "%20")+"&destinations="+to.replace(" ", "%20")+"&key="+apikey;
        
        // System.out.println(requestUrl);

        String xmlString = "";
        
        try 
        {
            URL url = new URL(requestUrl);    
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String s, s2 = new String();
            while ((s = in.readLine()) != null)
                s2 += s + "\n";
            in.close();
            
            xmlString = s2;
        } 
        catch (IOException e)  {
            e.printStackTrace();
        }
        return parseXML(xmlString); 
    }

    private double parseXML(String xmlString) {

        String km = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));

            XPath xPath = XPathFactory.newInstance().newXPath();
            Node node = (Node) xPath.evaluate("/DistanceMatrixResponse/row/element/distance/value/text()", document, XPathConstants.NODE);
            km = node.getNodeValue();
        } 
        catch(Exception e) {
            System.out.println("Error processing distance for trip for client: " + client.getId());
            // e.printStackTrace();
        }

        if(km.equals("")) return 0.0;
        else {
            // return kilometers
            return Double.parseDouble(km)/1000; 
        }
    }
}