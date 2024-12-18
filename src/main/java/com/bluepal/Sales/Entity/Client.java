package com.bluepal.Sales.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
	
	   @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
	   
	   @NotBlank(message = "Client name is mandatory")
	   @Size(max = 100)
	    private String firstName;
	   
	   @Size(max = 100)
	    private String middleName;
	   
	   @Size(max = 100)
	    private String lastName;
	   
	   @Size(max = 100)
	   private String address;
	   
	   @NotBlank
	   @Pattern(regexp = "active|inactive", message = "Status must be either 'active' or 'inactive'")
	   private String status;

	   @Min(value = 1, message = "Rating must be at least 1")
	   @Max(value = 10, message = "Rating must be at most 10")
	    private String rating;

	    @NotBlank(message = "Client email is mandatory")
	    @Email
	    private String email1;
	    
	    @Email
	    private String email2;

	    @NotBlank
	    @Size(max = 10)
	    private String mobileNo1;
	    
	    @Size(max = 10)
	    private String mobileNo2;

}
