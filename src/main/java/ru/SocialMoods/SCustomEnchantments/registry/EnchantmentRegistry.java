package ru.SocialMoods.SCustomEnchantments.registry;

import cn.nukkit.item.Item;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.utils.NBTUtils;
import ru.SocialMoods.SCustomEnchantments.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class EnchantmentRegistry {

    private static EnchantmentRegistry instance;
    private final Map<String, CustomEnchantment> enchantments = new ConcurrentHashMap<>();

    public static EnchantmentRegistry getInstance() {
        if (instance == null) {
            instance = new EnchantmentRegistry();
        }
        return instance;
    }

    public void register(CustomEnchantment enchantment, boolean registerBook) {
        Objects.requireNonNull(enchantment, "Enchantment cannot be null");
        if (enchantments.containsKey(enchantment.getId())) {
            throw new IllegalArgumentException("Enchantment already registered: " + enchantment.getId());
        }
        if (registerBook) {
            Utils.registerEnchantedBooks(enchantment);
        }
        enchantments.put(enchantment.getId(), enchantment);
    }

    public CustomEnchantment get(String id) {
        return enchantments.get(id);
    }

    public Set<CustomEnchantment> getAll() {
        return Set.copyOf(enchantments.values());
    }

    public Set<CustomEnchantment> getForItem(Item item) {
        Set<CustomEnchantment> result = new HashSet<>();
        for (CustomEnchantment ench : enchantments.values()) {
            if (ench.isSupported(item)) {
                result.add(ench);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public int hasEnchantment(Item item, String enchantmentId) {
        return NBTUtils.hasCustomEnchantment(item, enchantmentId);
    }
}