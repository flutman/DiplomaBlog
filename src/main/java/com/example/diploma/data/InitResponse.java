package com.example.diploma.data;

public class InitResponse {
    private final String title = "DevPub";
    private final String subtitle = "Рассказы разработчиков";
    private final String phone = "+7 903 666-44-55";
    private final String email = "mail@mail.ru";
    private final String copyright = "Олег Белеменко";
    private final String copyrightFrom = "2005";

    public InitResponse() {
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getCopyrightFrom() {
        return copyrightFrom;
    }
}
