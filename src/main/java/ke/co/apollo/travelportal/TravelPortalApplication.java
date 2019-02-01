package ke.co.apollo.travelportal;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TravelPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelPortalApplication.class, args);
	}

}

@Component
class ProcessDemo{

	private final RuntimeService runtimeService;
	private final TaskService taskService;

	public ProcessDemo(RuntimeService runtimeService, TaskService taskService) {
		this.runtimeService = runtimeService;
		this.taskService = taskService;
	}

	void beginCustomerEnrollment(String customerId, String email);

	@EventListener (ApplicationReadyEvent.class)
	public void enrollNewUser() throws Exception{

	}

}
