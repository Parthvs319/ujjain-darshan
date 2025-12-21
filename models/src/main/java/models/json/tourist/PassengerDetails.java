package models.json.tourist;

import lombok.Data;

@Data
public class PassengerDetails {
    private String name;
    private Integer age;
    private String gender;
    private String mobile;
    private String email;
    private String idProofType; // "Aadhar", "Passport", etc.
    private String idProofNumber;
}
