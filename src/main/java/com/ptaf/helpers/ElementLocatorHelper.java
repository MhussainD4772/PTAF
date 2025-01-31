package com.ptaf.helpers;

import com.ptaf.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ElementLocatorHelper is a utility class responsible for retrieving element locators from a
 * YAML configuration file based on specified element names and keys. It also provides methods
 * for extracting locator types and actual locator strings from a formatted locator value.
 *
 * Usage:
 * - Use the getElement method to retrieve a locator value for a specific element name and key.
 * - Use the getLocatorType and getLocator methods to parse and retrieve specific parts
 *   of a locator string.
 */
public class ElementLocatorHelper {

    private static final Logger logger = LoggerFactory.getLogger(ElementLocatorHelper.class);

    /**
     * Get the element locator value from the YAML configuration based on the element name and key.
     *
     * @param element The element name for which the locator is to be retrieved.
     * @param key     The key used to retrieve the specific locator for the element.
     * @return The locator value associated with the specified element and key.
     */
    public String getElement(String element, String key) {
        try {
            // Retrieve the locator from the YAML configuration using the specified element and key
            return (String) YamlReader.get("elements." + element + "." + key);
        } catch (Exception e) {
            // Log the error and rethrow the exception if the retrieval fails
            logger.error("Failed to retrieve selector for element '{}'", element + key, e);
            throw e;
        }
    }

    /**
     * Explanation:
     * indexOf("_"): Finds the position of the first underscore in the string.
     * substring(firstUnderscoreIndex + 1): Extracts the substring starting right after the first underscore.
     * Validation: Ensures there is content after the first underscore to avoid exceptions.
     * Fallback: Returns an empty string if the input is invalid or lacks an underscore.
     * Example:
     * Input: "CSS_button" → Output: "button"
     * Input: "ID_field_name" → Output: "field_name"
     * Input: "noUnderscore" → Output: "" (empty string)
     */

    /**
     * Extracts the locator type (e.g., XPATH, CSS) from the locator value.
     *
     * @param locatorValue The full locator value formatted as "LOCATOR_TYPE_LOCATOR".
     * @return The locator type extracted from the locator value.
     */

    public String getLocatorType(String locatorValue) {
        // Extract the locator type by splitting the locatorValue at the first underscore
        int firstUnderscoreIndex = locatorValue.indexOf("_");
        if (firstUnderscoreIndex != -1) {
            return locatorValue.substring(0, firstUnderscoreIndex); // Get substring before the first underscore
        }
        return ""; // Return an empty string if no underscore is found
    }

    /**
     * Explanation:
     * indexOf("_"): Finds the position of the first underscore in the string.
     * substring(firstUnderscoreIndex + 1): Extracts the substring starting right after the first underscore.
     * Validation: Ensures there is content after the first underscore to avoid exceptions.
     * Fallback: Returns an empty string if the input is invalid or lacks an underscore.
     * Example:
     * Input: "CSS_button" → Output: "button"
     * Input: "ID_field_name" → Output: "field_name"
     * Input: "noUnderscore" → Output: "" (empty string)
     */
    /**
     * Extracts the actual locator from the locator value.
     *
     * @param locatorValue The full locator value formatted as "LOCATOR_TYPE_LOCATOR".
     * @return The locator string, which is the part following the locator type.
     */
    public String getLocator(String locatorValue) {
        // Extract the actual locator by splitting the locatorValue at the first underscore
        int firstUnderscoreIndex = locatorValue.indexOf("_");
        if (firstUnderscoreIndex != -1 && firstUnderscoreIndex + 1 < locatorValue.length()) {
            return locatorValue.substring(firstUnderscoreIndex + 1); // Get substring after the first underscore
        }
        return ""; // Return an empty string if no underscore or nothing follows the first underscore
    }
}