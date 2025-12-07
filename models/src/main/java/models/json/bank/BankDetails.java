package models.json.bank;


import lombok.Data;

@Data
public class BankDetails {
    private String accountHolderName;
    private String accountNumber;
    private String ifsc;
    private String bankName;
    private String upiId;
}
