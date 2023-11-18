package org.quantum.synchronizer.entity;

import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * StaffTable represents root staff_table root element in xml file it
 * incapsulates staff entities
 */
@XmlRootElement
public class StaffTable {

    private Set<Staff> staffList;

    @XmlElement(name = "staff")
    public Set<Staff> getStaffList() {
        return staffList;
    }

    public void setStaffList(Set<Staff> staffList) {
        this.staffList = staffList;
    }

}
