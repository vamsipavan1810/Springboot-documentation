package com.tns.empmanagement.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BankHealthIndicator implements HealthIndicator {
	
	@Override
	public Health health() {
		boolean serviceRunning = true;
		if(serviceRunning) {
			return Health.up()
					.withDetail("Application", "Banking Management System")
					.withDetail("Message", "Application is Running")
					.build();
		}
		
		return Health.down()
				.withDetail("Application", "Banking Management System")
				.withDetail("Message", "Application is Down")
				.build();
	}
}


//package com.Tns.BankingManagementSystem.health;


//import org.springframework.boot.health.contributor.Health;
//import org.springframework.boot.health.contributor.HealthIndicator;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BankingHealthIndicator implements HealthIndicator {
//
//    @Override
//    public Health health() {
//
//        boolean serviceRunning = true;
//
//        if (serviceRunning) {
//
//            return Health.up()
//                    .withDetail("Application", "Banking Management System")
//                    .withDetail("Message", "Application is Running")
//                    .build();
//        }
//
//        return Health.down()
//                .withDetail("Application", "Banking Management System")
//                .withDetail("Message", "Application is Down")
//                .build();
//    }
//}