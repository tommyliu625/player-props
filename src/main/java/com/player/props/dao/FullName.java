package com.player.props.dao;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FullName implements Serializable{
  
    @Column(name = "first_name", nullable = false)
    public String firstName;

    @Column(name = "last_name", nullable = false)
    public String lastName;

    @Override
    public String toString(){
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(!FullName.class.isAssignableFrom(obj.getClass())){
            return false;
        }
        final FullName other = (FullName) obj;
        if (this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
