package com.exemple.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlimpayNotification {

    private final static String COMPLETED_STATE_CODE = "closed.completed";

    private String id;
    private String state;
    private String locale;
    private Boolean mandateReused;
    private String dateClosed;
    private List<Error> errors = null;

    public String getId() {
        return id;
    }

    public SlimpayNotification setId(String id) {
        this.id = id;
        return this;
    }

    public String getState() {
        return state;
    }

    public SlimpayNotification setState(String state) {
        this.state = state;
        return this;
    }

    public String getLocale() {
        return locale;
    }

    public SlimpayNotification setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public Boolean getMandateReused() {
        return mandateReused;
    }

    public SlimpayNotification setMandateReused(Boolean mandateReused) {
        this.mandateReused = mandateReused;
        return this;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public SlimpayNotification setDateClosed(String dateClosed) {
        this.dateClosed = dateClosed;
        return this;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public SlimpayNotification setErrors(List<Error> errors) {
        this.errors = errors;
        return this;
    }

    public boolean isCompleted() {
        return COMPLETED_STATE_CODE.equals(state);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {

        private Integer code;
        private String message;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
