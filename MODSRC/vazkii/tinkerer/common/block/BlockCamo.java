/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the ThaumicTinkerer Mod.
 *
 * ThaumicTinkerer is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 *
 * ThaumicTinkerer is a Derivative Work on Thaumcraft 4.
 * Thaumcraft 4 (c) Azanor 2012
 * (http://www.minecraftforum.net/topic/1585216-)
 *
 * File Created @ [Dec 27, 2013, 6:25:36 PM (GMT)]
 */
package vazkii.tinkerer.common.block;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.tinkerer.common.block.tile.TileCamo;
import cpw.mods.fml.common.network.PacketDispatcher;

public abstract class BlockCamo extends BlockModContainer<TileCamo> {

	static List<Integer> validRenderTypes = Arrays.asList(0, 31, 39);
	
	protected BlockCamo(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (tile instanceof TileCamo) {
            TileCamo camo = (TileCamo) tile;
            if (camo.camo > 0 && camo.camo < 4096) {
                Block block = Block.blocksList[camo.camo];
                if (block != null && isValidRenderType(block.getRenderType()))
                    return block.getIcon(side, camo.camoMeta);
            }
        }

        return getIconFromSideAfterCheck(tile, meta, side);
    }
	
	public boolean isValidRenderType(int type) {
		return validRenderTypes.contains(type);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);

        if (tile instanceof TileCamo) {
        	TileCamo camo = (TileCamo) tile;
        	ItemStack currentStack = par5EntityPlayer.getCurrentEquippedItem();

        	if(currentStack == null)
        		currentStack = new ItemStack(0, 1, 0);

        	boolean doChange = true;
        	checkChange : {
            	if(currentStack.itemID != 0) {
            		if(currentStack.itemID >= 4096) {
            			doChange = false;
            			break checkChange;
            		}

                    Block block = Block.blocksList[currentStack.itemID];
                    if(block == null || !isValidRenderType(block.getRenderType()) || block == this || block.blockMaterial == Material.air)
                    	doChange = false;
            	}
        	}

        	if(doChange) {
            	camo.camo = currentStack.itemID;
            	camo.camoMeta = currentStack.getItemDamage();
            	PacketDispatcher.sendPacketToAllInDimension(camo.getDescriptionPacket(), par1World.provider.dimensionId);

        		return true;
        	}
        }

        return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	public Icon getIconFromSideAfterCheck(TileEntity tile, int meta, int side) {
		return blockIcon;
	}

}
