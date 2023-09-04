package com.codegear.mariamc_rfid.rfidreader.settings;

import com.codegear.mariamc_rfid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.(Added by Pragnesh)
 */
public class SettingsContent {
    /**
     * An array of sample (Settings) items.
     */
    public static List<SettingItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (Settings) items, by ID.
     */
    public static Map<String, SettingItem> ITEM_MAP = new HashMap<>();

    static {
        // Add items.
        addItem(new SettingItem(R.id.profiles + "", "프로파일",/*"Set Antenna parameters",*/R.drawable.profiles));
        addItem(new SettingItem(R.id.advanced_options + "", "고급 리더 옵션",/*"Tag Settings",*/R.drawable.settings_rfid_accessory));
        addItem(new SettingItem(R.id.regulatory + "", "규제",/*"Host and sled volumes",*/R.drawable.settings_regulatory));
        addItem(new SettingItem(R.id.beeper + "", "신호음",/*"Status",*/R.drawable.settings_beeper));
        addItem(new SettingItem(R.id.led + "", "LED ",/*"Status",*/R.drawable.settings_led));
        addItem(new SettingItem(R.id.charge_terminal + "", "충전 단자",/*"Status",*/R.drawable.settings_antenna));// icon need to be replaced.
    }

    private static void addItem(SettingItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A Settings item representing a piece of content.
     */
    public static class SettingItem {
        public String id;
        public String content;
        public int icon;

        public SettingItem(String id, String content/*,String subcontent*/, int icon_id) {
            this.id = id;
            this.content = content;
            this.icon = icon_id;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
