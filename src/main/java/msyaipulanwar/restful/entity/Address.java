package msyaipulanwar.restful.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//CREATE TABLE addresses (
//        id varchar(100) NOT NULL,
//street varchar(255),
//city varchar(100),
//province varchar(100),
//country varchar(100) not null,
//postal_code varchar(10) not null,
//contact_id varchar(100) NOT NULL,
//PRIMARY KEY (id),
//FOREIGN KEY fk_contacts_addresses(contact_id) REFERENCES contacts (id)
//        )
//,

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    private String id;
    private String street;
    private String city;
    private String province;
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    private Contact contact;
}
