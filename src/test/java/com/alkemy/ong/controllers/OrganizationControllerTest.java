package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.OrganizationDTORequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;

class OrganizationControllerTest {

    @Nested
    class getAllTest {

        @Test
        @DisplayName("Valid case")
        void test1() {

        }

        @Test
        @DisplayName("No token provided")
        void test2() {

        }

        @Test
        @DisplayName("Token not valid")
        void test3() {

        }

    }

    @Nested
    class getByIdTest {

        @Test
        @DisplayName("Valid case")
        void test1() {

        }

        @Test
        @DisplayName("No token provided")
        void test2() {

        }

        @Test
        @DisplayName("Token not valid")
        void test3() {

        }

        @Test
        @DisplayName("Valid token but no role admin")
        void test4() {

        }

        @Test
        @DisplayName("Non-existing ID")
        void test5() {

        }

    }

    @Nested
    class updateOrganizationTest {

        @Test
        @DisplayName("Valid case")
        void test1() {

        }

        @Test
        @DisplayName("No token provided")
        void test2() {

        }

        @Test
        @DisplayName("Token not valid")
        void test3() {

        }

        @Test
        @DisplayName("Valid token but no role admin")
        void test4() {

        }

        @Test
        @DisplayName("Non-existing ID")
        void test5() {

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithMissingMandatoryAttributes")
        @DisplayName("Mandatory attributes missing")
        void test6(OrganizationDTORequest requestWithMissingAttribute) {

        }

        @ParameterizedTest
        @MethodSource("com.alkemy.ong.controllers.OrganizationControllerTest#generateRequestsWithBrokenAttributes")
        @DisplayName("Invalid attribute format")
        void test7(OrganizationDTORequest requestWithBrokenAttribute) {

        }

    }

    static List<OrganizationDTORequest> generateRequestsWithMissingMandatoryAttributes() {
        return Collections.singletonList(new OrganizationDTORequest());
    }

    static List<OrganizationDTORequest> generateRequestsWithBrokenAttributes() {
        return Collections.singletonList(new OrganizationDTORequest());
    }

}