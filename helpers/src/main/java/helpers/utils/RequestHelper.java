package helpers.utils;


import helpers.blueprint.enums.RequestEvent;
import helpers.customErrors.RoutingError;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.http.HttpServerRequest;
import io.vertx.rxjava.ext.web.RoutingContext;


import java.util.HashMap;
import java.util.List;

public enum RequestHelper {

    INSTANCE;

    public HashMap<String, Boolean> annotations(RoutingContext routingContext, String... defaults) {
        List<String> annotations = routingContext.queryParam("annotations");
        HashMap<String, Boolean> hashMap = new HashMap<>();
        if (annotations == null || annotations.size() == 0) {
            for (String aDefault : defaults) {
                hashMap.put(aDefault, true);
            }
        } else {
            for (String annotation : annotations) {
                hashMap.put(annotation, true);
            }
        }
        return hashMap;
    }

    public RequestZipped requestZipped(RoutingContext routingContext, List<RequestItem> items) {
        if (routingContext.request().method().equals(HttpMethod.GET))
            return new RequestZipped(routingContext, mapGetRequest(routingContext, items));
        else
            return new RequestZipped(routingContext, mapJsonRequest(routingContext.getBodyAsJson(), items));
    }

    public Request mapGetRequest(RoutingContext context, List<RequestItem> requestItems) {
        Request jsonRequest = new Request();
        HttpServerRequest request = context.request();
        for (RequestItem requestItem : requestItems) {
            Object item = null;
            switch (requestItem.getItemType()) {
                case DATE:
                    try {
                        item = Long.valueOf(request.getParam(requestItem.getKey()));
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid timestamp", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case EMAIL:
                    String mEmail = request.getParam(requestItem.getKey());
                    if (mEmail == null && requestItem.isRequired()) {
                        throw new RoutingError(String.format("No value provided for key %s.", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    if (mEmail != null && !validateEmailAddress(mEmail)) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid email address", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    item = mEmail;
                    break;
                case INTEGER:
                    try {
                        String itemVal = request.getParam(requestItem.getKey());
                        if (itemVal == null || itemVal.trim().equals("")) {
                            item = null;
                        } else {
                            item = Long.valueOf(itemVal);
                            if (!requestItem.isNegativeAllowed()) {
                                if (((Long) item) < 0L) {
                                    throw new RoutingError("Value can not be less then 0");
                                }
                            }
                        }
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid number", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case DOUBLE:
                    try {
                        item = Double.valueOf(request.getParam(requestItem.getKey()));
                        if (!requestItem.isNegativeAllowed()) {
                            if (((Double) item) < 0L) {
                                throw new RoutingError("Value can not be less then 0");
                            }
                        }
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid double", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case BOOLEAN:
                    try {
                        item = request.getParam(requestItem.getKey()) != null ? Boolean.valueOf(request.getParam(requestItem.getKey())) : null;
                    } catch (Exception e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid boolean", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case STRING:
                default:
                    item = request.getParam(requestItem.getKey());
                    if (item != null && ((String) item).toLowerCase().contains("<script>")) {
                        throw new RoutingError("Invalid characters found in request");
                    }
                    if (item != null && item.toString().length() > 50_000){
                        throw new RuntimeException("Maximum allowed length breached for " + requestItem.getKey());
                    }
                    break;
            }
            if (item != null) {
                if (requestItem.getPredicate() != null) {
                    if (!requestItem.getPredicate().test(item)) {
                        throw new RoutingError(requestItem.getError(), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                }
                if (item != null && item.toString().length() > 1_20_000){
                    throw new RoutingError("Maximum allowed length breached for " + requestItem.getKey(), RequestEvent.PRECONDITIONFAILED, 409);
                }
                jsonRequest.put(requestItem.getKey(), item);
            } else {
                if (requestItem.isRequired()) {
                    if (requestItem.getError() != null) {
                        throw new RoutingError(requestItem.getError(), RequestEvent.PRECONDITIONFAILED, 409);
                    } else
                        throw new RoutingError(String.format("Null value provided for key %s.", requestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                }
            }
        }
        return jsonRequest;
    }

    private boolean validateEmailAddress(String s) {
        return s.matches("^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,6}");
    }


    public Request mapJsonRequest(JsonObject mR, List<RequestItem> requestItems) {
        Request jsonRequest = new Request();
        if (mR == null) {
            throw new RoutingError("Invalid json body", 409);
        }
        for (RequestItem mRequestItem : requestItems) {
            Object item = null;
            String key = mRequestItem.getKey();
            JsonObject request = mR;
            String[] split = key.split("\\.");
            for (int i = 0; i < split.length; i++) {
                key = split[i];
                if (i == split.length - 1) {
                    break;
                } else {
                    if (request.getValue(key) != null) {
                        request = request.getJsonObject(key);
                    } else {
                        if (mRequestItem.isRequired()) {
                            if (mRequestItem.getError() != null) {
                                throw new RoutingError(mRequestItem.getError(), RequestEvent.PRECONDITIONFAILED, 409);
                            } else
                                throw new RoutingError(String.format("Invalid value provided for %s", mRequestItem.getKey().toLowerCase()), RequestEvent.PRECONDITIONFAILED, 409);
                        } else {
                            request = null;
                            break;
                        }
                    }
                }
            }
            if (request == null) {
                continue;
            }
            switch (mRequestItem.getItemType()) {
                case ARRAY:
                    item = request.getJsonArray(key);
                    break;
                case DATE:
                    try {
                        item = request.getLong(key);
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid timestamp", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case EMAIL:
                    String mEmail = request.getString(key);
                    if (mEmail == null && mRequestItem.isRequired()) {
                        throw new RoutingError(String.format("No value provided for key %s.", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    if (mEmail != null && !validateEmailAddress(mEmail)) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid email address", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    item = mEmail;
                    break;
                case BOOLEAN:
                    item = request.getBoolean(key);
                    break;
                case PASSWORD:
                    String mPassword = request.getString(key);
                    if ((mPassword == null || mPassword.length() < 6) && mRequestItem.isRequired()) {
                        throw new RoutingError(String.format("No value provided for key %s.", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    item = mPassword;
                    break;
                case INTEGER:
                    try {
                        item = request.getLong(key);
                        if (item != null && !mRequestItem.isNegativeAllowed()) {
                            if (((Long) item) < 0L) {
                                throw new RoutingError("Value can not be less then 0");
                            }
                        }
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid number", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case DOUBLE:
                    try {
                        item = request.getDouble(key);
                        if (item != null && !mRequestItem.isNegativeAllowed()) {
                            if (((Double) item) < 0L) {
                                throw new RoutingError("Value can not be less then 0");
                            }
                        }
                    } catch (NumberFormatException | ClassCastException e) {
                        throw new RoutingError(String.format("Provided value for key %s is not a valid double", key), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                    break;
                case OBJECT:
                    JsonObject object = request.getJsonObject(key);
                    if (object != null && mRequestItem.getObjectClass() != null) {
                        try {
                            item = Mapper.INSTANCE.getGson().fromJson(object.toString(), mRequestItem.getObjectClass());
                        } catch (Exception e) {
                            throw new RoutingError(e.getMessage(), 409);
                        }
                    }
                    break;
                case JSONOBJECT:
                    item = request.getJsonObject(key);
                    break;
                case STRING:
                default:
                    item = request.getString(key);
                    if (item != null && ((String) item).toLowerCase().contains("<script>")) {
                        throw new RoutingError("Invalid characters found in request");
                    }
                    if (item != null && ((String) item).toLowerCase().contains("\\u0002")) {
                        item = ((String) item).replaceAll("\\u0002", " ");
                    }
                    if (item != null && item.toString().length() > 1_00_000){
                        throw new RuntimeException("Maximum allowed request size breached for " + mRequestItem.getKey());
                    }
                    break;
            }
            if (item != null) {
                if (mRequestItem.getPredicate() != null) {
                    if (!mRequestItem.getPredicate().test(item)) {
                        throw new RoutingError(mRequestItem.getError(), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                }
                if (item != null && item.toString().length() > 10_00_000){
                    throw new RoutingError("Maximum allowed request size breached for " + mRequestItem.getKey(), RequestEvent.PRECONDITIONFAILED, 409);
                }
                jsonRequest.put(mRequestItem.getKey(), item);
            } else {
                if (mRequestItem.isRequired()) {
                    if (mRequestItem.getError() != null) {
                        throw new RoutingError(mRequestItem.getError(), RequestEvent.PRECONDITIONFAILED, 409);
                    } else {
                        throw new RoutingError(String.format("Null value provided for key %s.", mRequestItem.getKey()), RequestEvent.PRECONDITIONFAILED, 409);
                    }
                }
            }
        }
        return jsonRequest;
    }

    // Method to handle multipart form-data for file uploads
    public Request mapMultipartRequest(RoutingContext routingContext, List<RequestItem> items) {
        System.out.println("Inside map multipart request"+ routingContext.request().params());
        System.out.println("Inside map multipart upload"+ routingContext.fileUploads());
        Request formRequest = new Request();

        // Handle form fields
        routingContext.request().params().forEach(entry -> {
            formRequest.put(entry.getKey(), entry.getValue());
            System.out.println("Form Field - Key: " + entry.getKey() + ", Value: " + entry.getValue());

        });

        // Handle file uploads
        routingContext.fileUploads().forEach(file -> {
            System.out.println("File Upload Details:");
            System.out.println("File Name (Original): " + file.fileName());
            System.out.println("Uploaded Temp File Path: " + file.uploadedFileName());
            System.out.println("File Size: " + file.size());

            // Add file path to formRequest using the original file name or other identifier
            formRequest.put(file.fileName(), file.uploadedFileName());
        });
        System.out.println("form request is "+ formRequest);
        return formRequest;
    }

}

