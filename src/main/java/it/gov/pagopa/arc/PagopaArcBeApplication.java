package it.gov.pagopa.arc;

import it.gov.pagopa.arc.utils.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class PagopaArcBeApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(Constants.DEFAULT_TIMEZONE);
        SpringApplication.run(PagopaArcBeApplication.class, args);
	}

}
