package com.exemple;

import com.exemple.exception.SlimpayClientException;
import com.exemple.model.SlimpayMandate;
import com.exemple.model.SlimpayOrder;
import com.exemple.service.SlimpayClient;

import java.util.Scanner;

public class Application {

    private SlimpayClient slimpayClient = new SlimpayClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter action you want (create / get / edit / check): ");
        String action = scanner.nextLine();

        System.out.print("Please user Id: ");
        String userId = "testRak" + scanner.nextLine();

        System.out.println("----------------------------------------------------");

        Application app = new Application();

        switch (action.toLowerCase()) {
            case "create" :
                app.signMandate(userId);
                break;
            case "get" :
                System.out.println("Getting mandate of user " + userId);
                app.getMandate(userId);
                break;
            case "edit" :
                app.editMandate(userId);
                break;
            case "check" :
                app.checkMandate(userId);
                break;
            default:
                throw new IllegalStateException("Unknown action");
        }
    }

    private void signMandate(String userId) {
        System.out.println("Creating sign mandate order for user " + userId);
        try {
            SlimpayOrder signMandateOrderResponse = slimpayClient.createSignMandateOrder(userId);

            System.out.println("Sign mandate order created with id " + signMandateOrderResponse.getOrderId());
            System.out.println("Url to sign mandate " + signMandateOrderResponse.getRedirectUrl());

            System.out.print("Press enter once finished to sign mandate ");
            new Scanner(System.in).nextLine();

            System.out.println("---------------------------------------------------- ");

            getMandate(userId);
        } catch (SlimpayClientException e) {
            e.printStackTrace();
        }
    }

    private SlimpayMandate getMandate(String userId) {
        SlimpayMandate slimpayMandate = null;
        try {
            slimpayMandate = slimpayClient.getMandate(userId);

            if(slimpayMandate != null) {
                System.out.println("Reference   : " + slimpayMandate.getReference());
                System.out.println("Signed date : " + slimpayMandate.getDateSigned());
                System.out.println("Status      : " + slimpayMandate.getStatus().getText());
                System.out.println("Bank name   : " + slimpayMandate.getBankName());
                System.out.println("Iban        : " + slimpayMandate.getIban());
                System.out.println("Bic         : " + slimpayMandate.getBic());
            } else {
                System.out.println("No mandate found for user " + userId);
            }
        } catch (SlimpayClientException e) {
            e.printStackTrace();
        }

        return slimpayMandate;
    }

    private void checkMandate(String userId) {
        System.out.println("Checking mandate of user " + userId);
        try {
            boolean hasAlreadyActiveMandate = slimpayClient.hasActiveMandate(userId);

            if(hasAlreadyActiveMandate) {
                System.out.println("User " + userId + " has active mandate");
            } else {
                System.out.println("User " + userId + " doesn't have active mandate");
            }
        } catch (SlimpayClientException e) {
            e.printStackTrace();
        }
    }

    private void editMandate(String userId) {
        System.out.println("Current mandate of user " + userId);

        SlimpayMandate currentMandate = getMandate(userId);
        if(currentMandate == null) {
            System.exit(0);
        }

        try {
            System.out.println("---------------------------------------------------- ");
            System.out.println("Creating amend mandate order for user " + userId);
            SlimpayOrder amendMandateOrderResponse = slimpayClient.createAmendMandateOrder(userId, currentMandate.getReference());

            System.out.println("Amend mandate order created with id " + amendMandateOrderResponse.getOrderId());
            System.out.println("Url to amend mandate " + amendMandateOrderResponse.getRedirectUrl());

            System.out.print("Press enter once finished to amend mandate ");
            new Scanner(System.in).nextLine();

        } catch (SlimpayClientException e) {
            e.printStackTrace();
        }

        System.out.println("---------------------------------------------------- ");
        System.out.println("New mandate of user " + userId);
        getMandate(userId);
    }
}
