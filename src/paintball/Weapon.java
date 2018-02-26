package paintball;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "weapons")
public class Weapon implements Serializable {
    private static final long serialVersionUID = -300025L;

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true)
    private Integer id;

    @Column(name="brand")
    private String brand;
    
    @Column(name="type")
    private String type;
    
    

    public Weapon() {
    }

    public Weapon(Integer id, String brand, String type) {
        this.id = id;
        this.brand = brand;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Weapon{" + "id=" + id + ", brand=" + brand + ", type=" + type + '}';
    }

    

    
   
}
