package hacksquad.carcrash;

/**
 * Created by s160439 on 20/2/2016.
 */
public class CrashData {

    private String timestamp;
    private boolean crashed;

    public CrashData(String timestamp, boolean crashed) {
        this.timestamp = timestamp;
        this.crashed = crashed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isCrashed() {
        return crashed;
    }
}

