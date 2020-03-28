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
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.lib.ModTags;

public class GrainHorn extends Item {
	
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
				breakGrass(player.world, stack, new BlockPos(player));
			player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1F, 0.001F);
		}
	}
	
	public static void breakGrass(World world, ItemStack stack, BlockPos srcPos) {
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
			//((IHornHarvestable) block).harvestByHorn(world, currCoords, stack, EnumHornType.WILD);
			if((crop.isMaxAge(state))) {
				List<ItemStack> drops = Block.getDrops(world.getBlockState(currCoords), (ServerWorld) world, currCoords, null);
				ItemStack seed = crop.getItem(world, currCoords, world.getBlockState(currCoords));
				for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						System.out.println(drops.get(it).getCount());
						break;
					}
				}
				for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						drops.get(it).setCount(drops.get(it).getCount() - 1);
						it = drops.size();
					}
				}
				for(int it = 0; it < drops.size(); it++) {
					if(drops.get(it).getItem().getRegistryName().equals(seed.getItem().getRegistryName())) {
						System.out.println(drops.get(it).getCount());
					}
				}
				
				for(int it = 0; it < drops.size(); it++) {
					Block.spawnAsEntity(world, currCoords, drops.get(it));
				}
				world.setBlockState(currCoords, block.getDefaultState());
			}
		}
	}

	
}
