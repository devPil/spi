package io.pilju.spi.connectdb;

public class DatabaseQueryConstants {
    public static final String getUserListByUserName = "select username, email, firstname, lastname, birthDate from users where username = ?";
    public static final String getUserList = "select username, email, firstname, lastname, birthDate from users";
    public static final String getUserListByEmail = "select username, email, firstname, lastname, birthDate from users where email = ?";
    public static final String getPasswordByUserName = "select password from users where username = ?";
    public static final String removeUserByUserName = "delete from users where username = ?";
    public static final String addUser = "";

}
