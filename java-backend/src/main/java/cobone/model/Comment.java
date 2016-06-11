package cobone.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, exclude = { "deal" })
@ToString(exclude = { "deal" })
public class Comment implements Serializable {

	private static final long serialVersionUID = 8190096715963403303L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_gen")
	@SequenceGenerator(name = "comment_seq_gen", sequenceName = "comment_id_seq")
	protected Long id;

	@NotNull
	@Column(nullable = false)
	private String name;

	private String email;

	@NotNull
	@Column(nullable = false)
	private String content;

	private Date created = new Date();

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "DEAL_ID", nullable = false)
	private Deal deal;

}
