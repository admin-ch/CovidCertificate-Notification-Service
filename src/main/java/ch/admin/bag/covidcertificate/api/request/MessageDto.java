package ch.admin.bag.covidcertificate.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageDto {
    @NotEmpty
    private String de;
    @NotEmpty
    private String fr;
    @NotEmpty
    private String it;
    @NotEmpty
    private String en;
}
