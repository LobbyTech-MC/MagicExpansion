package io.Yomicer.magicExpansion.items.generators;

import static io.Yomicer.magicExpansion.utils.ColorGradient.getGradientName;
import static io.Yomicer.magicExpansion.utils.ColorGradient.getRandomGradientName;
import static io.Yomicer.magicExpansion.utils.Utils.doGlow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;

import io.Yomicer.magicExpansion.items.abstracts.MenuBlock;
import io.Yomicer.magicExpansion.items.misc.fish.Fish;
import io.Yomicer.magicExpansion.items.misc.fish.FishKeys;
import io.Yomicer.magicExpansion.utils.CustomHeadUtils.CustomHead;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.inventory.DirtyChestMenu;
import net.guizhanss.guizhanlib.minecraft.helper.inventory.ItemStackHelper;

public class FishOutputMachine extends MenuBlock implements EnergyNetComponent, RecipeDisplayItem {

    private final int Capacity;
    public static final int ENERGY_CONSUMPTION = 260;
    private static final int FishSlot = 49;

    // 1. 定义所有鱼类型与输出物品的映射（集中管理，易扩展）
    private final Map<String, ItemStack> FISH_OUTPUT_MAP = new LinkedHashMap<>() {{
        put("CopperDustFish",     SlimefunItems.COPPER_DUST);
        put("GoldDustFish",       SlimefunItems.GOLD_DUST);
        put("IronDustFish",       SlimefunItems.IRON_DUST);
        put("TinDustFish",        SlimefunItems.TIN_DUST);
        put("SilverDustFish",     SlimefunItems.SILVER_DUST);
        put("AluminumDustFish",   SlimefunItems.ALUMINUM_DUST);
        put("LeadDustFish",       SlimefunItems.LEAD_DUST);
        put("ZincDustFish",       SlimefunItems.ZINC_DUST);
        put("MagnesiumDustFish",  SlimefunItems.MAGNESIUM_DUST);
        // 🔶 煤晶鱼 → 煤炭
        put("CoalFish", new ItemStack(Material.COAL));
        // 💚 翠宝鱼 → 绿宝石
        put("EmeraldFish", new ItemStack(Material.EMERALD));
        // 🔷 靛灵鱼 → 青金石
        put("LapisFish", new ItemStack(Material.LAPIS_LAZULI));
        // 💎 晶耀鱼 → 钻石
        put("DiamondFish", new ItemStack(Material.DIAMOND));
        // 🔴 焰晶鱼 → 下界石英
        put("QuartzFish", new ItemStack(Material.QUARTZ));
        // 🟣 震颤鱼 → 紫水晶碎片
        put("AmethystFish", new ItemStack(Material.AMETHYST_SHARD));
        // ⚫ 铁核鱼 → 铁锭
        put("IronFish", new ItemStack(Material.IRON_INGOT));
        // 🟡 鎏核鱼 → 金锭
        put("GoldFish", new ItemStack(Material.GOLD_INGOT));
        // 🟠 铜脉鱼 → 铜锭
        put("CopperFish", new ItemStack(Material.COPPER_INGOT));
        // 🟠 赤脉鱼 → 红石
        put("RedstoneFish", new ItemStack(Material.REDSTONE));
        // ⚔️ 狱铸鱼 → 下界合金锭
        put("NetheriteFish", new ItemStack(Material.NETHERITE_INGOT));
        // ⚔️ 灯笼鱼 → 萤石粉
        put("GlowStoneDustFish", new ItemStack(Material.GLOWSTONE_DUST));
        // ⚔️ 塑灵鱼 → 塑料纸
        put("ShuLingYu", SlimefunItems.PLASTIC_SHEET);
        // ⚔️ 铀核鱼 → U
        put("UraniumFish", SlimefunItems.URANIUM);
        // ⚔️ 油岩鱼 → 原油桶
        put("OilRockFish", SlimefunItems.OIL_BUCKET);
        // ⚔️ 泡晶鱼 → 起泡锭
        put("FoamCrystalFish", SlimefunItems.BLISTERING_INGOT_3);
        // ⚔️ 黑曜鱼 → 黑金刚石
        put("BlackDiamondFish", SlimefunItems.CARBONADO);
        // ⚔️ 灵咒鱼 → 附魔之瓶
        put("EnchantedBottleFish", new ItemStack(Material.EXPERIENCE_BOTTLE));
        // ⚔️ 晶鳞鱼 → 硫酸盐
        put("SulfateFish", SlimefunItems.SULFATE);
        // ⚔️ 酸晶鱼 → 硅
        put("SiliconFish", SlimefunItems.SILICON);

        // 【合金灵鱼】用于生产：强化合金锭
        put("ReinforcedAlloyFish", SlimefunItems.REINFORCED_ALLOY_INGOT);

        // 【硬化灵鱼】用于生产：硬化金属
        put("HardenedMetalFish", SlimefunItems.HARDENED_METAL_INGOT);

        // 【大马士革灵鱼】用于生产：大马士革钢锭
        put("DamascusSoulFish", SlimefunItems.DAMASCUS_STEEL_INGOT);

        // 【钢魄鱼】用于生产：钢锭
        put("SteelSoulFish", SlimefunItems.STEEL_INGOT);

        // 【青铜古影鱼】用于生产：青铜锭
        put("BronzeAncientFish", SlimefunItems.BRONZE_INGOT);

        // 【硬铝天翔鱼】用于生产：硬铝锭
        put("HardlightAluFish", SlimefunItems.DURALUMIN_INGOT);

        // 【银铜灵鱼】用于生产：银铜合金锭
        put("SilverCopperFish", SlimefunItems.BILLON_INGOT);

        // 【黄铜鸣音鱼】用于生产：黄铜锭
        put("BrassResonanceFish", SlimefunItems.BRASS_INGOT);

        // 【铝黄铜灵鱼】用于生产：铝黄铜锭
        put("AluminumBrassFish", SlimefunItems.ALUMINUM_BRASS_INGOT);

        // 【铝青铜灵鱼】用于生产：铝青铜锭
        put("AluminumBronzeFish", SlimefunItems.ALUMINUM_BRONZE_INGOT);

        // 【科林斯青铜灵鱼】用于生产：科林斯青铜锭
        put("CorinthianBronzeFish", SlimefunItems.CORINTHIAN_BRONZE_INGOT);

        // 【焊锡灵鱼】用于生产：焊锡锭
        put("SolderFlowFish", SlimefunItems.SOLDER_INGOT);

        // 【镍魄鱼】用于生产：镍锭
        put("NickelSpiritFish", SlimefunItems.NICKEL_INGOT);

        // 【钴焰鱼】用于生产：钴锭
        put("CobaltFlameFish", SlimefunItems.COBALT_INGOT);

        // 【硅铁灵鱼】用于生产：硅铁
        put("SiliconIronFish", SlimefunItems.FERROSILICON);

        // 【碳魂鱼】用于生产：碳块
        put("CarbonSoulFish", SlimefunItems.CARBON_CHUNK);

        // 【镀金灵鱼】用于生产：镀金铁锭
        put("GildedIronFish", SlimefunItems.GILDED_IRON);

        // 【红石合金灵鱼】用于生产：红石合金锭
        put("RedstoneAlloyFish", SlimefunItems.REDSTONE_ALLOY);

        // 【镎影鱼】用于生产：镎
        put("NeptuniumShadowFish", SlimefunItems.NEPTUNIUM);

        // 【钚心鱼】用于生产：钚
        put("PlutoniumCoreFish", SlimefunItems.PLUTONIUM);





    }};


