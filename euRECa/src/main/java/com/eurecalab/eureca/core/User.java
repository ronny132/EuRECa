package com.eurecalab.eureca.core;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDocument;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.eurecalab.eureca.constants.GenericConstants;

import java.text.ParseException;
import java.util.Date;

@DynamoDBDocument
@DynamoDBTable(tableName = "User")
public class User {
    private String email;
    private Date expireDate;
    private String proVersionExpireDate;
    private String displayName;
    private int admin;

    public User() {
        admin = GenericConstants.FALSE_INT;
    }

    @DynamoDBHashKey(attributeName = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "ProVersionExpireDate")
    public String getProVersionExpireDate() {
        return proVersionExpireDate;
    }

    public void setProVersionExpireDate(String proVersionExpireDate) {
        this.proVersionExpireDate = proVersionExpireDate;
        try {
            expireDate = GenericConstants.DATE_FORMATTER.parse(proVersionExpireDate);
        } catch (ParseException e) {
            expireDate = new Date();
        }
    }

    @DynamoDBIgnore
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
        this.proVersionExpireDate = GenericConstants.DATE_FORMATTER.format(expireDate);
    }

    @DynamoDBIgnore
    public boolean isProUser() {
        return new Date().before(expireDate);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @DynamoDBAttribute(attributeName = "DisplayName")
    public String getDisplayName() {
        return displayName;
    }

    @DynamoDBAttribute(attributeName = "Admin")
    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}
