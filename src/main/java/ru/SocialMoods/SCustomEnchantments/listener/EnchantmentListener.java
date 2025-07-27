package ru.SocialMoods.SCustomEnchantments.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.event.inventory.RepairItemEvent;
import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.SocialMoods.SCustomEnchantments.api.EnchantmentAPI;
import ru.SocialMoods.SCustomEnchantments.model.CustomEnchantment;
import ru.SocialMoods.SCustomEnchantments.service.EnchantmentService;
import ru.SocialMoods.SCustomEnchantments.utils.NBTUtils;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class EnchantmentListener implements Listener {

    private final EnchantmentService enchantmentService;

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Item tool = event.getItem();
        Block block = event.getBlock();

        enchantmentService.handleBlockBreak(tool, block, player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            enchantmentService.handleEntityHit(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamageTaken(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            enchantmentService.handleDamageTaken(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled()) return;

        Item inputItem = event.getOldItem();
        Item outputItem = event.getNewItem();
        int enchantLevel = event.getXpCost();
        Player player = event.getEnchanter();

        Map<String, CustomEnchantment> possibleEnchants = EnchantmentAPI.getCompatibleEnchantments(inputItem)
                .stream()
                .collect(Collectors.toMap(
                        CustomEnchantment::getId,
                        e -> e,
                        (existing, replacement) -> existing
                ));

        if (possibleEnchants.isEmpty()) return;

        possibleEnchants.forEach((id, enchant) -> {
            float chance = calculateEnchantChance(enchant, player);
            if (random.nextFloat() < chance) {
                int level = calculateEnchantLevel(enchant);
                NBTUtils.addCustomEnchantment(outputItem, id, enchant.getDisplayName(), level);
            }
        });
    }

    private float calculateEnchantChance(CustomEnchantment enchant, Player player) {
        float chance = enchant.getEnchantChance();

        if (player.getExperienceLevel() >= 30) {
            chance *= 1.2f;
        }

        return Math.min(chance, 0.9f);
    }

    private int calculateEnchantLevel(CustomEnchantment enchant) {;
        return random.nextInt(enchant.getMaxLevel()) + 1;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilUse(RepairItemEvent event) {
        try {
            Item input = event.getOldItem();
            Item material = event.getMaterialItem();
            Item output = event.getNewItem();

            log.info("Начало обработки наковальни. Вход: {}, Материал: {}, Результат: {}",
                    input.getName(), material.getName(), output.getName());

            if (material.getId() == Item.ENCHANTED_BOOK) {
                Map<String, Integer> inputEnchants = NBTUtils.getCustomEnchantments(input);
                Map<String, Integer> materialEnchants = NBTUtils.getCustomEnchantments(material);
                Map<String, Integer> outputEnchants = NBTUtils.getCustomEnchantments(output);

                log.info("Кастомные зачарования - Вход: {}, Материал: {}, Результат (до): {}",
                        inputEnchants, materialEnchants, outputEnchants);

                if (!materialEnchants.isEmpty()) {
                    materialEnchants.forEach((id, level) -> {
                        CustomEnchantment enchant = EnchantmentAPI.getEnchantment(id);
                        if (enchant == null) {
                            log.warn("Не найдено зачарование с ID: {}", id);
                            return;
                        }

                        int currentLevel = outputEnchants.getOrDefault(id, 0);
                        if (currentLevel < level) {
                            log.info("Добавление зачарования {} {} уровня", id, level);NBTUtils.addCustomEnchantment(output, id, enchant.getDisplayName(), level);
                        }
                    });

                    int costIncrease = calculateCustomEnchantCost(materialEnchants);
                    log.info("Увеличение стоимости на {} уровней", costIncrease);
                    event.setCost(event.getCost() + costIncrease);

                    log.info("Результат после обработки: {}", NBTUtils.getCustomEnchantments(output));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке наковальни", e);
        }
    }

    private int calculateCustomEnchantCost(Map<String, Integer> enchants) {
        int cost = 0;
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            CustomEnchantment enchant = EnchantmentAPI.getEnchantment(entry.getKey());
            if (enchant != null) {
                cost += enchant.getRarity().getWeight() * entry.getValue();
            }
        }
        return Math.min(cost, 40);
    }
}