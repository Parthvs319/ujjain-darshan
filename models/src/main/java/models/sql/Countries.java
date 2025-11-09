package models.sql;


import helpers.blueprint.models.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "countries")
public class Countries extends BaseModel {

    @Column(nullable = false, unique = true)
    public String name;
}
