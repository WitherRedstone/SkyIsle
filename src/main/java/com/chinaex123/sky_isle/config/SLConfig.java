package com.chinaex123.sky_isle.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SLConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_STRUCTURE_GENERATION;
    public static final ModConfigSpec.ConfigValue<String> OVERWORLD_STRUCTURE_NAME;
    public static final ModConfigSpec.IntValue STRUCTURE_POS_X;
    public static final ModConfigSpec.IntValue STRUCTURE_POS_Y;
    public static final ModConfigSpec.IntValue STRUCTURE_POS_Z;
    public static final ModConfigSpec.ConfigValue<String> NETHER_STRUCTURE_NAME;
    public static final ModConfigSpec.IntValue NETHER_POS_X;
    public static final ModConfigSpec.IntValue NETHER_POS_Y;
    public static final ModConfigSpec.IntValue NETHER_POS_Z;
    public static final ModConfigSpec.BooleanValue BLAZE_ROOM_ONLY;
    public static final ModConfigSpec.BooleanValue ENABLE_END_SPIKES;
    public static final ModConfigSpec.BooleanValue DEFAULT_WORLD_TYPE;
    public static final ModConfigSpec.ConfigValue<String> SPAWN_DIMENSION;
    public static final ModConfigSpec.ConfigValue<String> NETHER_RESPAWN_STRUCTURE_NAME;
    public static final ModConfigSpec.ConfigValue<String> END_RESPAWN_STRUCTURE_NAME;
    public static final ModConfigSpec.IntValue END_POS_X;
    public static final ModConfigSpec.IntValue END_POS_Y;
    public static final ModConfigSpec.IntValue END_POS_Z;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("通用配置").push("Common Config");
        ENABLE_STRUCTURE_GENERATION = builder
                .comment("是否启用结构生成（村庄、要塞等）")
                .comment("Enable structure generation (villages, fortresses, etc.)")
                .define("enableStructureGeneration", true);
        DEFAULT_WORLD_TYPE = builder
                .comment("是否将 Sky Isle 设为默认世界类型")
                .comment("Set Sky Isle as the default world type")
                .define("defaultWorldType", true);

        builder.comment("重生的维度").push("Respawn dimension");
        SPAWN_DIMENSION = builder
                .comment("玩家重生的维度: overworld, the_nether 或 the_end")
                .comment("Player respawn dimension: overworld, the_nether or the_end")
                .define("respawnDimension", "overworld");
        NETHER_RESPAWN_STRUCTURE_NAME = builder
                .comment(
                        "设置在下界重生时的结构文件名（不含.nbt后缀）",
                        "结构文件放置在 config/sky_isle/structures/the_nether/ 目录下"
                )
                .comment(
                        "Nether respawn structure file name (without the .nbt extension)",
                        "Place structure files in the config/sky_isle/structures/the_nether/ directory"
                )
                .define("netherRespawnStructureName", "the_nether_respawn");
        END_RESPAWN_STRUCTURE_NAME = builder
                .comment(
                        "设置在末地重生时的结构文件名（不含.nbt后缀）",
                        "结构文件放置在 config/sky_isle/structures/the_end/ 目录下"
                )
                .comment(
                        "End respawn structure file name (without the .nbt extension)",
                        "Place structure files in the config/sky_isle/structures/the_end/ directory"
                )
                .define("endRespawnStructureName", "the_end_respawn");
        builder.pop();

        builder.pop();


        builder.comment("结构模板配置").push("Structure");

        builder.comment("主世界").push("Overworld");
        OVERWORLD_STRUCTURE_NAME = builder
                .comment(
                        "主世界结构文件名（不含.nbt后缀）",
                        "结构文件放置在 config/sky_isle/structures/overworld/ 目录下"
                )
                .comment(
                        "Overworld structure file name (without the .nbt extension)",
                        "Place structure files in the config/sky_isle/structures/overworld/ directory"
                )
                .define("overworldStructureName", "overworld_oak");
        builder.comment("结构放置位置").push("Position");
        STRUCTURE_POS_X = builder
                .comment("X坐标")
                .comment("X coordinate")
                .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        STRUCTURE_POS_Y = builder
                .comment("Y坐标")
                .comment("Y coordinate")
                .defineInRange("y", 64, Integer.MIN_VALUE, Integer.MAX_VALUE);
        STRUCTURE_POS_Z = builder
                .comment("Z坐标")
                .comment("Z coordinate")
                .defineInRange("z", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        builder.pop();
        builder.pop();

        builder.comment("下界").push("Nether");
        NETHER_STRUCTURE_NAME = builder
                .comment(
                        "下界结构文件名（不含.nbt后缀）",
                        "结构文件放置在 config/sky_isle/structures/the_nether/ 目录下"
                )
                .comment(
                        "Nether structure file name (without the .nbt extension)",
                        "Place structure files in the config/sky_isle/structures/the_nether/ directory"
                )
                .define("netherStructureName", "the_nether_default");
        builder.comment("结构放置位置").push("Position");
        NETHER_POS_X = builder
                .comment("X坐标")
                .comment("X coordinate")
                .defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        NETHER_POS_Y = builder
                .comment("Y坐标")
                .comment("Y coordinate")
                .defineInRange("y", 64, Integer.MIN_VALUE, Integer.MAX_VALUE);
        NETHER_POS_Z = builder
                .comment("Z坐标")
                .comment("Z coordinate")
                .defineInRange("z", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        BLAZE_ROOM_ONLY = builder
                .comment("是否让下界要塞只生成烈焰人刷怪房")
                .comment("Enable blaze room generation in the nether fortress")
                .define("blazeRoomOnly", true);
        builder.pop();
        builder.pop();

        builder.comment("末地").push("End");
        builder.comment("结构放置位置").push("Position");
        END_POS_X = builder
                .comment("X坐标")
                .comment("X coordinate")
                .defineInRange("x", 2500, Integer.MIN_VALUE, Integer.MAX_VALUE);
        END_POS_Y = builder
                .comment("Y坐标")
                .comment("Y coordinate")
                .defineInRange("y", 64, Integer.MIN_VALUE, Integer.MAX_VALUE);
        END_POS_Z = builder
                .comment("Z坐标")
                .comment("Z coordinate")
                .defineInRange("z", 2500, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ENABLE_END_SPIKES = builder
                .comment("主岛是否生成末地黑曜石柱")
                .comment("Enable end spikes generation in the main island")
                .define("enableEndSpikes", true);
        builder.pop();
        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }
}