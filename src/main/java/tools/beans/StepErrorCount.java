package tools.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StepErrorCount implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9042303447893525990L;

    String activity;

    Map<Integer, Integer> errorCount = new HashMap<>();

    public int getErrorCount(int build) {
        return Optional.ofNullable(errorCount.get(build)).orElse(-1);
    }

    public void setErrorCount(int build, int errorCount) {
        this.errorCount.put(build, errorCount);
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activity == null) ? 0 : activity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StepErrorCount other = (StepErrorCount) obj;
        if (activity == null) {
            if (other.activity != null) return false;
        } else if (!activity.equals(other.activity)) return false;
        return true;
    }

}
