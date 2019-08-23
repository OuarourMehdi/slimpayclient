package com.exemple.model;

public enum SlimpayErrorCode {

    CODE_100(100, "Internal error", " "),
    CODE_103(103, "Creditor authentication failed", " "),
    CODE_122(122, "Invalid honorific prefix", " "),
    CODE_123(123, "Invalid given name", " "),
    CODE_124(124, "Invalid family name", " "),
    CODE_125(125, "Invalid email", " "),
    CODE_127(127, "Invalid telephone", " "),
    CODE_128(128, "Invalid postal code", " "),
    CODE_129(129, "Invalid city", " "),
    CODE_146(146, "Invalid country", " "),
    CODE_133(133, "Invalid bic", " "),
    CODE_134(134, "Invalid IBAN", " "),
    CODE_135(135, "Invalid mandate reference", " "),
    CODE_141(141, "Invalid subscriber reference", " "),
    CODE_142(142, "Invalid company name", " "),
    CODE_143(143, "Invalid organization name", " "),
    CODE_144(144, "Invalid address line 1", " "),
    CODE_145(145, "Invalid address line 2", " "),
    CODE_180(180, "Duplicate mandate reference", " "),
    CODE_191(191, "Invalid date signed", " "),
    CODE_205(205, "Inconsistent client data", " "),
    CODE_301(301, "Card transaction refusal", " "),
    CODE_199(199, "Mandate not found", " "),
    CODE_196(196, "Undefined payment scheme", " "),
    CODE_230(230, "Inconsistent signature approval", " "),
    CODE_231(231, "Invalid payment order items", " "),
    CODE_232(232, "Inconsistent signature approval and payment scheme", " "),
    CODE_239(239, "Service Unavailable", " "),
    CODE_631(631, "This account does not exist", " "),
    CODE_632(632, "Referral - Contact your bank for authorization", " "),
    CODE_633(633, "Operation cancelled not authorized", " "),
    CODE_634(634, "Card expired", " "),
    CODE_635(635, "Card lost", " "),
    CODE_636(636, "Card stolen", " "),
    CODE_637(637, "Invalid merchant", " "),
    CODE_638(638, "Conversion rate is not found", " "),
    CODE_639(639, "Used order", " "),
    CODE_640(640, "Debit transaction frequency exceeded", " "),
    CODE_641(641, "Opposition on the account (temporary)", " "),
    CODE_642(642, "Invalid bank", " "),
    CODE_643(643, "Invalid card number", " "),
    CODE_644(644, "Invalid PIN code", " "),
    CODE_645(645, "Acquirer Invalid transaction", " "),
    CODE_646(646, "Invalid birthdate", " "),
    CODE_647(647, "Invalid cvv2", " "),
    CODE_648(648, "Invalid amount", " "),
    CODE_649(649, "Amount limit", " "),
    CODE_650(650, "Security violation", " "),
    CODE_651(651, "Counterfeit suspected", " "),
    CODE_652(652, "Transaction can not be found", " "),
    CODE_653(653, "Transaction refused", " "),
    CODE_654(654, "This transaction is not authorized", " "),
    CODE_655(655, "Card not registered", " "),
    CODE_656(656, "Fraud detected", " "),
    CODE_661(661, "Card not enrolled", " "),
    CODE_662(662, "Card not participating", " "),
    CODE_663(663, "Card authentication failed", " "),
    CODE_664(664, "Can not find verify Enrollment call for this card", " "),
    CODE_665(665, "Duplicated transaction", " "),
    CODE_666(666, "Invalid PARES", " "),
    CODE_667(667, "Enrollment verification failed", " "),
    CODE_668(668, "Authentication verification failed", " "),
    CODE_901(901, "Duplicate order", " "),
    CODE_902(902, "Resource not found", " "),
    CODE_903(903, "Illegal state", " "),
    CODE_904(904, "Access denied", " "),
    CODE_905(905, "Unmapped error", " "),
    CODE_906(906, "Framework error", " "),
    CODE_907(907, "Bad gateway", " "),
    CODE_908(908, "Currency mismatch", " "),
    CODE_910(910, "Unsupported payment scheme", " "),
    CODE_911(911, "Invalid amount", " "),
    CODE_912(912, "No mandate", " "),
    CODE_913(913, "Client configuration error", " "),
    CODE_914(914, "Validation error", " "),
    CODE_915(915, "Payment can't be cancelled", " "),
    CODE_916(916, "Subscriber account not found", " "),
    CODE_917(917, "Creditor account not found", " "),
    CODE_918(918, "Multiple accounts", " "),
    CODE_919(919, "Missing required creditor entity", " "),
    CODE_920(920, "Creditor not configured with entities", " "),
    CODE_921(921, "Creditor entity not found", " "),
    CODE_922(922, "Report interval conflict", " "),
    CODE_923(923, "Creditor configured with entities conflict", " "),
    CODE_924(924, "Unreadable binary content", " "),
    CODE_925(925, "Checkout user approval error", " "),
    CODE_101(101, "Incomplete request data", " "),
    CODE_105(105, "Incoherent timestamp", " "),
    CODE_108(108, "No customer", " "),
    CODE_120(120, "Session timeout", " "),
    CODE_158(158, "Max OTP key try reached", " "),
    CODE_160(160, "Max OTP key send reached", " "),
    CODE_179(179, "Invalid creditor", " "),
    CODE_181(181, "No documents to sign", " "),
    CODE_186(186, "Forbidden IBAN", " "),
    CODE_188(188, "Invalid account owner", " "),
    CODE_201(201, "Mandate signature failed", " "),
    CODE_202(202, "Invalid user approval (OTP) code", " "),
    CODE_400(400, "Card operation refused by operator", " "),
    CODE_401(401, "Card operation bank refusal", " "),
    CODE_402(402, "Card operation access failure", " "),
    CODE_407(407, "Card operation invalid transaction reference", " "),
    CODE_408(408, "Card lost", " "),
    CODE_409(409, "Card stolen", " "),
    CODE_412(412, "Card suspect", " "),
    CODE_1001(1001, "Missing phone", " "),
    CODE_1002(1002, "Phone number is not accessible", " "),
    CODE_1003(1003, "Phone number not received", " "),
    CODE_1004(1004, "Missing email", " "),
    CODE_1005(1005, "Inaccessible email", " "),
    CODE_1008(1008, "Email not received", " "),
    CODE_1009(1009, "Code cancelled", " "),
    CODE_2000(2000, "Forbidden access code reuse", " "),
    CODE_2001(2001, "Forbidden access code override", " "),
    UNKNOWN(9999, "Unknown error", " ");

    private int code;
    private String slimpayMessage;
    private String userMessage;

    SlimpayErrorCode(int code, String slimpayMessage, String userMessage) {
        this.code = code;
        this.slimpayMessage = slimpayMessage;
        this.userMessage = userMessage;
    }

    public int getCode() {
        return code;
    }

    public String getSlimpayMessage() {
        return slimpayMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public static SlimpayErrorCode fromCode(int code) {
        for (SlimpayErrorCode slimpayErrorCode : SlimpayErrorCode.values()) {
            if (slimpayErrorCode.code == code) {
                return slimpayErrorCode;
            }
        }
        return UNKNOWN;
    }
}