    public FishOutputMachine(ItemGroup category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, int Capacity) {
        super(category, item, recipeType, recipe);
        this.Capacity = Capacity;
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, SlimefunBlockData data) {
                FishOutputMachine.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }
    protected void tick(Block block) {
        BlockMenu inv = StorageCacheUtils.getMenu(block.getLocation());

        if(inv != null && inv.hasViewer()) {
            if (getCharge(block.getLocation()) < getEnergyConsumption()) {
                inv.addItem(40, new CustomItemStack(doGlow(Material.LANTERN), getGradientName("⚡机器停止运行⚡"),
                                getGradientName("请检查电力供应是否充足或鱼种是否符合")),
                        (player1, slot, item, action) -> false);
                return;
            }
        }

        ItemStack fish = null;
        ItemMeta meta = null;
        if (inv != null) {
            fish = inv.getItemInSlot(FishSlot);
            if (fish != null && !fish.getType().isAir()) {
                fish = fish.clone();
            }
        }
        if(fish != null) {
            meta = fish.getItemMeta();
        }
        ItemStack outItems = null;
        if(meta != null) {

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            // 读取PDC数据
            String fishType = pdc.get(FishKeys.FISH_TYPE, PersistentDataType.STRING);
            Double weight = pdc.get(FishKeys.FISH_WEIGHT, PersistentDataType.DOUBLE);
            String weightRarityName = pdc.get(FishKeys.FISH_WEIGHT_RARITY, PersistentDataType.STRING);

            // 基础校验
            if (fishType == null || weight == null || weight == 0.0 || weightRarityName == null) {
                return ;
            }

            // 从映射中查找对应输出物品
            ItemStack baseOutputOrigin = FISH_OUTPUT_MAP.get(fishType);
            if(baseOutputOrigin != null) {
                ItemStack baseOutput = FISH_OUTPUT_MAP.get(fishType).clone();
                if (baseOutput != null) {

                    int multiplier = Fish.WeightRarity.getMultiplierByName(weightRarityName);
                    int amount = (int) (weight * multiplier);
                    if (amount <= 0) amount = 1;

                    baseOutput.setAmount(amount);
                    outItems = baseOutput;

                }
            }

        }
        if (outItems != null && inv != null) {
            removeCharge(block.getLocation(), getEnergyConsumption());
            inv.addItem(40, new CustomItemStack(doGlow(Material.SOUL_LANTERN), getGradientName("⚡机器正在运行⚡"),
                            getGradientName("本机器会源源不断地生产，即使输出槽已经填满了"),
                            getRandomGradientName("当前产出: ")+ ItemStackHelper.getDisplayName(outItems),
                            getRandomGradientName("当前效率: "+ calculateRealAmount(outItems) + "个/tick")),
                    (player1, slot, item, action) -> false);
            pushAllItems(inv,outItems, getOutputSlots());
            return;
        }
        if (inv != null) {
            inv.addItem(40, new CustomItemStack(doGlow(Material.LANTERN), getGradientName("⚡机器停止运行⚡"),
                            getGradientName("请检查电力供应是否充足或鱼种是否符合")),
                    (player1, slot, item, action) -> false);
        }

    }

