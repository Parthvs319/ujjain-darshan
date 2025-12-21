package models.services;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import models.json.pujari.PujariDetails;
import models.json.tourist.TripConfig;
import models.json.vehicles.VehicleDetails;
import models.sql.*;
import rx.Single;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * WhatsApp Service for sending notifications via WhatsApp Business API
 * Supports multiple providers (Twilio, WhatsApp Business API, etc.)
 * Configure via environment variables:
 * - WHATSAPP_API_URL: Base URL for WhatsApp API
 * - WHATSAPP_API_KEY: API key/token for authentication
 * - WHATSAPP_PHONE_NUMBER_ID: Phone number ID (for WhatsApp Business API)
 */
public class WhatsAppService {
    
    private static final String WHATSAPP_API_URL = System.getenv().getOrDefault("WHATSAPP_API_URL", "");
    private static final String WHATSAPP_API_KEY = System.getenv().getOrDefault("WHATSAPP_API_KEY", "");
    private static final String WHATSAPP_PHONE_NUMBER_ID = System.getenv().getOrDefault("WHATSAPP_PHONE_NUMBER_ID", "");
    private static final String WHATSAPP_FROM_NUMBER = System.getenv().getOrDefault("WHATSAPP_FROM_NUMBER", "Temple Trails");
    
    private static Vertx vertxInstance;
    
    public static void initialize(Vertx vertx) {
        vertxInstance = vertx;
    }
    
    /**
     * Send WhatsApp message to a phone number
     * Uses Vertx HttpClient if available, otherwise falls back to logging
     */
    private static Single<Boolean> sendWhatsAppMessage(String toPhoneNumber, String message) {
        if (WHATSAPP_API_URL == null || WHATSAPP_API_URL.isEmpty()) {
            System.out.println("WhatsApp API not configured. Message would be sent to: " + toPhoneNumber);
            System.out.println("Message: " + message);
            return Single.just(true); // Return true for development/testing
        }
        
        if (vertxInstance == null) {
            System.err.println("WhatsAppService not initialized. Message would be sent to: " + toPhoneNumber);
            System.out.println("Message: " + message);
            return Single.just(true); // Return true for development/testing
        }
        
        return Single.fromCallable(() -> {
            try {
                String formattedPhone = formatPhoneNumber(toPhoneNumber);

                JsonObject requestBody = buildRequestBody(formattedPhone, message);
                
                URL url = new URL(WHATSAPP_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + WHATSAPP_API_KEY);
                conn.setDoOutput(true);
                
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
                
                int responseCode = conn.getResponseCode();
                boolean success = responseCode >= 200 && responseCode < 300;
                
                if (!success) {
                    System.err.println("WhatsApp API error: " + responseCode + " - " + conn.getResponseMessage());
                }
                
                conn.disconnect();
                return success;
            } catch (Exception e) {
                System.err.println("Error sending WhatsApp message: " + e.getMessage());
                return false;
            }
        });
    }
    
    private static JsonObject buildRequestBody(String toPhone, String message) {
        JsonObject body = new JsonObject();
        
        if (!WHATSAPP_PHONE_NUMBER_ID.isEmpty()) {
            body.put("messaging_product", "whatsapp");
            body.put("to", toPhone);
            body.put("type", "text");
            body.put("text", new JsonObject().put("body", message));
        } else {
            body.put("to", toPhone);
            body.put("message", message);
            body.put("from", WHATSAPP_FROM_NUMBER);
        }
        
        return body;
    }
    
    private static String formatPhoneNumber(String phone) {
        // Remove any non-digit characters
        String digits = phone.replaceAll("[^0-9]", "");
        
        // If it's a 10-digit Indian number, add country code 91
        if (digits.length() == 10) {
            return "91" + digits;
        }
        
        // If it already has country code, return as is
        return digits;
    }
    
    /**
     * Send trip creation confirmation to tourist
     */
    public static void sendTripCreationConfirmation(Trip trip) {
        if (trip.getUser() == null || trip.getUser().getMobile() == null) {
            return;
        }
        
        String message = buildTripCreationMessage(trip);
        sendWhatsAppMessage(trip.getUser().getMobile(), message)
                .subscribe(
                        success -> {
                            if (success) {
                                System.out.println("Trip creation confirmation sent to tourist: " + trip.getUser().getMobile());
                            }
                        },
                        error -> System.err.println("Failed to send trip creation confirmation: " + error.getMessage())
                );
    }
    
