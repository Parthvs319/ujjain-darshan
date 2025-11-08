package helpers.blueprint;


import io.ebean.annotation.DbJsonB;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;
import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class AttrsModel extends BaseModel {

    @DbJsonB
    private HashMap<String,String> attrs = new HashMap<>();

    public HashMap<String,String> getAttrs(){
        if(attrs==null)
            attrs = new HashMap<>();
        return attrs;
    }
}

