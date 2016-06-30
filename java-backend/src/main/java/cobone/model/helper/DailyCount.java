package cobone.model.helper;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyCount {
	@JsonIgnore
	private Date day;
	private Object action;
	private Long value;
}
