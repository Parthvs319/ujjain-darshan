package helpers.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {

    private boolean success = true;

    private String message;

    public SuccessResponse(boolean success){
        this.success = success;
    }

    public static SuccessResponse generate(){
        return new SuccessResponse();
    }

}

