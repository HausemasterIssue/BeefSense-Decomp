



package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.*;
import com.gamesense.api.setting.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.util.stream.*;
import com.gamesense.api.util.world.*;
import com.gamesense.api.event.events.*;
import java.util.concurrent.atomic.*;
import net.minecraft.init.*;
import com.gamesense.api.util.combat.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.item.*;
import java.util.function.*;
import com.gamesense.api.util.render.*;
import java.util.*;

public class CityESP extends Module
{
    Setting.Integer range;
    Setting.Integer down;
    Setting.Integer sides;
    Setting.Integer depth;
    Setting.Double minDamage;
    Setting.Double maxDamage;
    Setting.Boolean ignoreCrystals;
    Setting.Mode targetMode;
    Setting.Mode selectMode;
    Setting.Mode renderMode;
    Setting.Integer width;
    Setting.ColorSetting color;
    private final HashMap<EntityPlayer, List<BlockPos>> cityable;
    
    public CityESP() {
        super("CityESP", Module.Category.Render);
        this.cityable = new HashMap<EntityPlayer, List<BlockPos>>();
    }
    
    public void setup() {
        final ArrayList<String> targetModes = new ArrayList<String>();
        targetModes.add("Single");
        targetModes.add("All");
        final ArrayList<String> selectModes = new ArrayList<String>();
        selectModes.add("Closest");
        selectModes.add("All");
        final ArrayList<String> renderModes = new ArrayList<String>();
        renderModes.add("Outline");
        renderModes.add("Fill");
        renderModes.add("Both");
        this.range = this.registerInteger("Range", 20, 1, 30);
        this.down = this.registerInteger("Down", 1, 0, 3);
        this.sides = this.registerInteger("Sides", 1, 0, 4);
        this.depth = this.registerInteger("Depth", 3, 0, 10);
        this.minDamage = this.registerDouble("Min Damage", 5.0, 0.0, 10.0);
        this.maxDamage = this.registerDouble("Max Self Damage", 7.0, 0.0, 20.0);
        this.ignoreCrystals = this.registerBoolean("Ignore Crystals", true);
        this.targetMode = this.registerMode("Target", (List)targetModes, "Single");
        this.selectMode = this.registerMode("Select", (List)selectModes, "Closest");
        this.renderMode = this.registerMode("Render", (List)renderModes, "Both");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(102, 51, 153));
    }
    
    public void onUpdate() {
        if (CityESP.mc.player == null || CityESP.mc.world == null) {
            return;
        }
        this.cityable.clear();
        final List<EntityPlayer> players = (List<EntityPlayer>)CityESP.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer.getDistanceSqToEntity((Entity)CityESP.mc.player) <= this.range.getValue() * this.range.getValue()).filter(entityPlayer -> !EntityUtil.basicChecksEntity(entityPlayer)).collect(Collectors.toList());
        for (final EntityPlayer player : players) {
            if (player == CityESP.mc.player) {
                continue;
            }
            List<BlockPos> blocks = (List<BlockPos>)EntityUtil.getBlocksIn((Entity)player);
            if (blocks.size() == 0) {
                continue;
            }
            int minY = Integer.MAX_VALUE;
            for (final BlockPos block : blocks) {
                final int y = block.getY();
                if (y < minY) {
                    minY = y;
                }
            }
            if (player.posY % 1.0 > 0.2) {
                ++minY;
            }
            final int finalMinY = minY;
            blocks = blocks.stream().filter(blockPos -> blockPos.getY() == finalMinY).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
            final Optional<BlockPos> any = blocks.stream().findAny();
            if (!any.isPresent()) {
                continue;
            }
            final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole((BlockPos)any.get(), false, true);
            if (holeInfo.getType() == HoleUtil.HoleType.NONE) {
                continue;
            }
            if (holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) {
                continue;
            }
            final List<BlockPos> sides = new ArrayList<BlockPos>();
            for (final BlockPos block2 : blocks) {
                sides.addAll(this.cityableSides(block2, HoleUtil.getUnsafeSides(block2).keySet(), player));
            }
            if (sides.size() <= 0) {
                continue;
            }
            this.cityable.put(player, sides);
        }
    }
    
    public void onWorldRender(final RenderEvent event) {
        final AtomicBoolean noRender = new AtomicBoolean(false);
        final AtomicBoolean atomicBoolean;
        this.cityable.entrySet().stream().sorted((entry, entry1) -> (int)entry.getKey().getDistanceSqToEntity((Entity)entry1.getKey())).forEach(entry -> {
            if (!atomicBoolean.get()) {
                this.renderBoxes(entry.getValue());
                if (this.targetMode.getValue().equalsIgnoreCase("All")) {
                    atomicBoolean.set(true);
                }
            }
        });
    }
    
    private List<BlockPos> cityableSides(final BlockPos centre, final Set<HoleUtil.BlockOffset> weakSides, final EntityPlayer player) {
        final List<BlockPos> cityableSides = new ArrayList<BlockPos>();
        final HashMap<BlockPos, HoleUtil.BlockOffset> directions = new HashMap<BlockPos, HoleUtil.BlockOffset>();
        for (final HoleUtil.BlockOffset weakSide : weakSides) {
            final BlockPos pos = weakSide.offset(centre);
            if (CityESP.mc.world.getBlockState(pos).getBlock() != Blocks.AIR) {
                directions.put(pos, weakSide);
            }
        }
        BlockPos pos2;
        BlockPos pos3;
        List<BlockPos> square;
        IBlockState holder;
        final Iterator<BlockPos> iterator2;
        BlockPos pos4;
        final List<BlockPos> list;
        directions.forEach((blockPos, blockOffset) -> {
            if (blockOffset == HoleUtil.BlockOffset.DOWN) {
                return;
            }
            else {
                pos2 = blockOffset.left(blockPos.down(this.down.getValue()), this.sides.getValue());
                pos3 = blockOffset.forward(blockOffset.right(blockPos, this.sides.getValue()), this.depth.getValue());
                square = (List<BlockPos>)EntityUtil.getSquare(pos2, pos3);
                holder = CityESP.mc.world.getBlockState(blockPos);
                CityESP.mc.world.setBlockToAir(blockPos);
                square.iterator();
                while (iterator2.hasNext()) {
                    pos4 = iterator2.next();
                    if (this.canPlaceCrystal(pos4.down(), this.ignoreCrystals.getValue()) && DamageUtil.calculateDamage(pos4.getX() + 0.5, (double)pos4.getY(), pos4.getZ() + 0.5, (Entity)player) >= this.minDamage.getValue()) {
                        if (DamageUtil.calculateDamage(pos4.getX() + 0.5, (double)pos4.getY(), pos4.getZ() + 0.5, (Entity)CityESP.mc.player) <= this.maxDamage.getValue()) {
                            list.add(blockPos);
                            break;
                        }
                        else {
                            break;
                        }
                    }
                }
                CityESP.mc.world.setBlockState(blockPos, holder);
                return;
            }
        });
        return cityableSides;
    }
    
    private boolean canPlaceCrystal(final BlockPos blockPos, final boolean ignoreCrystal) {
        final BlockPos boost = blockPos.add(0, 1, 0);
        final BlockPos boost2 = blockPos.add(0, 2, 0);
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(boost, boost2);
        if (CityESP.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && CityESP.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        if (CityESP.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
            return false;
        }
        if (CityESP.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
            return false;
        }
        if (!ignoreCrystal) {
            return CityESP.mc.world.getEntitiesWithinAABB((Class)Entity.class, axisAlignedBB).isEmpty();
        }
        final List<Entity> entityList = (List<Entity>)CityESP.mc.world.getEntitiesWithinAABB((Class)Entity.class, axisAlignedBB);
        entityList.removeIf(entity -> entity instanceof EntityEnderCrystal);
        return entityList.isEmpty();
    }
    
    private void renderBoxes(final List<BlockPos> blockPosList) {
        final String value = this.selectMode.getValue();
        switch (value) {
            case "Closest": {
                blockPosList.stream().min(Comparator.comparing(blockPos -> blockPos.distanceSq((double)(int)CityESP.mc.player.posX, (double)(int)CityESP.mc.player.posY, (double)(int)CityESP.mc.player.posZ))).ifPresent((Consumer<? super Object>)this::renderBox);
                break;
            }
            case "All": {
                for (final BlockPos blockPos2 : blockPosList) {
                    this.renderBox(blockPos2);
                }
                break;
            }
        }
    }
    
    private void renderBox(final BlockPos blockPos) {
        final GSColor gsColor1 = new GSColor(this.color.getValue(), 255);
        final GSColor gsColor2 = new GSColor(this.color.getValue(), 50);
        final String value = this.renderMode.getValue();
        switch (value) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, 1.0, (float)this.width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1.0, (float)this.width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                break;
            }
        }
    }
}
