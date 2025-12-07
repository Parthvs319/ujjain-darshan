package models.json.vehicles;

import lombok.Data;

@Data
public class DriverKycDetails {

    private String aadhaarNumber;
    private String aadhaarImageUrl;

    private String panNumber;
    private String panImageUrl;

    private String addressProofType;       // Aadhaar / Passport / Voter ID etc.
    private String addressProofImageUrl;

    private boolean policeVerificationDone;
    private String policeVerificationCertificateUrl;
}