package sql;


import src.helpers.blueprint.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "countries")
public class Countries extends BaseModel {

    @Column(nullable = false, unique = true)
    public String name;
}
