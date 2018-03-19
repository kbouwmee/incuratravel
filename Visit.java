import java.util.Date;

public class Visit {
    
    private Date date;
    private Therapist therapist;
    private Client client;
    private Boolean atHome;

    public Visit(Date d, Therapist t, Client c, Boolean a) {
        date = d;
        therapist = t;
        client = c;
        atHome = a;
    }

    public Date getDate() {
        return date;
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public Client getClient() {
        return client;
    }

    public Boolean getAtHome() {
        return atHome;
    }

    public void setAtHome(Boolean b) {
        this.atHome = b;
    }

    public String toJSON() {
        return "{ 'date': '"+date.toString() + "'," +
                "'therapist': '"+therapist.getName() + "'," +
                "'client': '"+client.getName() + "'," +
                "'atHome': '"+atHome.toString() + "'}";
    }

    public Boolean equals(Visit v) {
        if(this.client.getId() == v.getClient().getId() 
        && this.date.equals(v.getDate())
        && this.therapist.getName().equals(v.getTherapist().getName())) 
        {
                    return true;
                }
                else {
                    return false;
                }
    }
}