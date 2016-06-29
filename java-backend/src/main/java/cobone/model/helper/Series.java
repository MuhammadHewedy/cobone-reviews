package cobone.model.helper;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Series {
	private String name;
	private List<Long> data;
}
