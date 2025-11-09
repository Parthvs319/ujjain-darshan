package helpers.utils;


import com.google.gson.Gson;

public enum  Mapper {

    INSTANCE;

    private Gson gson;


    public Gson getGson() {
        if(gson==null){
            gson = new Gson();
        }
        return gson;
    }


}
