package test;

import org.brickhouse.filter.Filter;

public class FilterTest {
    public static void main(String[] args) {
        String filter = "((hotDeck and coldDeck) or (hotDeck and neutralDeck) or (coldDeck and neutralDeck)) and !(hotDeck and coldDeck and neutralDeck)";
        System.out.println(Filter.parse(filter));
    }
}
