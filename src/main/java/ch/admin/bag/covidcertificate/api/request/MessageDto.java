package ch.admin.bag.covidcertificate.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageDto {
    @NotEmpty(message = DE_MUST_NOT_BE_EMPTY)
    private String de;
    @NotEmpty(message = FR_MUST_NOT_BE_EMPTY)
    private String fr;
    @NotEmpty(message = IT_MUST_NOT_BE_EMPTY)
    private String it;
    @NotEmpty(message = EN_MUST_NOT_BE_EMPTY)
    private String en;
}
