package com.alkemy.ong.utility;

public abstract class GlobalConstants {

    //  Default role names
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String[] ALL_ROLES = {"ROLE_USER", "ROLE_ADMIN"};

    // Send email
    public static final String TEMPLATE_CONTACT = "CONTACT";
    public static final String TEMPLATE_WELCOME = "WELCOME";
    public static final String TITLE_EMAIL_WELCOME = "Welcome! Thank you for registering";
    public static final String TITLE_EMAIL_CONTACT = "Thank you for contacting us! We will be answering your query shortly";

    // Pagination
    public static final int GLOBAL_PAGE_SIZE = 10;
    public static final String PAGE_INDEX_PARAM = "page";
    public static final String CATEGORY_SORT_ATTRIBUTE = "name";
    public static final String NEWS_SORT_ATTRIBUTE = "timestamp";
    public static final String MEMBERS_SORT_ATTRIBUTE = "name";

    public static final String TESTIMONIAL_SORT_ATTRIBUTE = "name";

    public static abstract class Endpoints {
        public static final String LOGIN = "/auth/login";
        public static final String REGISTER = "/auth/register";
        public static final String AUTH_ME = "/auth/me";
        public static final String ORGANIZATION_PUBLIC_INFO = "/organization/public";
        public static final String USER = "/users/";
        public static final String CATEGORIES = "/categories";
        public static final String CLOUD_STORAGE = "/cloud-storage";
        public static final String NEWS = "/news";
        public static final String CONTACT = "/contacts";
        public static final String ACTIVITIES = "/activities";
        public static final String SLIDES = "/slides";
        public static final String TESTIMONIALS="/testimonials";
        public static final String MEMBERS= "/members";
        public static final String COMMENTS = "/comments";

    }

    public static abstract class EndpointsRoutes{
        public static final String[] USER_GET = {"/auth/me", "/organization/public", "/post/{id}/comments", "/news/list", Endpoints.CATEGORIES, Endpoints.NEWS, Endpoints.MEMBERS, Endpoints.TESTIMONIALS};
        public static final String[] USER_POST = {"/comments", "/contacts", "/members"};
        public static final String[] USER_PUT = {"/comments/{id}", "/members/{id}", "/users/{id}"};
        public static final String[] USER_DELETE = {"/comments/{id}", "/users/{id}"};

    }

}


