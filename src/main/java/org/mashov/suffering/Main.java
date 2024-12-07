package org.mashov.suffering;

import org.mashov.suffering.templates.FTL;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("suffer!!!");
        FTL.generate("basic.ftl");
        System.out.println();
    }
}
