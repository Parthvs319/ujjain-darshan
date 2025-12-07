package models.json.vehicles;

import lombok.Data;

@Data
public class DriverDetails {

    private String fullName;
    private String phone;
    private String email;
    private String gender;
    private String dob;
    private String profilePhotoUrl;
    private String referralCode;
    private String emergencyContact;
}
