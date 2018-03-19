import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class TripsImporter {
    
    private List<Trip> trips;
    private VisitImporter visitImporter;
    private int year;
    private Address practiceAddress;

    public TripsImporter(int y, VisitImporter vi)
    {
        this.trips = new ArrayList<Trip>();
        this.visitImporter = vi;
        this.year = y;
    }

    public void setPracticeAddress(Address a) {
        this.practiceAddress = a;
    }

    // return date from day of the year
    private Date getDateFromDay(int dayOfYear) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    public int getTripCount() {
        return trips.size();
    }

    public String toString() {
        String output = "";
        for(int t=0;t<trips.size();t++) {
            output += trips.get(t).toString() + System.lineSeparator();
        }
        return output;
    }

    public void writeCSV(String outputdir) {

        // CSV file name
        String csvFileName = outputdir + "/" + "trips_" + year + "_" + visitImporter.getTherapist().getName()+ ".csv";
        System.out.println("Writing to                      : " + csvFileName);
        try {
            PrintWriter pw = new PrintWriter(new File(csvFileName));
            StringBuilder sb = new StringBuilder();
            sb.append("Date");
            sb.append(',');
            sb.append("Client");
            sb.append(',');
            sb.append("From");
            sb.append(',');
            sb.append("To");
            sb.append(',');
            sb.append("Distance");
            sb.append('\n');

            for(int l=0;l<trips.size();l++) {
                Trip currentTrip = trips.get(l);
                sb.append(currentTrip.toCSV());
                sb.append('\n');
            }
            
            pw.write(sb.toString());
            pw.close();
            System.out.println("CSV file written!");
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 : 
            (total - current) * (System.currentTimeMillis() - startTime) / current;
    
        String etaHms = current == 0 ? "N/A" : 
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));
    
        StringBuilder string = new StringBuilder(140);   
        int percent = (int) (current * 100 / total);
        string
            .append('\r')
            .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
            .append(String.format(" %d%% [", percent))
            .append(String.join("", Collections.nCopies(percent, "=")))
            .append('>')
            .append(String.join("", Collections.nCopies(100 - percent, " ")))
            .append(']')
            .append(String.join("", Collections.nCopies(current == 0 ? (int) (Math.log10(total)) : (int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
            .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));
    
        System.out.print(string);
    }

    public void processTrips() {

        // walk all days
        long total = 365;
        long startTime = System.currentTimeMillis();

        for(int d=1; d<=total; d++) {
            printProgress(startTime, total, d);
            Date currentDate = getDateFromDay(d);
            
            Therapist currentTherapist = visitImporter.getTherapist();
            List<Visit> v = visitImporter.getVisits(currentTherapist, currentDate);
            
            // check if any visits on this date for the therapists
            if(v.size() > 0) {
                // System.out.println(currentDate.toString());

                // previous
                Address prevAddress = null;
                Client prevClient = null;

                for(int s=0;s<v.size();s++) {
                    Visit currentVisit = v.get(s);
                    prevClient = currentVisit.getClient();
                    Address fromAddress, toAddress;

                    if(s == 0) {
                        // start from home
                        fromAddress = currentTherapist.getAddress();
                    } else {
                        if(v.get(s-1).getAtHome()) {
                            // get from address from previous visit of the day
                            fromAddress = v.get(s-1).getClient().getAddress();
                        } else {
                            // get from address from practice
                            fromAddress = practiceAddress;
                        }
                    }
                    
                    if(v.get(s).getAtHome()) {
                        // get address from next visit of the day
                        toAddress = v.get(s).getClient().getAddress();
                    } else {
                        // get to address from practice
                        toAddress = practiceAddress;
                    }
                    prevAddress = toAddress;

                    if(toAddress.getStreet().equals("") || toAddress.getPostalcode().equals("") || toAddress.getCity().equals("")) {
                        System.out.println("Creating trip - incomplete address found for client " + currentVisit.getClient().getId());
                    }

                    // build new trip
                    Trip newTrip = new Trip(currentDate, s, currentVisit.getClient(), fromAddress, toAddress);
                    newTrip.calcDistance();

                    // add new trip to list
                    trips.add(newTrip);
                }
                // add last trip of the day to home
                Trip lastTrip = new Trip(currentDate, v.size(), prevClient, prevAddress, currentTherapist.getAddress());
                lastTrip.calcDistance();
                trips.add(lastTrip);
            }
        }
        System.out.println("");
    }
}