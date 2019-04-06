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
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.text.ParseException;

public class VisitImporter{
    /*  Class to import visits from CSV into memory.
        File should be named visits_2017_marieke.csv.
     */
    
    // constants
    private static final String prefix = "visits";
    
    // class attributes
    private int year;
    private List<Visit> visits;
    // private List<Therapist> therapists;
    private Therapist currentTherapist;
    //private Therapist boukje;
    private File[] files;
    private ClientImporter ci;
    private String dir;

    // constructor
    public VisitImporter(int year, String t, String dir, ClientImporter ci) 
    {
        // set year
        this.year = year;
        this.ci = ci;
        this.dir = dir;

        // initiate vists list
        this.visits = new ArrayList<Visit>();
        // this.therapists = new ArrayList<Therapist>();

    }

    public void setTherapist(Therapist t) {
        this.currentTherapist = t;
    }

    public Therapist getTherapist() {
        return currentTherapist;
    }

    // iterate files and process them
    public void processFiles() 
    {    
        for (int i=0; i<files.length; i++) {
        // check if all client exists, then processs the file
          if(checkVisits(files[i]) == 0) {           
            importVisits(files[i]);
          }
        } 
    }

    // return number of files
    public int filesCount() {
        return files.length;
    }

    // return number of files
    public int visitsCount() {
        return visits.size();
    }

    // find files in directory that match the prefix
    public void findFiles()
    {
        String filePrefix = prefix+"_"+this.year+"_"+currentTherapist.getName();

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

    private Visit findVisit(Visit v) 
    {
        for(int i=0;i<visits.size();i++) {
            Visit a = visits.get(i);
            if(a.equals(v))
            {
                return a;
            }
        }
        return null;
    }

    public List<Visit> getVisits(Therapist t, Date d) {

        List<Visit> v = new ArrayList<Visit>();

        for(int i=0; i<visits.size();i++) {
            Visit w = visits.get(i);

            // check if the same
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String date1 = sdf.format(w.getDate());
            String date2 = sdf.format(d);

            if(w.getTherapist().getName().equals(t.getName())
            && date1.equals(date2)) {
                v.add(w);
            }
        }
        return v;
    }


// check if clients exists for all Visits
private int checkVisits(File f) {
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ";";
    int notFound = 0;

    try {
        br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            String[] visitLine = line.split(cvsSplitBy);
                                                                
            // find client
            Client c = ci.find(Integer.parseInt(visitLine[0]));                
            if(c == null) {
                notFound++;
                System.out.println("Client "+visitLine[0]+" not found for trip.");
                                                                                                                                                                                         }
                                                                                                                                                                                     }
                                                                                                                                                                                  } catch (FileNotFoundException e) {
                                                                                                                                                                                      e.printStackTrace();
                                                                                                                                                                                  } catch (IOException e) {
                                                                                                                                                                                      e.printStackTrace();
                                                                                                                                                                                  } catch (NullPointerException e) {
                                                                                                                                                                                      e.printStackTrace();
                                                                                                                                                                                  }finally {
                                                                                                                                                                                      if (br != null) {
                                                                                                                                                                                          try {
                                                                                                                                                                                             br.close();
                                                                                                                                                                                                                                                                                                                                                             } catch (IOException e) {
                                                                                                                                                                                                                                                                                                                                                                                 e.printStackTrace();
                                                                                                                                                                                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                                                                                                                                                                                                             return notFound;
                                                                                                                                                                                }
  
    // process rows from file
    private void importVisits(File f) 
    {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {
            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {

                String[] visitLine = line.split(cvsSplitBy);
                
                // find therapist name
                Therapist t = null;                
                if(visitLine[7].equals(currentTherapist.getCsvString())) {
                    t = currentTherapist;
                }

                // type of visit
                Boolean atHome = true;
                if(visitLine[9].equals("5000, Enkelvoudige extramurale ergotherapie")) {
                    atHome = true;
                } else if(visitLine[9].equals("5001, Toeslag thuisbehandeling voor enkelvoudige extramurale ergotherapie")) {
                    atHome = false;
                } else if(visitLine[9].equals("5002, Screening Directe Toegang Ergotherapie")) {
                    atHome = true;
                } 
                

                // find client
                Client c = ci.find(Integer.parseInt(visitLine[0]));                
                if(c == null) {
		    System.out.println("Client "+visitLine[0]+" not found while importing visit.");
                    throw new NullPointerException();
                }

                // make date
                DateFormat dateFormat = new SimpleDateFormat("d-M-yyyy");
                try {
                    Date d = dateFormat.parse(visitLine[4]);
                    Visit v = new Visit(d, t, c, atHome);

                    /* See if this visit was already added.
                       Yes: if atHome=true then update
                       No: just insert new visit
                    */
                    Visit foundVisit = findVisit(v);
                    if(foundVisit != null) {
                        // visit found
                        if(v.getAtHome()) {
                            foundVisit.setAtHome(true);
                            // System.out.println(foundVisit.toJSON());
                        }
                    }
                    else {
                        visits.add(v);
                        // System.out.println(v.toJSON());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }finally {
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
