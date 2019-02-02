package ke.co.apollo.travelportal;

import lombok.extern.log4j.Log4j2;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class TravelPortalApplicationTests {

	@Autowired
	private HistoryService historyService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private EmailService emailService;

	private static String CUSTOMER_ID_PV = "customerId";
	private static String EMAIL_PV = "email";

	@Test
	public void contextLoads() throws Exception{
		String customerId = "1";
		String email = "email@email.com";
		String processInstanceId = beginCustomerEnrollmentProcess(customerId, email);
		log.info("process instance id: " + processInstanceId);
		Assert.assertNotNull("the process instance ID should not be null",processInstanceId);

		// get outstanding tasks

		List<Task> tasks = this.taskService
						.createTaskQuery()
						.active()
						.taskName("confirm-email-task")
						.includeProcessVariables()
						.processVariableValueEquals(CUSTOMER_ID_PV, customerId)
						.includeProcessVariables()
						.list();
		Assert.assertTrue("there should be one outstanding", tasks.size()>=1);

		// complete outstanding tasks
		tasks.forEach( task -> {
							this.taskService.claim(task.getId(), "akipkoech");
							this.taskService.complete(task.getId());
						}
		);

		// confirm that the email has been sent
		Assert.assertEquals(this.emailService.sends.get(email).get(),1);

		List<HistoricProcessInstance> histSvc = this.historyService
						.createHistoricProcessInstanceQuery()
						.includeProcessVariables()
						.variableValueEquals("customerId", "1")
						.list();

		Assert.assertTrue("there should be one history service", histSvc.size()>=1);

		/*this.confirmEmail(customerId );*/
	}

	String beginCustomerEnrollmentProcess(String customerId, String email){
		Map<String,Object> vars = new HashMap<>();
		vars.put(CUSTOMER_ID_PV,customerId);
		vars.put(EMAIL_PV,email);
		ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey("signup-process",vars);
		return processInstance.getId();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void enrollNewUser() throws Exception{

	}

	void confirmEmail(String customerId) {

	}

}