package com.homeapp.backend.models.bike;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The Part object, for a single complete Bike part.
 */
@Entity
public class Part {

    @Id
    @JsonProperty("internalReference")
    private String internalReference;

    @JsonProperty("component")
    private String component;

    @JsonProperty("name")
    private String name;

    @JsonProperty("price")
    private String price;

    @JsonProperty("link")
    private String link;

    @JsonProperty("dateLastUpdated")
    private String dateLastUpdated;

    @JsonProperty("isUpToDate")
    private boolean isUpToDate;

    /**
     * Zero argument Constructor to Instantiate a new Part.
     */
    public Part() {
        this.isUpToDate = false;
    }

    /**
     * Instantiates a new Part.
     *
     * @param component         the component
     * @param internalReference the internalReference
     * @param link              the link
     */
    public Part(String component, String internalReference, String link) {
        this.component = component;
        this.internalReference = internalReference;
        this.link = link;
    }

    /**
     * Instantiates a new Part using all fields.
     *
     * @param component         the component
     * @param internalReference the internalReference
     * @param link              the link
     * @param name              the name
     * @param price             the price
     * @param dateLastUpdated   the date the Price/name was last updated
     * @param isUpToDate        Boolean to show if price was found on start-up
     */
    public Part(String component, String internalReference, String name, String price, String link, String dateLastUpdated, boolean isUpToDate) {
        this.component = component;
        this.internalReference = internalReference;
        this.name = name;
        this.price = price;
        this.link = link;
        this.dateLastUpdated = dateLastUpdated;
        this.isUpToDate = isUpToDate;
    }

    /**
     * Gets component.
     *
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * Sets component.
     *
     * @param component the component
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets internalReference.
     *
     * @return the internalReference
     */
    public String getInternalReference() {
        return internalReference;
    }

    /**
     * Sets internalReference.
     *
     * @param internalReference the internalReference
     */
    public void setInternalReference(String internalReference) {
        this.internalReference = internalReference;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * Gets link.
     *
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets link.
     *
     * @param link the link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Gets dateLastUpdated.
     *
     * @return the dateLastUpdated
     */
    public String getDateLastUpdated() {
        return dateLastUpdated;
    }

    /**
     * Sets dateLastUpdated.
     *
     * @param dateLastUpdated the dateLastUpdated
     */
    public void setDateLastUpdated(String dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    /**
     * Gets isUpToDate.
     *
     * @return boolean isUpToDate
     */
    public boolean getIsUpToDate() {
        return isUpToDate;
    }

    /**
     * Sets IsUpToDate.
     *
     * @param isUpToDate boolean isUpToDate
     */
    public void setIsUptoDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    @Override
    public String toString() {
        return "Part{" +
                "component='" + component + '\'' +
                ", internalReference='" + internalReference + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", link='" + link + '\'' +
                ", dateLastUpdated='" + dateLastUpdated + '\'' +
                ", isUpToDate='" + isUpToDate + '\'' +
                '}';
    }
}