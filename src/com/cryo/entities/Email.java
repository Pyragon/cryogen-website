package com.cryo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Email extends MySQLDao {

    private final String username;
    @MySQLRead
    private String email;

}