package cz.tul.lp.testapp.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class Notebooks {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyNotebook> ITEMS = new ArrayList<DummyNotebook>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyNotebook> ITEM_MAP = new HashMap<String, DummyNotebook>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyNotebook item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyNotebook createDummyItem(int position) {
        return new DummyNotebook(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyNotebook {
        public final String id;
        public final String content;
        public final String details;

        public DummyNotebook(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
