package ru.spbau.mit;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class StringSetImpl implements StringSet, StreamSerializable {

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

    @Override
    public void serialize(OutputStream out) {
        serializeNode(new DataOutputStream(out));
    }

    private void serializeNode(DataOutputStream dataOutputStream) {
        try {
            dataOutputStream.writeBoolean(hasElement);
            dataOutputStream.writeInt(size);
            dataOutputStream.writeInt(characters.size());
            for (Map.Entry<Character, StringSetImpl> entry : characters.entrySet()) {
                dataOutputStream.writeChar(entry.getKey());
                entry.getValue().serializeNode(dataOutputStream);
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    @Override
    public void deserialize(InputStream in) {
        deserializeNode(new DataInputStream(in));
    }

    private void deserializeNode(DataInputStream dataInputStream) {
        try {
            hasElement = dataInputStream.readBoolean();
            size = dataInputStream.readInt();
            characters.clear();
            int characterSize = dataInputStream.readInt();
            for (int i = 0; i < characterSize; i++) {
                char c = dataInputStream.readChar();
                StringSetImpl newNode = new StringSetImpl();
                characters.put(c, newNode);
                newNode.deserializeNode(dataInputStream);
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }
}
