package io.pilju.spi.constants;

public class DatabaseQueryConstants {

    public static final String getUserListByUserName = "SELECT USER_ID AS userId FROM COM.dbo.COM_USER_M WHERE USER_ID = ?";
    public static final String getUserList = "SELECT USER_ID AS userId, COMPANY_CD AS companyCd, USER_NM AS userNm, DEPT_CD AS deptCd, EMAIL_ADDRESS AS emailAddress FROM COM.dbo.COM_USER_M";
    public static final String getPasswordByUserName = "select USER_PW FROM COM.dbo.COM_USER_M WHERE USER_ID = ?";

//    public static final String getUserListByUserName = "select username, email, firstname, lastname, birthDate from users where username = ?";
//    public static final String getUserList = "select username, email, firstname, lastname, birthDate from users";
//    public static final String getUserListByEmail = "select username, email, firstname, lastname, birthDate from users where email = ?";
//    public static final String getPasswordByUserName = "select password from users where username = ?";
//    public static final String removeUserByUserName = "delete from users where username = ?";
//    public static final String addUser = "";

}
