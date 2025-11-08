package sql;


import src.helpers.blueprint.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "states")
public class States extends BaseModel {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false, unique = false)
    public Long countryId;

}
