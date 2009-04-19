package org.jboss.seam.wiki.util;

/**
 * Convenience value holder, used mostly for asynchronous background operation status.
 *
 * @author Christian Bauer
 */
public class Progress {

    public static final String COMPLETE = "Complete";

    private int percentComplete;
    private String status;
    private String ofOperation;

    public Progress() {}

    public Progress(String ofOperation) {
        this.ofOperation = ofOperation;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOfOperation() {
        return ofOperation;
    }

    public void setOfOperation(String ofOperation) {
        this.ofOperation = ofOperation;
    }

    public String toString() {
        return "Progress of operation: " + getOfOperation() + ", Status: " + getStatus() + ", Complete: " + getPercentComplete() + "%";
    }

}
