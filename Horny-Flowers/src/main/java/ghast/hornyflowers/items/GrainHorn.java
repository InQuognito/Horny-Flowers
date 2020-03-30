package ghast.hornyflowers.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import vazkii.botania.api.item.IHornHarvestable;
import vazkii.botania.api.item.IHornHarvestable.EnumHornType;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.core.helper.ItemNBTHelper;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

public class GrainHorn extends Item implements IManaItem {
	
	public static final int MAX_MANA = 60;
	public static final int MANA_COST = 30;
	
	private static final String TAG_MANA = "mana";
	
	public GrainHorn(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

	@Nonnull
	@Override
	public UseAction getUseAction(ItemStack par1ItemStack) {
		return UseAction.BOW;
	}
	
	@Override
	public int getUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		player.setActiveHand(hand);
		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}
	
	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int time) {
		if(!player.world.isRemote) {
			if(time != getUseDuration(stack) && time % 5 == 0)
				breakGrass(player.world, stack, new BlockPos(player), player);
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1F, 0.001F);
		}
	}
	
	public static void getManaFromFlowers(World world, ItemStack stack, BlockPos srcPos, PlayerEntity player) {
		GrainHorn horn = (GrainHorn) stack.getItem();
		if(ManaItemHandler.getManaItems(player).size() > 0) {
			for(ItemStack itm : ManaItemHandler.getManaItems(player)) {
				if(itm.getItem() instanceof ItemManaTablet) {
					ItemManaTablet tablet = (ItemManaTablet) itm.getItem();
					if(tablet.getMana(itm) >= MANA_COST) {
						horn.addMana(stack, MANA_COST);
						ItemManaTablet.setMana(itm, tablet.getMana(itm) - MANA_COST);
					}
				}
			}
		}
		/*
		for(BlockPos pos : BlockPos.getAllInBoxMutable(srcPos.add(-16, -16, -16),
				srcPos.add(16, 16, 16))) {
			Block block = world.getBlockState(pos).getBlock();
			if(world.getTileEntity(pos) instanceof TilePool && horn.getMana(stack) <= 5) {
					TilePool entity = (TilePool) world.getTileEntity(pos);
					if(entity.getCurrentMana() >= MANA_COST) {
						horn.addMana(stack, MANA_COST);
						entity.recieveMana(-MANA_COST);
						entity.
						System.out.println(entity.getCurrentMana());
					}
			}
		}*/
	}
	
	public static void breakGrass(World world, ItemStack stack, BlockPos srcPos, LivingEntity player) {
		GrainHorn horn = (GrainHorn) stack.getItem();
		int range = 12 * 3;
		int rangeY = 3 * 4;
		List<BlockPos> coords = new ArrayList<>();

		for(BlockPos pos : BlockPos.getAllInBoxMutable(srcPos.add(-range, -rangeY, -range),
				srcPos.add(range, rangeY, range))) {
			Block block = world.getBlockState(pos).getBlock();
			if(block instanceof CropsBlock /*BushBlock*/ && !block.isIn(ModTags.Blocks.SPECIAL_FLOWERS)) {
					coords.add(pos.toImmutable());
			}
		}

		Collections.shuffle(coords, world.rand);

		int count = Math.min(coords.size(), 32 * 16);
		for(int i = 0; i < count; i++) {
			BlockPos currCoords = coords.get(i);
			BlockState state = world.getBlockState(currCoords);
			Block block = state.getBlock();
			CropsBlock crop = (CropsBlock) block;
			getManaFromFlowers(world, stack, srcPos, (PlayerEntity) player);
			//((IHornHarvestable) block).harvestByHorn(world, currCoords, stack, EnumHornType.WILD);
			if((crop.isMaxAge(state)) && horn.getMana(stack) >= MANA_COST) {
				List<ItemStack> drops = Block.getDrops(world.getBlockState(currCoords), (ServerWorld) world, currCoords, null);
				ItemStack seed = crop.getItem(world, currCoords, world.getBlockState(currCoords));
				//debug
				/*for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						System.out.println(drops.get(it).getCount());
						break;
					}
				}*/
				for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						drops.get(it).setCount(drops.get(it).getCount() - 1);
						it = drops.size();
					}
				}
				//debug
				/*for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						System.out.println(drops.get(it).getCount());
					}
				}*/
				
				for(int it = 0; it < drops.size(); it++) {
					Block.spawnAsEntity(world, currCoords, drops.get(it));
				}
				world.setBlockState(currCoords, block.getDefaultState());
				horn.addMana(stack, -MANA_COST);
			}
		}
	}

	public static void setMana(ItemStack stack, int mana) {
		ItemNBTHelper.setInt(stack, TAG_MANA, Math.min(mana, MAX_MANA));
	}
	
	@Override
	public void addMana(ItemStack stack, int mana) {
		setMana(stack, Math.min(MAX_MANA, getMana(stack) + mana));
		
	}

	@Override
	public boolean canExportManaToItem(ItemStack arg0, ItemStack arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExportManaToPool(ItemStack arg0, TileEntity arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack arg0, ItemStack arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canReceiveManaFromPool(ItemStack arg0, TileEntity arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMana(ItemStack stack) {
		// TODO Auto-generated method stub
		return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
	}

	@Override
	public int getMaxMana(ItemStack arg0) {
		// TODO Auto-generated method stub
		return MAX_MANA;
	}

	@Override
	public boolean isNoExport(ItemStack arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
