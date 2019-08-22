package com.exemple;

import com.exemple.model.SlimpayOrder;
import com.exemple.service.SlimpayClient;

import java.util.Scanner;

public class Application {

    private SlimpayClient slimpayClient = new SlimpayClient();

    public static void main(String[] args) {
        Application app = new Application();

        String action = args.length > 0 ? args[0] : "signMandate";

        switch (action) {
            case "sign" :
                app.signMandate();
                break;
            case "get" :
                app.getMandate();
                break;
            case "edit" :
                app.editMandate();
                break;

                default:
                    throw new IllegalStateException("Unknown action");
        }
    }

    private void signMandate() {
        String userId = "testRak00000001";

        try {
            System.out.println("Creating sign mandate order for user " + userId);
            SlimpayOrder signMandateOrderResponse = slimpayClient.createSignMandateOrder(userId);

            System.out.println("Order created with id " + signMandateOrderResponse.getOrderId());
            System.out.println("Url to sign mandate " + signMandateOrderResponse.getRedirectUrl());

            System.out.print("Enter 'done' to continue: ");
            Scanner s = new Scanner(System.in);
            while(!s.hasNext()) {
                // Nothing
            }

            System.out.println("Finished");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMandate() {
        try {
            slimpayClient.getSubscriberMandateUrl("testRak00000001");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editMandate() {

    }
}
