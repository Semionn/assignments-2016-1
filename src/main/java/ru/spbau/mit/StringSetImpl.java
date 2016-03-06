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
        return add(element, 0);
    }

    private boolean add(String element, int position) {
        if (element.length() == position) {
            if (!hasElement) {
                hasElement = true;
                size++;
                return true;
            }
            return false;
        }

        char substrChar = element.charAt(position);
        boolean inserted = false;
        if (!characters.containsKey(substrChar)) {
            characters.put(substrChar, new StringSetImpl());
            inserted = true;
        }

        boolean insertedBelow = characters.get(substrChar).add(element, position + 1);
        if (inserted || insertedBelow) {
            size++;
        }
        return inserted || insertedBelow;
    }

    @Override
    public boolean contains(String element) {
        return contains(element, 0);
    }

    private boolean contains(String element, int position) {
        if (element.length() == position) {
            return hasElement;
        }

        char substrChar = element.charAt(position);
        StringSetImpl insertNode = characters.get(substrChar);
        return insertNode != null && insertNode.contains(element, position + 1);
    }

    @Override
    public boolean remove(String element) {
        return remove(element, 0);
    }

    private boolean remove(String element, int position) {
        if (element.length() == position) {
            if (hasElement) {
                size--;
                hasElement = false;
                return true;
            }
            return false;
        }

        char substrChar = element.charAt(position);
        if (!characters.containsKey(substrChar)) {
            return false;
        }

        boolean removed = characters.get(substrChar).remove(element, position + 1);
        if (removed) {
            size--;
            if (characters.get(substrChar).size == 0) {
                characters.remove(substrChar);
            }
        }
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return howManyStartsWithPrefix(prefix, 0);
    }

    private int howManyStartsWithPrefix(String prefix, int position) {
        if (prefix.length() == position) {
            return size;
        }

        char substrChar = prefix.charAt(position);
        if (!characters.containsKey(substrChar)) {
            return 0;
        }

        if (prefix.length() - position == 1) {
            return characters.get(substrChar).size;
        }
        return characters.get(substrChar).howManyStartsWithPrefix(prefix, position + 1);
    }
}
