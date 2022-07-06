package com.alkemy.ong.utility;

public abstract class GlobalConstants {

    //  Default role names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // Send email
    public static final String TEMPLATE_CONTACT = "CONTACT";
    public static final String TEMPLATE_WELCOME = "WELCOME";
    public static final String TITLE_EMAIL_WELCOME = "Welcome! Thank you for registering";
    public static final String TITLE_EMAIL_CONTACT = "Thank you for contacting us! We will be answering your query shortly";

    public static abstract class Endpoints {
        public static final String ORGANIZATION_PUBLIC_INFO = "/organization/public";
        public static final String USER = "/users/";
        public static final String CATEGORIES = "/categories";
        public static final String CLOUD_STORAGE = "/cloud-storage";
        public static final String NEWS = "/news";
        public static final String CONTACT = "/contacts";
        public static final String ACTIVITIES = "/activities";
        public static final String SLIDES = "/slides";

    }

    public static abstract class EndpointsRoutes{
        public static final String LOGIN = "/auth/login";
        public static final String REGISTER = "/auth/register";
    }

}