    /**
     * Send pujari acceptance notifications
     */
    public static void sendPujariAcceptanceNotifications(TripRequest tripRequest) {
        Trip trip = tripRequest.getTrip();
        Pujari pujari = tripRequest.getPujari();
        
        if (trip == null || pujari == null) {
            return;
        }
        
        // Send to pujari
        if (pujari.getUser() != null && pujari.getUser().getMobile() != null) {
            String pujariMessage = buildPujariAcceptanceMessageForPujari(trip, tripRequest);
            sendWhatsAppMessage(pujari.getUser().getMobile(), pujariMessage)
                    .subscribe(
                            success -> {
                                if (success) {
                                    System.out.println("Pujari acceptance notification sent to pujari: " + pujari.getUser().getMobile());
                                }
                            },
                            error -> System.err.println("Failed to send pujari notification: " + error.getMessage())
                    );
        }
        
        // Send to tourist
        if (trip.getUser() != null && trip.getUser().getMobile() != null) {
            String touristMessage = buildPujariAcceptanceMessageForTourist(trip, pujari, tripRequest);
            sendWhatsAppMessage(trip.getUser().getMobile(), touristMessage)
                    .subscribe(
                            success -> {
                                if (success) {
                                    System.out.println("Pujari acceptance notification sent to tourist: " + trip.getUser().getMobile());
                                }
                            },
                            error -> System.err.println("Failed to send tourist notification: " + error.getMessage())
                    );
        }
    }
    
    /**
     * Send driver acceptance notifications
     */
    public static void sendDriverAcceptanceNotifications(TripRequest tripRequest) {
        Trip trip = tripRequest.getTrip();
        Drivers driver = tripRequest.getDriver();
        
        if (trip == null || driver == null) {
            return;
        }
        
        // Send to driver
        if (driver.getUser() != null && driver.getUser().getMobile() != null) {
            String driverMessage = buildDriverAcceptanceMessageForDriver(trip, driver, tripRequest);
            sendWhatsAppMessage(driver.getUser().getMobile(), driverMessage)
                    .subscribe(
                            success -> {
                                if (success) {
                                    System.out.println("Driver acceptance notification sent to driver: " + driver.getUser().getMobile());
                                }
                            },
                            error -> System.err.println("Failed to send driver notification: " + error.getMessage())
                    );
        }
        
        // Send to tourist
        if (trip.getUser() != null && trip.getUser().getMobile() != null) {
            String touristMessage = buildDriverAcceptanceMessageForTourist(trip, driver, tripRequest);
            sendWhatsAppMessage(trip.getUser().getMobile(), touristMessage)
                    .subscribe(
                            success -> {
                                if (success) {
                                    System.out.println("Driver acceptance notification sent to tourist: " + trip.getUser().getMobile());
                                }
                            },
                            error -> System.err.println("Failed to send tourist notification: " + error.getMessage())
                    );
        }
    }
    
    private static String buildTripCreationMessage(Trip trip) {
        StringBuilder message = new StringBuilder();
        message.append("🎉 *Trip Created Successfully!*\n\n");
        message.append("Hello ").append(trip.getUser().getName() != null ? trip.getUser().getName() : "there").append(",\n\n");
        message.append("Your trip has been created with the following details:\n\n");
        message.append("📋 *Trip ID:* ").append(trip.getTripId()).append("\n");
        message.append("📍 *Destination:* ").append(trip.getCity() != null ? trip.getCity().getName() : "N/A").append("\n");
        message.append("📅 *Start Date:* ").append(formatDate(trip.getStartDate())).append("\n");
        message.append("📅 *End Date:* ").append(formatDate(trip.getEndDate())).append("\n");
        message.append("👥 *Passengers:* ").append(trip.getNumberOfPassengers() != null ? trip.getNumberOfPassengers() : 1).append("\n");
        
        if (trip.getBudget() != null && trip.getBudget() > 0) {
            message.append("💰 *Budget:* ₹").append(trip.getBudget()).append("\n");
        }
        
        TripConfig config = trip.getConfig();
        if (config != null) {
            if (config.getTempleAndPujasMap() != null && !config.getTempleAndPujasMap().isEmpty()) {
                message.append("\n🕉️ *Temples & Pujas:*\n");
                for (Map.Entry<String, java.util.List<String>> entry : config.getTempleAndPujasMap().entrySet()) {
                    message.append("  • ").append(entry.getKey());
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        message.append(" - ").append(String.join(", ", entry.getValue()));
                    }
                    message.append("\n");
                }
            }
            
            if (config.getStayMethod() != null) {
                message.append("\n🏨 *Stay Method:* ").append(config.getStayMethod().getValue()).append("\n");
            }
            
            if (config.getTravelMethod() != null) {
                message.append("🚗 *Travel Method:* ").append(config.getTravelMethod().getValue()).append("\n");
            }
        }
        
