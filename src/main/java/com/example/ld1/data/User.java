package com.example.ld1.data;

import java.time.LocalDate;

public class User extends dbBase
{
    private String username;
    private String password;
    private LocalDate dateCreated;
    private LocalDate dateModified;
    private String secondaryText1;
    private String secondaryText2;
    private AccountType accountType;

    public User(){}

    public User(String username, String password, LocalDate dateCreated, LocalDate dateModified, String secondaryText1, String secondaryText2, AccountType accountType)
    {
        this.username = username;
        this.password = password;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.secondaryText1 = secondaryText1;
        this.secondaryText2 = secondaryText2;
        this.accountType = accountType;
    }

    public User(String username, String password, String secondaryText1, String secondaryText2, AccountType accountType)
    {
        this.username = username;
        this.password = password;
        this.dateCreated = LocalDate.now();
        this.dateModified = LocalDate.now();
        this.secondaryText1 = secondaryText1;
        this.secondaryText2 = secondaryText2;
        this.accountType = accountType;
    }

    public boolean isCompany()
    {
        return accountType == AccountType.Company || accountType == AccountType.Admin;
    }

    public boolean isPerson()
    {
        return accountType == AccountType.Person;
    }

    public boolean isAdmin()
    {
        return accountType == AccountType.Admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDate getDateModified() {
        return dateModified;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public String getSecondaryText1() {
        return secondaryText1;
    }

    public void setSecondaryText1(String secondaryText1) {
        this.secondaryText1 = secondaryText1;
    }

    public String getSecondaryText2() {
        return secondaryText2;
    }

    public void setSecondaryText2(String secondaryText2) {
        this.secondaryText2 = secondaryText2;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
