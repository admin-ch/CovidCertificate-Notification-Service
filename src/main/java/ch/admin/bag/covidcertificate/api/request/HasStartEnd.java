package ch.admin.bag.covidcertificate.api.request;

import java.time.LocalDateTime;

public interface HasStartEnd {
    LocalDateTime getStartTime();

    void setStartTime(LocalDateTime localDateTime);

    LocalDateTime getEndTime();

    void setEndTime(LocalDateTime localDateTime);
}
