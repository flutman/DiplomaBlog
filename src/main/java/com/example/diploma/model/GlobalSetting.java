package com.example.diploma.model;

import com.example.diploma.enums.GlobalSettings;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
public class GlobalSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) not null")
    private GlobalSettings.Code code;
    @Column(columnDefinition = "varchar(255) not null")
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) not null")
    private GlobalSettings.Value value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GlobalSettings.Code getCode() {
        return code;
    }

    public void setCode(GlobalSettings.Code code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GlobalSettings.Value getValue() {
        return value;
    }

    public void setValue(GlobalSettings.Value value) {
        this.value = value;
    }
}
