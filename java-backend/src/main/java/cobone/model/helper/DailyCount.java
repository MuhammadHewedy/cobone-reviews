package cobone.model.helper;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyCount {
	private Date day;
	private Object name; // categories.name
	private Long value; // element in categories.data array
}
