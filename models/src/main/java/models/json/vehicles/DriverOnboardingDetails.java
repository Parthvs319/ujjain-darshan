package models.json.vehicles;

import lombok.Data;
import models.json.bank.BankDetails;

@Data
public class DriverOnboardingDetails {

    private DriverDetails driverDetails;
    private DriverKycDetails kycDetails;
    private DrivingLicenseDetails licenseDetails;

    private BankDetails bankDetails;
    private SafetyDetails safetyDetails;

    private String platform;
}