import java.io.File;
import java.util.List;
import java.lang.NullPointerException;
import java.util.Iterator;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ClientImporter{
    /*  Class to import clients from CSV into memory.
        File should be named clients_2017.csv.
     */
    
    // constants
    private static final String prefix = "clients_";
    
    // class attributes
    private int year;
    private List<Client> clients;
    private File[] files;

    // constructor
    public ClientImporter(int year, String dir) 
    {
        // set year
        this.year = year;

        // initiate clients list
        this.clients = new ArrayList<Client>();

        // find files to import
        findFiles(dir);
    }

    // find client by id
    public Client find(int id)
    {        
        for(int j=0;j<clients.size();j++) {
            if(clients.get(j).getId() == id) {
                return clients.get(j);
            }
        }
        return null;
    }

    // iterate files and process them
    public void processFiles() 
    {    
        for (int i=0; i<files.length; i++) {
            importClients(files[i]);
        } 
    }

    // return number of files
    public int filesCount() {
        return files.length;
    }

    // return number of files
    public int clientsCount() {
        return clients.size();
    }

    // find files in directory that match the prefix
    private void findFiles(String dir)
    {
        String filePrefix = prefix+this.year;

        // your directory
        File f = new File(dir);
        FilenameFilter fn = new FilenameFilter() 
        {
            public boolean accept(File dir, String name) {
                return name.startsWith(filePrefix) && name.endsWith(".csv");
            }
        };
        File[] matchingFiles = f.listFiles(fn);
        files = matchingFiles;
    }

    // process rows from file
    private void importClients(File f) 
    {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {
            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {

                String[] clientLine = line.split(cvsSplitBy);
                
                // read data from client line
                int clientId = Integer.parseInt(clientLine[0]);
                String clientName = clientLine[1];
                String street = clientLine[6] + " " + clientLine[7];
                String postalcode = clientLine[9];
                String city = clientLine[10];

                // complain when address is empty
                if(street.equals("") || postalcode.equals("") || city.equals(""))
                {
                    System.out.println("Address incomplete for client " + clientId);
                }
                
                // instantiate new client from data
                Client c = new Client(clientName
                                    , clientId
                                    , new Address(street, postalcode, city));

                clients.add(c);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}