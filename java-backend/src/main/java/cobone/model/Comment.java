package cobone.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
@Entity
@EqualsAndHashCode(callSuper = false, exclude = { "deal" })
@ToString(exclude = { "deal" })
public class Comment implements Serializable {

	private static final long serialVersionUID = 8190096715963403303L;

	// -- Simple fields

	@Id
	@GeneratedValue
	protected Long id;

	@NotBlank
	@Column(nullable = false)
	private String name;

	private String email;

	@NotBlank
	@Length(min = 20)
	@Column(nullable = false)
	private String content;

	private String url;

	@JsonIgnore
	private String uuid;

	private Date created = new Date();

	@Getter(AccessLevel.NONE)
	private Boolean active = Boolean.TRUE;

	public Boolean isActive() {
		return active;
	}

	// -- Associations

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "DEAL_ID", nullable = false)
	private Deal deal;

	// -- Transient fields

	@NotBlank
	@Transient
	@JsonProperty(access = Access.WRITE_ONLY)
	private String captcha;

	@Transient
	private boolean canDelete;
}
