package sql;

import src.helpers.blueprint.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City extends BaseModel {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false, unique = true)
    public Long stateId;

}
