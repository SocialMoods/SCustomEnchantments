package ru.SocialMoods.SCustomEnchantments.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.TextFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.*;

public class NBTUtils {

    private static final String CUSTOM_ENCHANTS_TAG = "CustomEnchantments";
    private static final String LORE_TAG = "display";
    private static final String LORE_LIST_TAG = "Lore";

    private static final String[] ROMAN_NUMERALS = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    public static void addCustomEnchantment(Item item, String enchantmentId, String displayName, int level) {
        if (item == null || enchantmentId == null || displayName == null) {
            return;
        }

        CompoundTag tag = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        ListTag<CompoundTag> enchantments = tag.getList(CUSTOM_ENCHANTS_TAG, CompoundTag.class);

        CompoundTag newEnchant = new CompoundTag()
                .putString("id", enchantmentId)
                .putInt("lvl", level);

        boolean exists = false;
        for (CompoundTag ench : enchantments.getAll()) {
            if (ench.getString("id").equals(enchantmentId)) {
                ench.putInt("lvl", level);
                exists = true;
                break;
            }
        }

        if (!exists) {
            enchantments.add(newEnchant);
        }

        tag.putList(enchantments);
        item.setNamedTag(tag);

        updateItemLore(item, displayName, level, exists);

    }

    private static void updateItemLore(Item item, String displayName, int level, boolean existed) {
        CompoundTag tag = item.getNamedTag();
        CompoundTag display = tag.getCompound(LORE_TAG);
        ListTag<StringTag> loreList = display.getList(LORE_LIST_TAG, StringTag.class);

        String enchantmentLine = TextFormat.RESET.toString() + TextFormat.GRAY + displayName + " " + toRoman(level);

        if (existed) {
            for (int i = 0; i < loreList.size(); i++) {
                String line = loreList.get(i).data;
                if (line.contains(displayName)) {
                    loreList.add(i, new StringTag("", enchantmentLine));
                    break;
                }
            }
        } else {
            loreList.add(new StringTag("", enchantmentLine));
        }

        display.putList(loreList);
        tag.putCompound(LORE_TAG, display);
        item.setNamedTag(tag);
    }

    public static String toRoman(int number) {
        if (number > 0 && number <= 10) {
            return ROMAN_NUMERALS[number];
        }
        return String.valueOf(number);
    }

    public static Item removeCustomEnchantment(Item item, String enchantmentId, String displayName) {
        if (item == null || enchantmentId == null || !item.hasCompoundTag()) {
            return item;
        }

        CompoundTag tag = item.getNamedTag();
        ListTag<CompoundTag> enchantments = tag.getList(CUSTOM_ENCHANTS_TAG, CompoundTag.class);

        List<CompoundTag> toRemove = new ArrayList<>();
        for (CompoundTag ench : enchantments.getAll()) {
            if (ench.getString("id").equals(enchantmentId)) {
                toRemove.add(ench);
            }
        }

        toRemove.forEach(enchantments::remove);

        if (enchantments.isEmpty()) {
            tag.remove(CUSTOM_ENCHANTS_TAG);
        } else {
            tag.putList(enchantments);
        }

        if (tag.contains(LORE_TAG)) {
            CompoundTag display = tag.getCompound(LORE_TAG);
            ListTag<StringTag> loreList = display.getList(LORE_LIST_TAG, StringTag.class);

            List<StringTag> toRemoveFromLore = new ArrayList<>();
            for (StringTag line : loreList.getAll()) {
                if (line.data.contains(displayName)) {
                    toRemoveFromLore.add(line);
                }
            }

            toRemoveFromLore.forEach(loreList::remove);

            if (loreList.isEmpty()) {
                display.remove(LORE_LIST_TAG);
            } else {
                display.putList(loreList);
            }

            tag.putCompound(LORE_TAG, display);
        }

        if (!tag.isEmpty()) {
            item.setNamedTag(tag);
        }

        return item;
    }

    public static Item clearAllCustomEnchantments(Item item) {
        if (item == null || !item.hasCompoundTag()) {
            return item;
        }

        CompoundTag tag = item.getNamedTag();
        tag.remove(CUSTOM_ENCHANTS_TAG);

        if (tag.contains(LORE_TAG)) {
            CompoundTag display = tag.getCompound(LORE_TAG);
            display.remove(LORE_LIST_TAG);
            tag.putCompound(LORE_TAG, display);
        }

        if (tag.isEmpty()) {
            item.setNamedTag(null);
        } else {
            item.setNamedTag(tag);
        }

        return item;
    }

    public static int hasCustomEnchantment(Item item, String enchantmentId) {
        if (item == null || enchantmentId == null || !item.hasCompoundTag()) {
            return 0;
        }

        ListTag<CompoundTag> enchantments = item.getNamedTag().getList(CUSTOM_ENCHANTS_TAG, CompoundTag.class);
        for (CompoundTag ench : enchantments.getAll()) {
            if (ench.getString("id").equals(enchantmentId)) {
                return ench.getInt("lvl");
            }
        }

        return 0;
    }

    public static Map<String, Integer> getCustomEnchantments(Item item) {
        if (item == null || !item.hasCompoundTag()) {
            return new HashMap<>();
        }

        ListTag<CompoundTag> enchantments = item.getNamedTag().getList(CUSTOM_ENCHANTS_TAG, CompoundTag.class);
        Map<String, Integer> result = new HashMap<>(enchantments.size());

        for (CompoundTag ench : enchantments.getAll()) {
            result.put(ench.getString("id"), ench.getInt("lvl"));
        }

        return result;
    }

    public static String convertNbtToJson(byte[] nbtData) {
        if (nbtData == null || nbtData.length == 0) {
            return "{}";
        }

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(nbtData);
            CompoundTag tag = NBTIO.read(inputStream, ByteOrder.LITTLE_ENDIAN, true);

            return tag.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\":\"Failed to parse NBT data\"}";
        }
    }
}