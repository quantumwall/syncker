package org.quantum.synchronizer.entity;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "depCode", "depJob", "description" })
public class Staff {

    private Long id;
    private String depCode;
    private String depJob;
    private String description;

    public Staff() {

    }

    public Staff(String depCode, String depJob, String description) {
        this.depCode = depCode;
        this.depJob = depJob;
        this.description = description;
    }

    @XmlTransient
    public Long getId() {
        return id;
    }

    public String getDepCode() {
        return depCode;
    }

    public String getDepJob() {
        return depJob;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public void setDepJob(String depJob) {
        this.depJob = depJob;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Staff [id=" + id + ", depCode=" + depCode + ", depJob=" + depJob + ", description=" + description + "]";
    }

}
