package models.json.vehicles;


import lombok.Data;

@Data
public class DrivingLicenseDetails {

    private String dlNumber;
    private String dlType;                 // LMV, MCWG, Transport etc.
    private String issueDate;
    private String expiryDate;
    private String dlImageUrl;
}
