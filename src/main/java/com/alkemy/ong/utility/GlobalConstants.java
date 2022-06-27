package com.alkemy.ong.utility;

public abstract class GlobalConstants {

    //  Default role names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static abstract class Endpoints {

        public static final String LOGIN = "/auth/login";
        public static final String REGISTER = "/auth/register";
        public static final String ORGANIZATION_PUBLIC_INFO = "/organization/public";
        public static final String USER = "/users/";



        public static final String CLOUD_STORAGE = "/cloud-storage";

    }

}
