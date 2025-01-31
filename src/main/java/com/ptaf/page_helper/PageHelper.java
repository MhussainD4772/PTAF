package com.ptaf.page_helper;

import com.microsoft.playwright.Page;
import com.ptaf.handlers.LocatorHandler;

/**
 * PageHelper class acts as a utility to assist with operations on a Playwright Page object.
 * It encapsulates common functionalities related to page interactions, including element location
 * through a LocatorHandler. This abstraction simplifies the workflow when working with web pages.
 */
public class PageHelper {

    // The Playwright Page object representing the current browser page.
    public Page page;

    // An instance of LocatorHandler to manage and handle element locators on the page.
    private final LocatorHandler locatorHandler;

    /**
     * Constructs a new PageHelper instance with the given Playwright Page.
     *
     * @param page The Playwright Page object that this PageHelper will assist with.
     *             This page object is used to interact with and perform actions on the web page.
     */
    public PageHelper(Page page) {
        this.page = page;  // Assign the provided Page object to the class variable.
        this.locatorHandler = new LocatorHandler(); // Initialize the LocatorHandler for element management.
    }

    // Additional methods for page interaction can be added here.
}