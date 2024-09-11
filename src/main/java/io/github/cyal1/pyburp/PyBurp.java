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
import burp.api.montoya.ui.menu.BasicMenuItem;
import burp.api.montoya.ui.menu.Menu;
import burp.api.montoya.ui.menu.MenuItem;
import burp.api.montoya.utilities.Utilities;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PyBurp implements BurpExtension
{
    public static MontoyaApi api;
    public static Http http;
    public static Proxy proxy;
    public static Utilities utils;
    public static Organizer organizer;

    @Override
    public void initialize(MontoyaApi api){
        PyBurp.api = api;
        PyBurp.http = api.http();
        PyBurp.proxy = api.proxy();
        PyBurp.organizer = api.organizer();
        // https://portswigger.github.io/burp-extensions-montoya-api/javadoc/burp/api/montoya/utilities/Utilities.html
        PyBurp.utils = api.utilities();
        api.extension().setName("PyBurp");
        PyBurpTabs pyBurpTabs = new PyBurpTabs();
        api.userInterface().registerContextMenuItemsProvider(new ContentTypeContextMenu());

        BasicMenuItem showEventItem = BasicMenuItem.basicMenuItem("Show").withAction(() -> pyBurpTabs.setVisible(true));
        MenuItem helpEventItem = MenuItem.basicMenuItem("Help").withAction(() -> {
            try {java.awt.Desktop.getDesktop().browse(new URI("https://github.com/cyal1/pyburp/"));} catch (URISyntaxException | IOException ignored) {}
        });
        Menu menu = Menu.menu("PyBurp").withMenuItems(showEventItem, helpEventItem);
        api.userInterface().menuBar().registerMenu(menu);
        api.extension().registerUnloadingHandler(pyBurpTabs::dispose);
    }
}