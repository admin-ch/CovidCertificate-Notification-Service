package ch.admin.bag.covidcertificate.api.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class RestError implements Serializable {
    private int errorCode;
    private String errorMessage;
    @JsonIgnore
    private HttpStatus httpStatus;

    public static RestError restValidationError(String errorMessage) {
        return new RestError(551, "Validation error: ".concat(errorMessage), HttpStatus.BAD_REQUEST);
    }

}
