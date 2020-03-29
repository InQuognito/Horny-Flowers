package ghast.hornyflowers.events;

import ghast.hornyflowers.HornyFlowers;
import ghast.hornyflowers.items.GrainHorn;
import ghast.hornyflowers.items.HornyItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.common.core.BotaniaCreativeTab;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class HornyRegistries {
	public static Properties defaultProperties(int stackSize) {
		return new Properties().maxStackSize(stackSize).setNoRepair().group(HornyItemGroup.INSTANCE);
	}
	
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
  
    }
    
    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
    	GrainHorn horn = new GrainHorn(defaultProperties(1));
    	event.getRegistry().register(setup(horn, "horn_grain"));
    }
    
    @SubscribeEvent
    public static void onModelRegistry(final ModelRegistryEvent event) {

    }
    
    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
    	return setup(entry, new ResourceLocation(HornyFlowers.MODID, name));
    }

    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
    	entry.setRegistryName(registryName);
    	return entry;
    }

}
