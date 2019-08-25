package com.exemple.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SlimpayCreateOrderRequest {

    public static final String SEPA_PAYMENT_SCHEMA = "SEPA.DIRECT_DEBIT.CORE";

    private boolean started = true;
    private String paymentScheme;
    private Creditor creditor;
    private Subscriber subscriber;
    private String returnUrl;
    private List<Item> items;

    public boolean getStarted() {
        return started;
    }

    public SlimpayCreateOrderRequest setStarted(boolean started) {
        this.started = started;
        return this;
    }

    public String getPaymentScheme() {
        return paymentScheme;
    }

    public SlimpayCreateOrderRequest setPaymentScheme(String paymentScheme) {
        this.paymentScheme = paymentScheme;
        return this;
    }

    public Creditor getCreditor() {
        return creditor;
    }

    public SlimpayCreateOrderRequest setCreditor(Creditor creditor) {
        this.creditor = creditor;
        return this;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public SlimpayCreateOrderRequest setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public SlimpayCreateOrderRequest setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public SlimpayCreateOrderRequest setItems(List<Item> items) {
        this.items = items;
        return this;
    }

    public static class Subscriber {

        private String reference;

        public Subscriber(String reference) {
            this.reference = reference;
        }

        public String getReference() {
            return reference;
        }

        public Subscriber setReference(String reference) {
            this.reference = reference;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Creditor {

        private String reference;

        public Creditor(String reference) {
            this.reference = reference;
        }

        public String getReference() {
            return reference;
        }

        public Creditor setReference(String reference) {
            this.reference = reference;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {

        public static final String SIGN_MANDATE_TYPE = "signMandate";
        public static final String CARD_ALIAS_TYPE = "cardAlias";

        public static final String SIGN_ACTION = "sign";
        public static final String AMEND_BANK_ACCOUNT_ACTION = "amendBankAccount";

        private String type;
        private String action;
        private Mandate mandate;

        public Item(String type, String action, Mandate mandate) {
            this.type = type;
            this.action = action;
            this.mandate = mandate;
        }

        public String getType() {
            return type;
        }

        public Item setType(String type) {
            this.type = type;
            return this;
        }

        public String getAction() {
            return action;
        }

        public Item setAction(String action) {
            this.action = action;
            return this;
        }

        public Mandate getMandate() {
            return mandate;
        }

        public Item setMandate(Mandate mandate) {
            this.mandate = mandate;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Mandate {

        private Signatory signatory;

        private String reference;

        public Mandate(Signatory signatory) {
            this.signatory = signatory;
        }

        public Mandate(String reference) {
            this.reference = reference;
        }

        public Signatory getSignatory() {
            return signatory;
        }

        public Mandate setSignatory(Signatory signatory) {
            this.signatory = signatory;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public Mandate setReference(String reference) {
            this.reference = reference;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Signatory {

        public static final String MR = "Mr";
        public static final String MRS = "Mrs";
        public static final String MISS = "Miss";

        private BillingAddress billingAddress;
        private String companyName;
        private String honorificPrefix;
        private String familyName;
        private String givenName;
        private String email;
        private String telephone;

        public BillingAddress getBillingAddress() {
            return billingAddress;
        }

        public Signatory setBillingAddress(BillingAddress billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public String getCompanyName() {
            return companyName;
        }

        public Signatory setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public String getHonorificPrefix() {
            return honorificPrefix;
        }

        public Signatory setHonorificPrefix(String honorificPrefix) {
            this.honorificPrefix = honorificPrefix;
            return this;
        }

        public String getFamilyName() {
            return familyName;
        }

        public Signatory setFamilyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public String getGivenName() {
            return givenName;
        }

        public Signatory setGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public String getEmail() {
            return email;
        }

        public Signatory setEmail(String email) {
            this.email = email;
            return this;
        }

        public String getTelephone() {
            return telephone;
        }

        public Signatory setTelephone(String telephone) {
            this.telephone = telephone;
            return this;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BillingAddress {

        private String street1;
        private String street2;
        private String city;
        private String postalCode;
        private String country;

        public String getStreet1() {
            return street1;
        }

        public BillingAddress setStreet1(String street1) {
            this.street1 = street1;
            return this;
        }

        public String getStreet2() {
            return street2;
        }

        public BillingAddress setStreet2(String street2) {
            this.street2 = street2;
            return this;
        }

        public String getCity() {
            return city;
        }

        public BillingAddress setCity(String city) {
            this.city = city;
            return this;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public BillingAddress setPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public String getCountry() {
            return country;
        }

        public BillingAddress setCountry(String country) {
            this.country = country;
            return this;
        }
    }

}

