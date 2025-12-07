package models.json.vehicles;

import lombok.Data;

@Data
public class SafetyDetails {

    private boolean criminalBackgroundCheckPassed;
    private boolean previousPlatformBan;
    private boolean safetyTrainingCompleted;
}
