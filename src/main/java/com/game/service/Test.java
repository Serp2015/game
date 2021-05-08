package com.game.service;

import java.util.Calendar;

public class Test {
    public static void main(String[] args) {
        long newBirthday = 1178571600000L;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(newBirthday);
        System.out.println(calendar.getTime());
        System.out.println(calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000);
    }
}
