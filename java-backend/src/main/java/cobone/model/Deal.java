package cobone.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, exclude = { "comments" })
@ToString(exclude = { "comments" })
public class Deal implements Serializable {

	private static final long serialVersionUID = -3214355870059327919L;

	@Id
	@GeneratedValue
	protected Long id;

	@NotNull
	@Column(unique = true, nullable = false)
	private String path;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "deal")
	private List<Comment> comments;
}
