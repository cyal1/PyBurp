/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.turboburp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.Http;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.utilities.Utilities;

import javax.swing.*;
import java.util.Objects;

public class TurboBurp implements BurpExtension
{
    public static MontoyaApi api;
    public static Http http;
    public static Proxy proxy;
    public static Utilities utils;
    public static Organizer organizer;

    @Override
    public void initialize(MontoyaApi api) {
        TurboBurp.api = api;
        TurboBurp.http = api.http();
        TurboBurp.proxy = api.proxy();
        TurboBurp.organizer = api.organizer();
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        TurboBurp.utils = api.utilities();
        api.extension().setName("Turbo Burp");
        TurboBurpTabs turboBurpTabs = new TurboBurpTabs();
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());

        JMenuBar burpMenuBar = Objects.requireNonNull(Tools.getBurpFrame()).getJMenuBar();
        burpMenuBar.add(turboBurpTabs.show);
        burpMenuBar.repaint();
        api.extension().registerUnloadingHandler(() -> {
            turboBurpTabs.dispose();
            try {
                burpMenuBar.remove(turboBurpTabs.show);
                burpMenuBar.repaint();
            } catch (NullPointerException ignored) {

            }
        });
    }

}