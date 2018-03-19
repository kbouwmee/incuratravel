import java.util.List;

public class VisitCleaner {
    
    private VisitImporter vi;
    private List<Visit> visits;

    public VisitCleaner(VisitImporter vi) {
        this.vi = vi;
    }

    /* Remove all double visits and determine if it is a 
       home visit or meeting at the practice.
    */
    public void clean() 
    {
        String test = "test";
    }
}