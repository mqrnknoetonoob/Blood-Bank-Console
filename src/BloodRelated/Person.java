package BloodRelated;

import java.io.Serializable;

public class Person implements Serializable {
    private String name;
    private String NID;
    private String bloodGroup;

    public Person(String name, String nid, String bloodGroup) {
        this.name = name;
        this.NID = nid;
        this.bloodGroup = bloodGroup;
    }

    public String getName() {
        return name;
    }

    public String getNID() {
        return NID;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }
}