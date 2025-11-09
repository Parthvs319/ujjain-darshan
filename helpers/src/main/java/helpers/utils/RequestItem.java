package helpers.utils;

import helpers.blueprint.enums.RequestItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestItem {

    private String key;
    @Builder.Default
    private RequestItemType itemType=RequestItemType.STRING;
    @Builder.Default
    private boolean required=true;
    private String error;
    private Predicate<Object> predicate;
    private Class objectClass;
    @Builder.Default
    private boolean negativeAllowed = false;

}



