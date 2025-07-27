package ru.SocialMoods.SCustomEnchantments.utils;

import cn.nukkit.Player;
import lombok.experimental.UtilityClass;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;

@UtilityClass
public class Utils {

    public float calculateEnchantChance(CustomEnchantment enchant, Player player) {
        float chance = enchant.getEnchantChance();

        if (player.getExperienceLevel() >= 30) {
            chance *= 1.2f;
        }

        return Math.min(chance, 0.9f);
    }

    public int calculateEnchantLevel(CustomEnchantment enchant) {
        return cn.nukkit.utils.Utils.nukkitRandom.nextRange(enchant.getMinLevel(), enchant.getMaxLevel());
    }
}
