/* 
 * Copyright (c) 2015, Matthew Lohbihler
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package test;

import org.brickhouse.filter.Filter;

public class FilterTest {
    public static void main(String[] args) {
        String filter = "((hotDeck and coldDeck) or (hotDeck and neutralDeck) or (coldDeck and neutralDeck)) and !(hotDeck and coldDeck and neutralDeck)";
        System.out.println(Filter.parse(filter));
    }
}
