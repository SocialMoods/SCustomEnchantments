# SCustomEnchantments

## ВНИМАНИЕ! Плагин эксперементальный и все еще в разработке.

## Базовый класс CustomEnchantment

Все кастомные зачарования должны наследоваться от базового класса `SCustomEnchantments`. Он предоставляет следующие возможности:

```java
public class MyEnchantment extends CustomEnchantment {
    // Обязательные поля
    protected String id;          // Уникальный ID (lower_case)
    protected String displayName; // Отображаемое имя (может содержать цветовые коды)
    protected Enchantment.Rarity rarity; // ВНИМАНИЕ! Временно не используется, но указывать обязательно
    
    // Методы для переопределения
    public boolean isSupported(Item item); // Проверка совместимости с предметом
    
    // Обработчики событий
    public void onBlockBreak(Item tool, Block block, Player player, int level);
    public boolean onHit(EntityDamageByEntityEvent event);
    public boolean onDamage(EntityDamageEvent event);
    
    // Настройки зачарования
    public float getEnchantChance();    // Шанс получения (0.0-1.0)
    public int getMinEnchantLevel();    // ВНИМАНИЕ! Не работает адекватно
    public int getMinLevel();          // Мин. уровень зачарования
    public int getMaxLevel();          // Макс. уровень зачарования
}
```

---

## Создание своего зачарования

1. Создайте новый класс, унаследованный от `CustomEnchantment`
2. Переопределите необходимые методы
3. Установите основные параметры в конструкторе

### Пример: Зачарование "Молот Тора"

```java
public class ThorHammerEnchantment extends CustomEnchantment {
    
    public ThorHammerEnchantment() {
        this.id = "thor_hammer";
        this.displayName = "§bМолот Тора";
        this.rarity = Enchantment.Rarity.RARE;
    }
    
    @Override
    public boolean isSupported(Item item) {
        return item.isAxe(); // Только для топоров
    }
    
    @Override
    public boolean onHit(EntityDamageByEntityEvent event) {
        if (Math.random() < 0.3) { // 30% шанс
            event.getEntity().getLocation().getLevel().addSound(
                event.getEntity().getLocation(), Sound.AMBIENT_WEATHER_THUNDER
            );
            event.getEntity().getLocation().getLevel().strikeLightning(
                event.getEntity().getLocation()
            );
        }
        return true;
    }
    
    @Override
    public float getEnchantChance() {
        return 0.1f; // 10% шанс получить при зачаровании
    }
    
    @Override
    public int getMaxLevel() {
        return 2; // Максимум II уровень
    }
}
```

---

## Регистрация зачарования

Для регистрации зачарования используйте `EnchantmentAPI`:

```java
public class MyPlugin extends PluginBase {
    @Override
    public void onEnable() {
        // Регистрация с созданием зачарованной книги
        EnchantmentAPI.registerEnchantment(new ThorHammerEnchantment(), true);
        
        // Регистрация без книги
        EnchantmentAPI.registerEnchantment(new AnotherEnchantment(), false);
    }
}
```

---

## Доступные методы API

```java
// Регистрация зачарования
EnchantmentAPI.registerEnchantment(CustomEnchantment enchantment, boolean registerBook);

// Получение зачарования по ID
CustomEnchantment enchant = EnchantmentAPI.getEnchantment("thor_hammer");

// Проверка наличия зачарования на предмете
int level = EnchantmentAPI.hasEnchantment(item, "thor_hammer");

// Получение всех зарегистрированных зачарований
Set<CustomEnchantment> all = EnchantmentAPI.getAllEnchantments();

// Получение зачарований, совместимых с предметом
Set<CustomEnchantment> forItem = EnchantmentAPI.getCompatibleEnchantments(item);
```

---

## Примеры зачарований

### 1. Зачарование "Ярость Вампира"

```java
public class VampireRageEnchantment extends CustomEnchantment {
    public VampireRageEnchantment() {
        this.id = "vampire_rage";
        this.displayName = "§cЯрость Вампира";
        this.rarity = Enchantment.Rarity.VERY_RARE;
    }

    @Override
    public boolean isSupported(Item item) {
        return item.isSword();
    }

    @Override
    public boolean onHit(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        float heal = event.getDamage() * 0.25f; // 25% от урона
        player.setHealth(player.getHealth() + heal);
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
```

### 2. Зачарование "Землетрясение"

```java
public class EarthquakeEnchantment extends CustomEnchantment {
    public EarthquakeEnchantment() {
        this.id = "earthquake";
        this.displayName = "§6Землетрясение";
        this.rarity = Enchantment.Rarity.RARE;
    }

    @Override
    public void onBlockBreak(Item tool, Block block, Player player, int level) {
        if (Math.random() < 0.15 * level) {
            Location loc = block.getLocation();
            loc.getLevel().addSound(loc, Sound.RANDOM_EXPLODE);
            
            for (Entity entity : loc.getLevel().getNearbyEntities(
                new SimpleAxisAlignedBB(loc.x-3, loc.y-1, loc.z-3, loc.x+3, loc.y+1, loc.z+3)
            )) {
                if (entity != player) {
                    entity.setMotion(new Vector3(
                        Math.random() - 0.5, 
                        Math.random() * 0.5, 
                        Math.random() - 0.5
                    ));
                }
            }
        }
    }

    @Override
    public boolean isSupported(Item item) {
        return item.isPickaxe() || item.isShovel();
    }
}
```

---

### Примечание
При создании кастомной книги, она будет автоматически зарегестрирована как кастомный предмет и временно не может использоваться для наковален, данный функционал в разработке и рекомендуется не использовать кастомные книги, а для выдачи зачарования можно использовать команду.