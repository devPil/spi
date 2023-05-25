package io.pilju.spi.connectdb;

public class DatabaseQueryConstants {
    public static final String getUserList = "select username, password, email, firstname, lastname, birthDate from users where username = ?";

}
