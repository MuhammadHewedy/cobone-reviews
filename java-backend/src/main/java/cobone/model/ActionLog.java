package cobone.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class ActionLog {

	@Id
	@GeneratedValue
	protected Long id;

	private String path;
	@Enumerated(EnumType.STRING)
	private Action action;
	private Date created = new Date();
	private String referrer;

	public enum Action {
		PAGE_LOAD, PAGE_SCROLL, WATCH_CLICK, DOWNLOAD_CLICK, CONTACT_CLICK, STORE_BUTTON_CLICK
	}
}
