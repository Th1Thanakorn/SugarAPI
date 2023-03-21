package com.thana.sugarapi.common.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlUtil;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.*;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(DebugScreenOverlay.class)
public abstract class MixinDebugScreenOverlay {

    @Shadow @Final private static Map<Heightmap.Types, String> HEIGHTMAP_NAMES;
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected HitResult block;
    @Shadow protected HitResult liquid;
    @Shadow @Nullable private ChunkPos lastPos;
    @Shadow protected abstract Level getLevel();
    @Shadow protected abstract String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> entry);
    @Shadow @Nullable protected abstract String getServerChunkStats();
    @Shadow public abstract void clearChunkCache();
    @Shadow protected abstract LevelChunk getClientChunk();
    @Shadow @Nullable protected abstract LevelChunk getServerChunk();
    @Shadow @Nullable protected abstract ServerLevel getServerLevel();
    @Shadow
    private static String printBiome(Holder<Biome> holder) {
        return null;
    }

    private static final ChatFormatting REGISTRY_FORMAT = ChatFormatting.YELLOW;
    private static final ChatFormatting TAG_REGISTRY_FORMAT = ChatFormatting.GOLD;
    private static final ChatFormatting JAVA_VERSION_FORMAT = ChatFormatting.AQUA;
    private static final ChatFormatting TARGETED_FORMAT = ChatFormatting.AQUA;
    private static final ChatFormatting COMMON_VALUE_FORMAT = ChatFormatting.GREEN;
    private static final ChatFormatting MOOD_FORMAT = ChatFormatting.AQUA;
    private static final ChatFormatting LOADING_CONDITION_FORMAT = ChatFormatting.RED;
    private static final ChatFormatting DIMENSION_FORMAT = ChatFormatting.DARK_AQUA;
    private static final ChatFormatting DELIMITER = ChatFormatting.GOLD;
    private static final ChatFormatting LIGHT_SKY = ChatFormatting.YELLOW;
    private static final ChatFormatting LIGHT_BLOCK = ChatFormatting.GOLD;
    private static final ChatFormatting INT_VALUE_FORMAT = ChatFormatting.YELLOW;
    private static final ChatFormatting SIMPLE_STRING_FORMAT = ChatFormatting.YELLOW;
    private static final ChatFormatting RESET = ChatFormatting.WHITE;
    private static final ChatFormatting DIRECTION = ChatFormatting.LIGHT_PURPLE;
    private static final ChatFormatting DAY_COUNT = ChatFormatting.AQUA;
    private static final ChatFormatting ROTATION = ChatFormatting.AQUA;


    /**
     * @author Thana
     * @reason Renew F3 debug screen format
     */
    @Overwrite
    public List<String> getGameInformation() {
        IntegratedServer integratedserver = this.minecraft.getSingleplayerServer();
        Connection connection = this.minecraft.getConnection().getConnection();
        float f = connection.getAverageSentPackets();
        float f1 = connection.getAverageReceivedPackets();
        String s;
        if (integratedserver != null) {
            s = String.format("Integrated server @ %.0f ms ticks, %.0f tx, %.0f rx", integratedserver.getAverageTickTime(), f, f1);
        } else {
            s = String.format("\"%s\" server, %.0f tx, %.0f rx", this.minecraft.player.getServerBrand(), f, f1);
        }
        BlockPos blockpos = this.minecraft.getCameraEntity().blockPosition();
        if (this.minecraft.showOnlyReducedInfo()) {
            return formatDelimiter(Lists.newArrayList("Minecraft " + COMMON_VALUE_FORMAT + SharedConstants.getCurrentVersion().getName() + RESET + " (" + SIMPLE_STRING_FORMAT + this.minecraft.getLaunchedVersion() + DELIMITER + "/" + SIMPLE_STRING_FORMAT + ClientBrandRetriever.getClientModName() + RESET + ")", COMMON_VALUE_FORMAT + this.minecraft.fpsString, s, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + INT_VALUE_FORMAT + this.minecraft.particleEngine.countParticles() + RESET + ". T: " + INT_VALUE_FORMAT + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats(), "", String.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15)));
        }
        else {
            Entity entity = this.minecraft.getCameraEntity();
            Direction direction = entity.getDirection();
            String s1 = switch (direction) {
                case NORTH -> ChatFormatting.YELLOW + "Towards negative Z" + RESET;
                case SOUTH -> ChatFormatting.YELLOW + "Towards positive Z" + RESET;
                case WEST -> ChatFormatting.YELLOW + "Towards negative X" + RESET;
                case EAST -> ChatFormatting.YELLOW + "Towards positive X" + RESET;
                default -> LOADING_CONDITION_FORMAT + "Invalid" + RESET;
            };
            ChunkPos chunkpos = new ChunkPos(blockpos);
            if (!Objects.equals(this.lastPos, chunkpos)) {
                this.lastPos = chunkpos;
                this.clearChunkCache();
            }
            Level level = this.getLevel();
            LongSet longset = level instanceof ServerLevel ? ((ServerLevel)level).getForcedChunks() : LongSets.EMPTY_SET;
            List<String> list = Lists.newArrayList("Minecraft " + COMMON_VALUE_FORMAT + SharedConstants.getCurrentVersion().getName() + RESET + " (" + SIMPLE_STRING_FORMAT + this.minecraft.getLaunchedVersion() + DELIMITER + "/" + SIMPLE_STRING_FORMAT + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType()) + RESET + ")", COMMON_VALUE_FORMAT + this.minecraft.fpsString, s, this.minecraft.levelRenderer.getChunkStatistics(), this.minecraft.levelRenderer.getEntityStatistics(), "P: " + INT_VALUE_FORMAT + this.minecraft.particleEngine.countParticles() + RESET + ". T: " + INT_VALUE_FORMAT + this.minecraft.level.getEntityCount(), this.minecraft.level.gatherChunkSourceStats());
            String s2 = this.getServerChunkStats();
            if (s2 != null) {
                list.add(s2);
            }
            list.add(DIMENSION_FORMAT.toString() + this.minecraft.level.dimension().location() + ChatFormatting.WHITE + " FC: " + INT_VALUE_FORMAT + longset.size());
            list.add("");
            list.add("XYZ:" + String.format(Locale.ROOT, TARGETED_FORMAT + " %.3f" + RESET + " / " + TARGETED_FORMAT + "%.5f" + RESET + " / " + TARGETED_FORMAT + "%.3f" + RESET, this.minecraft.getCameraEntity().getX(), this.minecraft.getCameraEntity().getY(), this.minecraft.getCameraEntity().getZ()));
            list.add("Block:" + TARGETED_FORMAT + String.format(" %d %d %d [%d %d %d]", blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
            list.add("Chunk:" + TARGETED_FORMAT + String.format(" %d %d %d [%d %d in r.%d.%d.mca]", chunkpos.x, SectionPos.blockToSectionCoord(blockpos.getY()), chunkpos.z, chunkpos.getRegionLocalX(), chunkpos.getRegionLocalZ(), chunkpos.getRegionX(), chunkpos.getRegionZ()));
            list.add(String.format(Locale.ROOT, "Facing: " + DIRECTION + "%s" + RESET + " (%s) (" + ROTATION + "%.1f" + RESET + " /" + ROTATION + "%.1f" + RESET + ")", direction, s1, Mth.wrapDegrees(entity.getYRot()), Mth.wrapDegrees(entity.getXRot())));
            LevelChunk levelchunk = this.getClientChunk();
            if (levelchunk.isEmpty()) {
                list.add(LOADING_CONDITION_FORMAT + "Waiting for chunk...");
            } else {
                int i = this.minecraft.level.getChunkSource().getLightEngine().getRawBrightness(blockpos, 0);
                int j = this.minecraft.level.getBrightness(LightLayer.SKY, blockpos);
                int k = this.minecraft.level.getBrightness(LightLayer.BLOCK, blockpos);
                list.add("Client Light: " + ChatFormatting.GREEN + i + RESET + DELIMITER + " (" + LIGHT_SKY + j + " sky" + DELIMITER + ", " + LIGHT_BLOCK + k + " block" + DELIMITER + ")");
                LevelChunk levelchunk1 = this.getServerChunk();
                StringBuilder stringbuilder = new StringBuilder("CH");

                for (Heightmap.Types heightmap$types : Heightmap.Types.values()) {
                    if (heightmap$types.sendToClient()) {
                        stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$types)).append(": ").append(INT_VALUE_FORMAT).append(levelchunk.getHeight(heightmap$types, blockpos.getX(), blockpos.getZ())).append(RESET);
                    }
                }

                list.add(stringbuilder.toString());
                stringbuilder.setLength(0);
                stringbuilder.append("SH");

                for (Heightmap.Types heightmap$types1 : Heightmap.Types.values()) {
                    if (heightmap$types1.keepAfterWorldgen()) {
                        stringbuilder.append(" ").append(HEIGHTMAP_NAMES.get(heightmap$types1)).append(": ");
                        if (levelchunk1 != null) {
                            stringbuilder.append(INT_VALUE_FORMAT).append(levelchunk1.getHeight(heightmap$types1, blockpos.getX(), blockpos.getZ())).append(RESET);
                        } else {
                            stringbuilder.append(LOADING_CONDITION_FORMAT).append("??").append(RESET);
                        }
                    }
                }

                list.add(stringbuilder.toString());
                if (blockpos.getY() >= this.minecraft.level.getMinBuildHeight() && blockpos.getY() < this.minecraft.level.getMaxBuildHeight()) {
                    list.add("Biome: " + REGISTRY_FORMAT + printBiome(this.minecraft.level.getBiome(blockpos)));
                    long l = 0L;
                    float f2 = 0.0F;
                    if (levelchunk1 != null) {
                        f2 = level.getMoonBrightness();
                        l = levelchunk1.getInhabitedTime();
                    }

                    DifficultyInstance difficultyinstance = new DifficultyInstance(level.getDifficulty(), level.getDayTime(), l, f2);
                    list.add(String.format(Locale.ROOT, "Local Difficulty: %.2f // %.2f (Day " + DAY_COUNT + "%d" + RESET + ")", difficultyinstance.getEffectiveDifficulty(), difficultyinstance.getSpecialMultiplier(), this.minecraft.level.getDayTime() / 24000L));
                }
            }

            ServerLevel serverlevel = this.getServerLevel();
            if (serverlevel != null) {
                ServerChunkCache serverchunkcache = serverlevel.getChunkSource();
                ChunkGenerator chunkgenerator = serverchunkcache.getGenerator();
                chunkgenerator.addDebugScreenInfo(list, blockpos);
                Climate.Sampler climate$sampler = chunkgenerator.climateSampler();
                BiomeSource biomesource = chunkgenerator.getBiomeSource();
                biomesource.addDebugInfo(list, blockpos, climate$sampler);
                NaturalSpawner.SpawnState naturalspawner$spawnstate = serverchunkcache.getLastSpawnState();
                if (naturalspawner$spawnstate != null) {
                    Object2IntMap<MobCategory> object2intmap = naturalspawner$spawnstate.getMobCategoryCounts();
                    int i1 = naturalspawner$spawnstate.getSpawnableChunkCount();
                    list.add("SC: "+ INT_VALUE_FORMAT + i1 + RESET + ", " + Stream.of(MobCategory.values()).map((category) -> Character.toUpperCase(category.getName().charAt(0)) + ": " + INT_VALUE_FORMAT + object2intmap.getInt(category) + RESET).collect(Collectors.joining(", ")));
                } else {
                    list.add("SC: " + LOADING_CONDITION_FORMAT + "N/A");
                }
            }

            PostChain postchain = this.minecraft.gameRenderer.currentEffect();
            if (postchain != null) {
                list.add("Shader: " + postchain.getName());
            }

            list.add(this.minecraft.getSoundManager().getDebugString() + String.format(" (Mood %s%%", MOOD_FORMAT.toString() + Math.round(this.minecraft.player.getCurrentMood() * 100.0F)) + ChatFormatting.WHITE + ")");
            return formatDelimiter(list);
        }
    }

    /**
     * @author Thana
     * @reason Renew F3 debug screen format
     */
    @Overwrite
    public List<String> getSystemInformation() {
        long i = Runtime.getRuntime().maxMemory();
        long j = Runtime.getRuntime().totalMemory();
        long k = Runtime.getRuntime().freeMemory();
        long l = j - k;
        List<String> list = Lists.newArrayList(String.format("Java: %s %sbit", JAVA_VERSION_FORMAT + System.getProperty("java.version"), (this.minecraft.is64Bit() ? 64 : 32)), String.format("Mem: % " + "2d%%" + " %03d/%03dMB", l * 100L / i, bytesToMegabytes(l), bytesToMegabytes(i)), String.format("Allocated: % " + "2d%%" + "%03dMB", j * 100L / i, bytesToMegabytes(j)), "", String.format("CPU: %s", GlUtil.getCpuInfo()), "", String.format("Display: %dx%d (%s)", Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight(), GlUtil.getVendor()), GlUtil.getRenderer(), GlUtil.getOpenGLVersion());
        if (!this.minecraft.showOnlyReducedInfo()) {
            if (this.block.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos = ((BlockHitResult) this.block).getBlockPos();
                BlockState blockstate = this.minecraft.level.getBlockState(blockpos);
                list.add("");
                list.add(ChatFormatting.UNDERLINE + "Targeted Block: " + TARGETED_FORMAT + ChatFormatting.UNDERLINE + (blockpos.getX() + ", " + blockpos.getY() + ", " + blockpos.getZ()));
                list.add(REGISTRY_FORMAT + String.valueOf(Registry.BLOCK.getKey(blockstate.getBlock())));
                for (Map.Entry<Property<?>, Comparable<?>> entry : blockstate.getValues().entrySet()) {
                    list.add(this.getPropertyValueString(entry));
                }
                blockstate.getTags().map((p_205379_) -> TAG_REGISTRY_FORMAT + "#" + p_205379_.location()).forEach(list::add);
            }
            if (this.liquid.getType() == HitResult.Type.BLOCK) {
                BlockPos blockpos1 = ((BlockHitResult) this.liquid).getBlockPos();
                FluidState fluidstate = this.minecraft.level.getFluidState(blockpos1);
                list.add("");
                list.add(ChatFormatting.UNDERLINE + "Targeted Fluid: " + TARGETED_FORMAT + ChatFormatting.UNDERLINE + (blockpos1.getX() + ", " + blockpos1.getY() + ", " + blockpos1.getZ()));
                list.add(REGISTRY_FORMAT + String.valueOf(Registry.FLUID.getKey(fluidstate.getType())));
                for (Map.Entry<Property<?>, Comparable<?>> entry1 : fluidstate.getValues().entrySet()) {
                    list.add(this.getPropertyValueString(entry1));
                }
                fluidstate.getTags().map((p_205365_) -> TAG_REGISTRY_FORMAT + "#" + p_205365_.location()).forEach(list::add);
            }
            Entity entity = this.minecraft.crosshairPickEntity;
            if (entity != null) {
                list.add("");
                list.add(ChatFormatting.UNDERLINE + "Targeted Entity");
                list.add(REGISTRY_FORMAT + String.valueOf(Registry.ENTITY_TYPE.getKey(entity.getType())));
                entity.getType().builtInRegistryHolder().tags().forEach(t -> list.add(TAG_REGISTRY_FORMAT + "#" + t.location()));
            }
        }
        return list;
    }

    private static long bytesToMegabytes(long bytes) {
        return bytes / 1024L / 1024L;
    }

    private static List<String> formatDelimiter(List<String> list) {
        ArrayList<String> formattedList = new ArrayList<>();
        list.forEach((text) -> {
            text = text.replaceAll("\\(", DELIMITER + "(" + RESET);
            text = text.replaceAll("\\)", DELIMITER + ")" + RESET);
            text = text.replaceAll("/", DELIMITER + "/" + RESET);
            text = text.replaceAll(",", DELIMITER + "," + RESET);
            formattedList.add(text);
        });
        return formattedList;
    }
}