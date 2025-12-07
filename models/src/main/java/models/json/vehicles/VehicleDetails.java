package models.json.vehicles;

import lombok.Data;
import models.enums.FuelType;

@Data
public class VehicleDetails {
    private String vehicleType;
    private String manufacturer;
    private String model;
    private String modelYear;
    private String color;
    private FuelType fuelType;
    private Integer engineCc;
    private Integer seatingCapacity;
    private String registrationNumber;
    private String registrationState;
    private String registrationCity;

    private VehicleDocumentDetails vehicleDocuments;
}