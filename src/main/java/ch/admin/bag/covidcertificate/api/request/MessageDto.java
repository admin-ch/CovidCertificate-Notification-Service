package ch.admin.bag.covidcertificate.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageDto {
   private String de;
   private String fr;
   private String it;
   private String en;
}
