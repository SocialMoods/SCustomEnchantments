package ru.SocialMoods.SCustomEnchantments.model;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import lombok.Getter;

/**
 * Базовый класс для создания кастомных зачарований.
 * Наследуйте этот класс для реализации собственных эффектов зачарований.
 */
@Getter
public class CustomEnchantment {

    /**
     * Уникальный идентификатор зачарования.
     * Должен быть в нижнем регистре и без пробелов (например: "fire_aspect")
     */
    protected String id;

    /**
     * Отображаемое имя зачарования, которое будет показано в Lore предмета.
     * Может содержать цветовые коды форматирования.
     */
    protected String displayName;

    protected Enchantment.Rarity rarity;

    /**
     * Проверяет, доступно ли данное зачарование для указанного предмета.
     *
     * @param item Предмет, для которого проверяется поддержка зачарования
     * @return true, если зачарование поддерживается предметом, иначе false
     */
    public boolean isSupported(Item item) {
        return true;
    }

    /**
     * Вызывается при разрушении блока предметом с этим зачарованием.
     *
     * @param tool   Инструмент, которым был разрушен блок
     * @param block  Разрушенный блок
     * @param player Игрок, разрушивший блок
     * @param level  Уровень зачарования
     */
    public void onBlockBreak(Item tool, Block block, Player player, int level) {}

    /**
     * Вызывается, когда предметом с этим зачарованием ударяют по мобу или игроку.
     *
     * @param event Событие урона по сущности
     * @return true, если событие должно быть обработано дальше, false для отмены
     */
    public boolean onHit(EntityDamageByEntityEvent event) {
        return true;
    }

    /**
     * Вызывается, когда игрока в броне с этим зачарованием ударяют.
     *
     * @param event Событие получения урона
     * @return true, если событие должно быть обработано дальше, false для отмены
     */
    public boolean onDamage(EntityDamageEvent event) {
        return true;
    }

    /**
     * Возвращает шанс получения этого зачарования при зачаровывании.
     *
     * @return Шанс получения от 0.0 до 1.0
     */
    public float getEnchantChance() {
        return 0.15f;
    }

    /**
     * Возвращает минимальный уровень стола зачарования, необходимый для получения этого зачарования.
     *
     * @return Минимальный уровень стола (1-3)
     */
    public int getMinEnchantLevel() {
        return 1;
    }

    /**
     * Возвращает минимально возможный уровень этого зачарования.
     *
     * @return Минимальный уровень зачарования (обычно 1)
     */
    public int getMinLevel() {
        return 1;
    }

    /**
     * Возвращает максимально возможный уровень этого зачарования.
     *
     * @return Максимальный уровень зачарования
     */
    public int getMaxLevel() {
        return 2;
    }
}