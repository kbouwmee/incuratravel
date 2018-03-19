import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class IncuraTravel {

    private static Properties readProperties(String configFile, String t) {
        Properties prop = new Properties();
        InputStream input = null;
    
        try {
    
            input = new FileInputStream(configFile);
    
            // load a properties file
            prop.load(input);
    
            // set therapist properties
            prop.setProperty("therapist.match", prop.getProperty(t+".match"));
            prop.setProperty("therapist.street", prop.getProperty(t+".street"));
            prop.setProperty("therapist.postalcode", prop.getProperty(t+".postalcode"));
            prop.setProperty("therapist.city", prop.getProperty(t+".city"));

            // get the properties value
            System.out.println("======================================");
            System.out.println("==========Found properties============");
            System.out.println("======================================");
            System.out.println("Practice street       : " + prop.getProperty("practice.street"));
            System.out.println("Practice postalcode   : " + prop.getProperty("practice.postalcode"));
            System.out.println("practice city         : " + prop.getProperty("practice.city"));

            // get properties of therapist
            System.out.println("Therapist match string: " + prop.getProperty("therapist.match"));
            System.out.println("Therapist street      : " + prop.getProperty("therapist.street"));
            System.out.println("Therapist postalcode  : " + prop.getProperty("therapist.postalcode"));
            System.out.println("Therapist city        : " + prop.getProperty("therapist.city"));
            System.out.println("======================================");

    
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    public static void main(String[] args) 
    {
        if(args.length != 5) {
            System.out.println("Use following parameters:");
            System.out.println("- year to import");
            System.out.println("- therapist name (marieke or boukje)");
            System.out.println("- config file");
            System.out.println("- directory to import from");
            System.out.println("- directory to import from");
        } else {

            int year = Integer.parseInt(args[0]);
            String therapist  = args[1];
            String configFile = args[2];
            String inputdir   = args[3];
            String outputdir  = args[4];

            Properties p = readProperties(configFile, therapist);
            System.out.println("==============Processing==============");
            System.out.println("======================================");
            System.out.println("Importing data from year        : " + year);
            System.out.println("Importing data from therapist   : " + therapist);
            System.out.println("Importing from directory        : " + inputdir);
            System.out.println("Exporting to directory          : " + outputdir);

            ClientImporter ci = new ClientImporter(year, inputdir);
            
            System.out.println("Number of client files found    : " + ci.filesCount());

            ci.processFiles();

            System.out.println("Number of clients records found : " + ci.clientsCount());

            Address therapistAddress = new Address(p.getProperty("therapist.street"), p.getProperty("therapist.postalcode"), p.getProperty("therapist.city"));
            String therapistMatch = p.getProperty("therapist.match");
            VisitImporter vi = new VisitImporter(year, therapist, inputdir, ci);
            vi.setTherapist(new Therapist(therapist, therapistAddress, therapistMatch));
            vi.findFiles();

            System.out.println("Number of visit files found     : " + vi.filesCount());

            vi.processFiles();

            System.out.println("Number of visits records found  : " + vi.visitsCount());

            TripsImporter ti = new TripsImporter(year, vi);
            Address practiceAddress = new Address(p.getProperty("practice.street"), p.getProperty("practice.postalcode"), p.getProperty("practice.city"));
            ti.setPracticeAddress(practiceAddress);
            
            System.out.println("Processing trips...");

            ti.processTrips();

            System.out.println("Number of trips created         : " + ti.getTripCount());

            ti.writeCSV(outputdir);
        }
    }
}