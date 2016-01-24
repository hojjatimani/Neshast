package bef.rest.neshast;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hojjatimani on 1/20/2016 AD.
 */

public class POJOs {
    class RegisterResponse {
        @SerializedName("errorCode")
        int errorCode;
        @SerializedName("message")
        String message;

        @SerializedName("entity")
        Entity entity;
    }

    class UpdateResponse {
        @SerializedName("erroCode")
        int errorCode;
    }

    class AskResponse {
        @SerializedName("errorCode")
        int errorCode;
        @SerializedName("message")
        String message;

        @SerializedName("entity")
        Entity entity;
    }

    class Entity {
        @SerializedName("uid")
        long uid;
    }
}
