package com.example.ld1.data;

public enum AccountType
{
    Admin(0),
    Person(1),
    Company(2);

    private final int value;

    AccountType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
