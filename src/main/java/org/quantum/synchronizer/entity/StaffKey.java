package org.quantum.synchronizer.entity;

import java.util.Objects;

/**
 * StaffKey is used for duplicate staff control during parse from xml file. In
 * database staff table has a unique contraint on fields dep_code and dep_job,
 * so we can't insert two rows with same dep_code and dep_job
 */
public class StaffKey {

    private final String depCode;
    private final String depJob;

    public StaffKey(String depCode, String depJob) {
        this.depCode = depCode;
        this.depJob = depJob;
    }

    public String getDepCode() {
        return depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    @Override
    public int hashCode() {
        return Objects.hash(depCode, depJob);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StaffKey other = (StaffKey) obj;
        return Objects.equals(depCode, other.depCode) && Objects.equals(depJob, other.depJob);
    }

    @Override
    public String toString() {
        return "StaffKey [depCode=" + depCode + ", depJob=" + depJob + "]";
    }

}
