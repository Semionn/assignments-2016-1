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
        boolean result = !contains(element);
        if (result) {
            add(element, 0);
        }
        return result;
    }

    private void add(String element, int position) {
        if (element.length() == position) {
            hasElement = true;
            size++;
            return;
        }

        char substrChar = element.charAt(position);
        if (!characters.containsKey(substrChar)) {
            characters.put(substrChar, new StringSetImpl());
        }

        characters.get(substrChar).add(element, position + 1);
        size++;
    }

    @Override
    public boolean contains(String element) {
        StringSetImpl resultNode = traverse(element, 0);
        return resultNode != null && resultNode.hasElement;
    }

    private StringSetImpl traverse(String element, int position) {
        if (element.length() == position) {
            return this;
        }

        char substrChar = element.charAt(position);
        StringSetImpl nextNode = characters.get(substrChar);
        if (nextNode == null) {
            return null;
        }
        return nextNode.traverse(element, position + 1);
    }

    @Override
    public boolean remove(String element) {
        boolean result = contains(element);
        if (result) {
            remove(element, 0);
        }
        return result;
    }

    private void remove(String element, int position) {
        if (element.length() == position) {
            size--;
            hasElement = false;
            return;
        }

        char substrChar = element.charAt(position);
        characters.get(substrChar).remove(element, position + 1);
        size--;
        if (characters.get(substrChar).size == 0) {
            characters.remove(substrChar);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        StringSetImpl resultNode = traverse(prefix, 0);
        if (resultNode == null) {
            return 0;
        }
        return resultNode.size();
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
