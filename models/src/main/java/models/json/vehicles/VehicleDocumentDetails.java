package models.json.vehicles;


import lombok.Data;

@Data
public class VehicleDocumentDetails {

    private String rcNumber;
    private String rcIssueDate;
    private String rcExpiryDate;
    private String rcImageUrl;

    private String insuranceNumber;
    private String insuranceProvider;
    private String insuranceIssueDate;
    private String insuranceExpiryDate;
    private String insuranceImageUrl;

    private String pucNumber;
    private String pucIssueDate;
    private String pucExpiryDate;
    private String pucImageUrl;

    private String permitNumber;
    private String permitIssueDate;
    private String permitExpiryDate;
    private String permitImageUrl;

    private String fitnessCertificateNumber;
    private String fitnessIssueDate;
    private String fitnessExpiryDate;
    private String fitnessImageUrl;
}