    private int calculateRealAmount(ItemStack item) {
        int totalAmount = item.getAmount(); // 这就是原始总数量
        int maxStackSize = 64;
        int realAmount = 0;

        // 模拟 dropItemInBatches 的分批逻辑，累加每一批的数量
        while (totalAmount > 0) {
            int batchSize = Math.min(totalAmount, maxStackSize);
            realAmount += batchSize;      // 累加这一批
            totalAmount -= batchSize;     // 减去已处理的
        }

        return realAmount;
    }

    protected void pushAllItems(BlockMenu menu, ItemStack item, int[] outputSlots) {
        if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) {
            return;
        }

        int totalAmount = item.getAmount();  // 总共有多少个
        int perPush = 64;                    // 每次塞64个

        while (totalAmount > 0) {
            ItemStack toPush = item.clone();
            toPush.setAmount(Math.min(totalAmount, perPush));  // 最后一次可能不足64

            menu.pushItem(toPush, outputSlots);  // 直接塞！不管有没有被拒绝（暴力！）

            totalAmount -= perPush;  // 每次减64，不管实际推进去多少（简单粗暴）
        }
    }

    public int getEnergyConsumption() {
        return ENERGY_CONSUMPTION;
    }



    @Override
    protected void setup(BlockMenuPreset var1) {
        var1.drawBackground(new CustomItemStack(Material.PINK_STAINED_GLASS_PANE,"§b请将鱼放入到该槽位中"),new int[] {

                48,  50
        });
        var1.drawBackground(new CustomItemStack(Material.LIME_STAINED_GLASS_PANE,"§b机器工作状态"),new int[] {

                39, 40, 41
        });

    }
    @Nonnull
    @Override
    protected int[] getInputSlots(DirtyChestMenu dirtyChestMenu, ItemStack itemStack) {
        return new int[]{49};
    }

    @Override
    protected int[] getInputSlots() {
        return new int[]{
                49
        };
    }

    @Override
    protected int[] getOutputSlots() {
        return new int[]{
                0,1,2,3,4,5,6,7,8,
                9,10,11,12,13,14,15,16,17,
                18,19,20,21,22,23,24,25,26,
                27,28,29,30,31,32,33,34,35,
                36,37,38, 42,43,44,
                45,46,47, 51,52,53
        };
    }

    @Override
    public @NotNull EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CONSUMER;
    }

    @Override
    public int getCapacity() {
        return Capacity;
    }

    @Override
    public @NotNull List<ItemStack> getDisplayRecipes() {

        List<ItemStack> display = new ArrayList<>();
        display.add(new CustomItemStack(Material.KNOWLEDGE_BOOK, getGradientName("使用说明⇩"),getGradientName("请务必仔细阅读")));
        display.add(new CustomItemStack(Material.BOOK, getGradientName("使用方法："),getGradientName("将魔法鱼放入到机器槽位中可进行生产")
                ,getGradientName("鱼的种类会影响最终产物种类")
                ,getGradientName("鱼的重量会影响最终产物数量")));
        display.add(new CustomItemStack(Material.KNOWLEDGE_BOOK, getGradientName("使用说明⇩"),getGradientName("请务必仔细阅读")));
        display.add(new CustomItemStack(Material.BOOK, getGradientName("产出量算法："),getGradientName("每个机器只能放置一条魔法鱼")
                ,getGradientName("产出量 = 重量(向下取整) * 魔法鱼稀有程度")
                ,getGradientName("普通/稀有/超级稀有/鱼皇 : 1/4/11/999")));
        display.add(new CustomItemStack(CustomHead.getHead("26314d31b095e4d421760497be6a156f459d8c9957b7e6b1c12deb4e47860d71"),getGradientName("支持的鱼类 ⇨")));
        display.add(new CustomItemStack(CustomHead.getHead("26314d31b095e4d421760497be6a156f459d8c9957b7e6b1c12deb4e47860d71"),getGradientName("产出的产物 ⇨")));

//        display.add(new CustomItemStack(Material.PUFFERFISH_BUCKET,CopperDustFish.getDisplayName(),getGradientName("每秒产出个数："+ " 重量 * 魔法鱼稀有程度 ")));
//        display.add(outputCopperDust);


        for (Map.Entry<String, ItemStack> entry : FISH_OUTPUT_MAP.entrySet()) {
            String fishTypeName = entry.getKey();
            Fish fish = Fish.fromString(fishTypeName);
            if (fish == null) {
                continue; // 跳过无效类型
            }
            ItemStack output = entry.getValue();
            // 根据稀有度选择不同的鱼桶材质
            Material displayMaterial = switch (fish.getRarity()) {
                case COMMON -> Material.COD_BUCKET;           // 普通 - 鳕鱼桶
                case UNCOMMON -> Material.SALMON_BUCKET;     // 不常见 - 鲑鱼桶
                case RARE -> Material.PUFFERFISH_BUCKET;  // 稀有 - 河豚
                case EPIC -> Material.TROPICAL_FISH_BUCKET;     // 史诗 - 热带鱼
                case LEGENDARY -> Material.AXOLOTL_BUCKET;   // 传说 - 用美西螈
                case MYTHICAL -> Material.NAUTILUS_SHELL;       // 神话 - 下界之星（最稀有）
                default -> Material.COD_BUCKET;
            };

            display.add(new CustomItemStack(
                    displayMaterial,
                    fish.getDisplayName(),
                    getGradientName("每秒产出个数：重量 × 魔法鱼体重稀有程度")
            ));
            display.add(output);
        }
        return display;
    }



    private static void addDisplay(List<ItemStack> l,Material m, String s, ItemStack i){
        l.add(new CustomItemStack(m, s, getGradientName("每秒产出个数：" + " 重量 * 魔法鱼稀有程度 ")));
        l.add(i);
    }

}

