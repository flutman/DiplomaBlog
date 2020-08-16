package main.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime time;
    @Column(columnDefinition = "tinytext not null")
    private String code;
    @Column(name = "secret_code", columnDefinition = "tinytext not null")
    private String secretCode;

}
