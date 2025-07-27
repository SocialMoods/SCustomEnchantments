package ru.SocialMoods.SCustomEnchantments.api;

import cn.nukkit.item.Item;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.registry.EnchantmentRegistry;

import java.util.Set;

public final class EnchantmentAPI {

    private EnchantmentAPI() {}

    /**
     * Регистрирует новое кастомное зачарование
     * @param enchantment Зачарование для регистрации
     * @throws IllegalArgumentException Если зачарование с таким ID уже существует
     */
    public static void registerEnchantment(CustomEnchantment enchantment, boolean registerBook) {
        EnchantmentRegistry.getInstance().register(enchantment, registerBook);
    }

    /**
     * Получает зачарование по ID
     * @param id Уникальный ID зачарования
     * @return Найденное зачарование или null
     */
    public static CustomEnchantment getEnchantment(String id) {
        return EnchantmentRegistry.getInstance().get(id);
    }

    /**
     * Проверяет наличие зачарования на предмете
     * @param item Предмет для проверки
     * @param enchantmentId ID зачарования
     * @return Уровень зачарования (0 если отсутствует)
     */
    public static int hasEnchantment(Item item, String enchantmentId) {
        return EnchantmentRegistry.getInstance().hasEnchantment(item, enchantmentId);
    }

    /**
     * Получает все зарегистрированные зачарования
     * @return Неизменяемый набор зачарований
     */
    public static Set<CustomEnchantment> getAllEnchantments() {
        return EnchantmentRegistry.getInstance().getAll();
    }

    /**
     * Получает зачарования, совместимые с предметом
     * @param item Предмет для проверки
     * @return Неизменяемый набор совместимых зачарований
     */
    public static Set<CustomEnchantment> getCompatibleEnchantments(Item item) {
        return EnchantmentRegistry.getInstance().getForItem(item);
    }
}