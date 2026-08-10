package io.github.mortuusars.exposure.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.PostChainConfig;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostEffectResourcesTests {
    private static final List<String> EFFECTS = List.of(
            "black_tint", "blue_filter", "brown_tint", "bsod", "crisp",
            "cyan_tint", "gray_tint", "green_filter", "invert", "light_blue_tint",
            "light_gray_tint", "lime_tint", "magenta_tint", "orange_tint", "pink_tint",
            "purple_tint", "red_filter", "white_tint", "yellow_tint");

    @Test
    void allPostEffectsUseTheMinecraft262Schema() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        for (String effect : EFFECTS) {
            String path = "assets/exposure/post_effect/" + effect + ".json";
            try (var stream = loader.getResourceAsStream(path)) {
                assertNotNull(stream, "Missing post effect: " + path);
                JsonElement json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                PostChainConfig.CODEC.parse(JsonOps.INSTANCE, json)
                        .getOrThrow(error -> new IllegalArgumentException(path + ": " + error));
            }
        }
    }
}
