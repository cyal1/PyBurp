/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package io.github.cyal1.pyburp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.Http;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.utilities.Utilities;

import javax.swing.*;
import java.util.Objects;

public class PyBurp implements BurpExtension
{
    public static MontoyaApi api;
    public static Http http;
    public static Proxy proxy;
    public static Utilities utils;
    public static Organizer organizer;

    @Override
    public void initialize(MontoyaApi api) {
        PyBurp.api = api;
        PyBurp.http = api.http();
        PyBurp.proxy = api.proxy();
        PyBurp.organizer = api.organizer();
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        PyBurp.utils = api.utilities();
        api.extension().setName("PyBurp");
        PyBurpTabs pyBurpTabs = new PyBurpTabs();
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());

        JMenuBar burpMenuBar = Objects.requireNonNull(Tools.getBurpFrame()).getJMenuBar();
        burpMenuBar.add(pyBurpTabs.show);
        burpMenuBar.repaint();
        api.extension().registerUnloadingHandler(() -> {
            pyBurpTabs.dispose();
            try {
                burpMenuBar.remove(pyBurpTabs.show);
                burpMenuBar.repaint();
            } catch (NullPointerException ignored) {

            }
        });
    }

}