        message.append("\n✅ Your trip requests have been sent to available pujaris and drivers. ");
        message.append("You will be notified once they accept.\n\n");
        message.append("Thank you for choosing *Temple Trails*! 🙏");
        
        return message.toString();
    }
    
    private static String buildPujariAcceptanceMessageForPujari(Trip trip, TripRequest tripRequest) {
        StringBuilder message = new StringBuilder();
        message.append("✅ *Trip Request Accepted!*\n\n");
        message.append("Hello ").append(tripRequest.getPujari().getUser().getName() != null ? tripRequest.getPujari().getUser().getName() : "there").append(",\n\n");
        message.append("You have successfully accepted a trip request. Here are the details:\n\n");
        message.append("📋 *Trip ID:* ").append(trip.getTripId()).append("\n");
        message.append("📍 *Location:* ").append(trip.getCity() != null ? trip.getCity().getName() : "N/A").append("\n");
        message.append("📅 *Start Date:* ").append(formatDate(trip.getStartDate())).append("\n");
        message.append("📅 *End Date:* ").append(formatDate(trip.getEndDate())).append("\n");
        
        if (tripRequest.getTempleName() != null) {
            message.append("🕉️ *Temple:* ").append(tripRequest.getTempleName()).append("\n");
        }
        
        if (tripRequest.getPujaNames() != null && !tripRequest.getPujaNames().isEmpty()) {
            message.append("📿 *Pujas:* ").append(String.join(", ", tripRequest.getPujaNames())).append("\n");
        }
        
        if (trip.getUser() != null) {
            message.append("\n👤 *Tourist Details:*\n");
            message.append("  Name: ").append(trip.getUser().getName() != null ? trip.getUser().getName() : "N/A").append("\n");
            message.append("  Mobile: ").append(trip.getUser().getMobile() != null ? trip.getUser().getMobile() : "N/A").append("\n");
        }
        
        message.append("\nPlease prepare for the trip accordingly. Thank you! 🙏");
        
        return message.toString();
    }
    
    private static String buildPujariAcceptanceMessageForTourist(Trip trip, Pujari pujari, TripRequest tripRequest) {
        StringBuilder message = new StringBuilder();
        message.append("✅ *Pujari Assigned!*\n\n");
        message.append("Hello ").append(trip.getUser().getName() != null ? trip.getUser().getName() : "there").append(",\n\n");
        message.append("Great news! A pujari has accepted your trip request.\n\n");
        message.append("📋 *Trip ID:* ").append(trip.getTripId()).append("\n");
        
        if (tripRequest.getTempleName() != null) {
            message.append("🕉️ *Temple:* ").append(tripRequest.getTempleName()).append("\n");
        }
        
        if (tripRequest.getPujaNames() != null && !tripRequest.getPujaNames().isEmpty()) {
            message.append("📿 *Pujas:* ").append(String.join(", ", tripRequest.getPujaNames())).append("\n");
        }
        
        message.append("\n👤 *Pujari Details:*\n");
        if (pujari.getUser() != null) {
            message.append("  Name: ").append(pujari.getUser().getName() != null ? pujari.getUser().getName() : "N/A").append("\n");
            message.append("  Mobile: ").append(pujari.getUser().getMobile() != null ? pujari.getUser().getMobile() : "N/A").append("\n");
        }
        
        PujariDetails details = pujari.getDetails();
        if (details != null) {
            if (details.getLanguages() != null && !details.getLanguages().isEmpty()) {
                message.append("  Languages: ").append(String.join(", ", details.getLanguages())).append("\n");
            }
            if (details.getSkills() != null && !details.getSkills().isEmpty()) {
                message.append("  Skills: ").append(String.join(", ", details.getSkills())).append("\n");
            }
        }
        
        message.append("\nYou can contact the pujari directly for any queries. Thank you! 🙏");
        
        return message.toString();
    }
    
    private static String buildDriverAcceptanceMessageForDriver(Trip trip, Drivers driver, TripRequest tripRequest) {
        StringBuilder message = new StringBuilder();
        message.append("✅ *Trip Request Accepted!*\n\n");
        message.append("Hello ").append(driver.getUser().getName() != null ? driver.getUser().getName() : "there").append(",\n\n");
        message.append("You have successfully accepted a trip request. Here are the details:\n\n");
        message.append("📋 *Trip ID:* ").append(trip.getTripId()).append("\n");
        message.append("📍 *Location:* ").append(trip.getCity() != null ? trip.getCity().getName() : "N/A").append("\n");
        message.append("📅 *Start Date:* ").append(formatDate(trip.getStartDate())).append("\n");
        message.append("📅 *End Date:* ").append(formatDate(trip.getEndDate())).append("\n");
        message.append("👥 *Passengers:* ").append(trip.getNumberOfPassengers() != null ? trip.getNumberOfPassengers() : 1).append("\n");
        
        if (tripRequest.getVehicle() != null) {
            message.append("🚗 *Vehicle:* ").append(tripRequest.getVehicle().getNumber() != null ? tripRequest.getVehicle().getNumber() : "N/A").append("\n");
        }
        
        if (trip.getUser() != null) {
            message.append("\n👤 *Tourist Details:*\n");
            message.append("  Name: ").append(trip.getUser().getName() != null ? trip.getUser().getName() : "N/A").append("\n");
            message.append("  Mobile: ").append(trip.getUser().getMobile() != null ? trip.getUser().getMobile() : "N/A").append("\n");
        }
        
        TripConfig config = trip.getConfig();
        if (config != null && config.getOnboardingLocation() != null) {
            message.append("  Pickup Location: ").append(config.getOnboardingLocation()).append("\n");
        }
        
        message.append("\nPlease prepare for the trip accordingly. Thank you! 🙏");
        
        return message.toString();
    }
    
    private static String buildDriverAcceptanceMessageForTourist(Trip trip, Drivers driver, TripRequest tripRequest) {
        StringBuilder message = new StringBuilder();
        message.append("✅ *Driver Assigned!*\n\n");
        message.append("Hello ").append(trip.getUser().getName() != null ? trip.getUser().getName() : "there").append(",\n\n");
        message.append("Great news! A driver has accepted your trip request.\n\n");
        message.append("📋 *Trip ID:* ").append(trip.getTripId()).append("\n");
        
        message.append("\n👤 *Driver Details:*\n");
        if (driver.getUser() != null) {
            message.append("  Name: ").append(driver.getUser().getName() != null ? driver.getUser().getName() : "N/A").append("\n");
            message.append("  Mobile: ").append(driver.getUser().getMobile() != null ? driver.getUser().getMobile() : "N/A").append("\n");
        }
        
        if (tripRequest.getVehicle() != null) {
            message.append("  Vehicle Number: ").append(tripRequest.getVehicle().getNumber() != null ? tripRequest.getVehicle().getNumber() : "N/A").append("\n");
            VehicleDetails vehicleDetails = tripRequest.getVehicle().getDetails();
            if (vehicleDetails != null) {
                if (vehicleDetails.getVehicleType() != null) {
                    message.append("  Vehicle Type: ").append(vehicleDetails.getVehicleType()).append("\n");
                }
                if (vehicleDetails.getSeatingCapacity() != null) {
                    message.append("  Seating Capacity: ").append(vehicleDetails.getSeatingCapacity()).append("\n");
                }
            }
        }
        
        TripConfig config = trip.getConfig();
        if (config != null && config.getOnboardingLocation() != null) {
            message.append("\n📍 *Pickup Location:* ").append(config.getOnboardingLocation()).append("\n");
        }
        
        message.append("\nYou can contact the driver directly for any queries. Thank you! 🙏");
        
        return message.toString();
    }
    
    private static String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        return sdf.format(new Date(timestamp.getTime()));
    }
}

