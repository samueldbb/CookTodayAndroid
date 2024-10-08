package org.udg.pds.todoandroid.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.Collection;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id", scope = Recepta.class)
public class ReceptaString {
    public Long id;
    public String nom;
    public String descripcio;
    public String passos;
    public String ingredients;
    public Collection<String> categories;
    public String imageUrl;
    public boolean isChecked = false;
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
