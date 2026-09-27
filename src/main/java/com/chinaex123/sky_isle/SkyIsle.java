package com.chinaex123.sky_isle;

import com.chinaex123.sky_isle.config.SLConfig;
import com.chinaex123.sky_isle.worldgen.StructureLoader;
import com.chinaex123.sky_isle.worldgen.dimensions.EndSkyIsland;
import com.chinaex123.sky_isle.worldgen.dimensions.NetherSkyIsland;
import com.chinaex123.sky_isle.worldgen.dimensions.OverworldSkyIsland;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(SkyIsle.MODID)
public class SkyIsle {
    public static final String MODID = "sky_isle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SkyIsle(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(SkyIsle::onRegister);
        modContainer.registerConfig(ModConfig.Type.COMMON, SLConfig.SPEC);
        StructureLoader.ensureDirectories();
    }

    public static void onRegister(RegisterEvent event) {
        event.register(Registries.CHUNK_GENERATOR, helper -> {
            helper.register(Identifier.fromNamespaceAndPath(SkyIsle.MODID, "overworld_sky_island"), OverworldSkyIsland.CODEC);
            helper.register(Identifier.fromNamespaceAndPath(SkyIsle.MODID, "nether_sky_island"), NetherSkyIsland.CODEC);
            helper.register(Identifier.fromNamespaceAndPath(SkyIsle.MODID, "end_sky_island"), EndSkyIsland.CODEC);
        });
    }
}