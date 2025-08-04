package BloodRelated;

import java.time.LocalDate;
import java.io.Serializable;

public class BloodUnit implements Serializable{
    private String donorName;
    private String donorNID;
    private String bloodGroup;
    private LocalDate donationDate;
    private LocalDate expiryDate;

    public BloodUnit(String donorName, String donorNID, String bloodGroup, LocalDate donationDate, LocalDate expiryDate) {
        this.donorName = donorName;
        this.donorNID = donorNID;
        this.bloodGroup = bloodGroup;
        this.donationDate = donationDate;
        this.expiryDate = expiryDate;
    }

    public String getDonorName() { return donorName; }
    public String getDonorNID() { return donorNID; }
    public String getBloodGroup() { return bloodGroup; }
    public LocalDate getDonationDate() { return donationDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getName() {
        return donorName;
    }

    public void showInfo() {
        System.out.println("Donor: " + donorName + ", NID: " + donorNID + ", Group: " + bloodGroup +
                ", Donated: " + donationDate + ", Expires: " + expiryDate);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return donorName + " (" + bloodGroup + "), Expires: " + expiryDate;
    }
}
