package com.example.diploma.dto;

import com.example.diploma.model.User;

public class UserDto {
    private Integer id;
    private String name;

    public UserDto() {
    }

    public UserDto(User user){
        id = user.getId();
        name = user.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
