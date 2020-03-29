package ghast.hornyflowers.items;

import java.util.function.Supplier;

import ghast.hornyflowers.HornyFlowers;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class HornyItemGroup extends ItemGroup {
	
	public static final ItemGroup INSTANCE = new HornyItemGroup(HornyFlowers.MODID, () -> new ItemStack(StaticItems.HORN_GRAIN));
	private final Supplier<ItemStack> icon;
	
	public HornyItemGroup(String label, final Supplier<ItemStack> itemSupplier ) {
		super(label);
		this.icon = itemSupplier;
	}

	@Override
	public ItemStack createIcon() {
		return icon.get();
	}
	
	
}
