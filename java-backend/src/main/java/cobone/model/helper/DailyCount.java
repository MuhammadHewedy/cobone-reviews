package cobone.model.helper;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyCount {
	private Date day;
	private Object action;
	private Long value;
}
