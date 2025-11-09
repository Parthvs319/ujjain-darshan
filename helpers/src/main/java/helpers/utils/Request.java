package helpers.utils;

import java.util.HashMap;

public class Request {

    private HashMap<String,Object> requestItem;

    public Request(){
        requestItem = new HashMap<>();
    }

    public HashMap<String, Object> getRequestItem() {
        if(requestItem==null)
            requestItem = new HashMap<>();
        return requestItem;
    }

    public void put(String key, Object o){
        getRequestItem().put(key,o);
    }

    public <T>T get(String key){
        return (T) getRequestItem().get(key);
    }

    public <T>T get(String key,T t){
        return (T) getRequestItem().getOrDefault(key,t);
    }
    public <T>T getOrDefault(String key,T t){
        return (T) getRequestItem().getOrDefault(key,t);
    }

    public boolean isPresent(String key){
        return getRequestItem().containsKey(key);
    }

}
