package models.json.tourist;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;


import java.util.HashMap;
import java.util.List;

@Data
public class CreateTripData {


    @Expose
    private String title;

    @Expose
    private List<City> cities;

    @Expose
    private Origin origin;

    @Expose
    @SerializedName("return")
    private Return mReturn;

    @Expose
    private HashMap<String,String> forms;

}


