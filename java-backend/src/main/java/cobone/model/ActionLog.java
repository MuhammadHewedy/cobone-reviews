package cobone.model;

import java.util.Date;
import java.util.EnumSet;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Entity
@Slf4j
public class ActionLog {

	@Id
	@GeneratedValue
	protected Long id;

	private String path;
	@Enumerated(EnumType.STRING)
	private Action action;
	private Date created = new Date();
	private String referrer;
	private Long value;

	public enum Action {
		// Order <= 0 means hide from statistics
		PAGE_LOAD("Page Load", 1), 
		PAGE_SCROLL("Page Scroll", 4), 
		WATCH_CLICK("Watch Click", 5), 
		CONTACT_CLICK("Contact Click", -1), 
		STORE_BUTTON_CLICK("Store Button Click", 3),
		TIME_SPENT("Average Time Spent", -1);
		
		String name;
		int order;

		Action(String name, int order) {
			this.name = name;
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public int getOrder() {
			return order;
		}

		public static Action forName(final String name) {
			log.trace("for naming: {}", name);
			return EnumSet.allOf(Action.class).stream().filter(o -> o.name() == name).findFirst().get();
		}
	}
}
