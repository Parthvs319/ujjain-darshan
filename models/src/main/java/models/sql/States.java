package models.sql;

import helpers.blueprint.BaseModel;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "states")
public class States extends BaseModel {

    @Column(nullable = false, unique = true)
    public String name;

    @Column(nullable = false, unique = false)
    public Long countryId;

}
