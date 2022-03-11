package sn.free.selfcare.web.rest.errors;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI LINE_ALREADY_ATTRIBUTED = URI.create(PROBLEM_BASE_URL + "/ligne-already-attributed");
    public static final URI PRODUCT_ALREADY_ASSIGNED = URI.create(PROBLEM_BASE_URL + "/product-already-assigned");
    public static final URI GROUP_HAS_EMPLOYEE = URI.create(PROBLEM_BASE_URL + "/group-has-employee");
    public static final URI OFFER_HAS_LINE = URI.create(PROBLEM_BASE_URL + "/offer-has-line");
    public static final URI OFFER_HAS_PRODUCT = URI.create(PROBLEM_BASE_URL + "/offer-has-product");

    private ErrorConstants() {
    }
}
