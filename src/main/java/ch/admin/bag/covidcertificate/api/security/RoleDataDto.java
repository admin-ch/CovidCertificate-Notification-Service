package ch.admin.bag.covidcertificate.api.security;

import lombok.Data;

@Data
public class RoleDataDto {
    private final String intern;
    private final String eiam;
    private final String claim;
}
