package ru.spbau.mit;

import java.util.HashMap;

public class StringSetImpl implements StringSet {

    private HashMap<Character, StringSetImpl> characters;
    private int size;
    private boolean hasElement;

    public StringSetImpl() {
        characters = new HashMap<>();
        size = 0;
    }

    @Override
    public boolean add(String element) {
        if (element.length() == 0) {
            hasElement = true;
            size++;
            return true;
        }

        char firstChar = element.charAt(0);
        boolean inserted = false;
        if (!characters.containsKey(firstChar)) {
            characters.put(firstChar, new StringSetImpl());
            inserted = true;
        }

        boolean insertedBelow = characters.get(firstChar).add(element.substring(1));
        if (inserted || insertedBelow) {
            size++;
        }
        return inserted || insertedBelow;
    }

    @Override
    public boolean contains(String element) {
        if (element.length() == 0) {
            return hasElement;
        }

        char firstChar = element.charAt(0);
        StringSetImpl insertNode = characters.get(firstChar);
        return insertNode != null && insertNode.contains(element.substring(1));
    }

    @Override
    public boolean remove(String element) {
        if (element.length() == 0) {
            if (hasElement) {
                size--;
                hasElement = false;
                return true;
            }
            return false;
        }

        char firstChar = element.charAt(0);
        if (!characters.containsKey(firstChar)) {
            return false;
        }

        boolean removed = characters.get(firstChar).remove(element.substring(1));
        if (removed) {
            size--;
            if (characters.get(firstChar).size() == 0) {
                characters.remove(firstChar);
            }
        }
        return removed;
    }

    @Override
    public int size() {
        return characters.size();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        if (prefix.length() == 0) {
            return size;
        }

        char firstChar = prefix.charAt(0);
        if (!characters.containsKey(firstChar)) {
            return 0;
        }

        if (prefix.length() == 1) {
            return characters.get(firstChar).size;
        }
        return characters.get(firstChar).howManyStartsWithPrefix(prefix.substring(1));
    }
}
