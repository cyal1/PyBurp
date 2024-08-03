/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.bcryptmontoya;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.Http;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.utilities.Utilities;

import javax.swing.*;
import java.util.Objects;

public class BcryptMontoya implements BurpExtension
{
    public static MontoyaApi api;
    public static Http http;
    public static Proxy proxy;
    public static Utilities utils;
    public static Organizer organizer;

    @Override
    public void initialize(MontoyaApi api) {
        BcryptMontoya.api = api;
        BcryptMontoya.http = api.http();
        BcryptMontoya.proxy = api.proxy();
        BcryptMontoya.organizer = api.organizer();
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        BcryptMontoya.utils = api.utilities();
        api.extension().setName("BcryptMontoya");
        BcryptMontoyaTabs bcryptMontoyaTabs = new BcryptMontoyaTabs();
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());

        JMenuBar burpMenuBar = Objects.requireNonNull(Tools.getBurpFrame()).getJMenuBar();
        burpMenuBar.add(bcryptMontoyaTabs.show);
        burpMenuBar.repaint();
        api.extension().registerUnloadingHandler(() -> {
            bcryptMontoyaTabs.dispose();
            try {
                burpMenuBar.remove(bcryptMontoyaTabs.show);
                burpMenuBar.repaint();
            } catch (NullPointerException ignored) {

            }
        });
    }

}