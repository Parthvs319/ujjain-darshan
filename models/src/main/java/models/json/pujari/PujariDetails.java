package models.json.pujari;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class PujariDetails {

    private Long dob;

    private String gender;

    private Set<String> languages = new HashSet<>();

    private Set<String> skills = new HashSet<>();

    private String emailAddress;

    private Boolean verified;

    private String workingFrom;

    private String workingTill;

}
