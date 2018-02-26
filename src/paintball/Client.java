package paintball;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clients")
public class Client {
    
     private static final long serialVersionUID = -300025L;

    @Id
    @GeneratedValue
    @Column(name= "id", unique = true)
    private Integer id;
    
    @Column(name = "pesel")
    private long pesel;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    public Client() {
    }

    public Client(Integer id, long pesel, String name, String surname) {
        this.id = id;
        this.pesel = pesel;
        this.name = name;
        this.surname = surname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public long getPesel() {
        return pesel;
    }

    public void setPesel(long pesel) {
        this.pesel = pesel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
