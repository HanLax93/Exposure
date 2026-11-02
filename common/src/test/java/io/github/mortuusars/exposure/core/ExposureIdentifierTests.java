package io.github.mortuusars.exposure.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.mortuusars.exposure.Exposure;
import io.github.mortuusars.exposure.world.level.storage.ExposureData;
import io.github.mortuusars.exposure.world.level.storage.ExposureIdentifier;
import net.minecraft.util.GsonHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExposureIdentifierTests {
    @Test
    void generatedIdsAreValidSavedDataPaths() {
        assertEquals("han-lax_15223021", ExposureIdentifier.createId("Han-Lax", "15223021"));
        assertEquals("han-lax_15223021", ExposureData.storageId("Han-Lax_15223021"));
        assertDoesNotThrow(() -> ExposureData.type("Han-Lax_15223021"));
    }

    @Test
    void canOnlyBeIdOrTexture() {
        ExposureIdentifier id = ExposureIdentifier.id("test");
        assertTrue(id.isId());
        assertTrue(id.getId().isPresent());
        assertNotNull(id.id());
        assertNull(id.texture());
        assertTrue(id.getTexture().isEmpty());

        ExposureIdentifier texture = ExposureIdentifier.texture(Exposure.resource("test"));
        assertTrue(texture.isTexture());
        assertTrue(texture.getTexture().isPresent());
        assertNotNull(texture.texture());
        assertNull(texture.id());
        assertTrue(texture.getId().isEmpty());
    }

    // --

    @Test
    void codecEncodesToSimpleStringIfId() {
        ExposureIdentifier id = ExposureIdentifier.id("test");
        DataResult<JsonElement> encodedResult = ExposureIdentifier.CODEC.encodeStart(JsonOps.INSTANCE, id);

        String jsonString = new Gson().toJson(encodedResult.getOrThrow());

        assertEquals("\"test\"", jsonString);
    }

    @Test
    void codecEncodesToFullIfTexture() {
        ExposureIdentifier texture = ExposureIdentifier.texture(Exposure.resource("test"));
        DataResult<JsonElement> encodedResult = ExposureIdentifier.CODEC.encodeStart(JsonOps.INSTANCE, texture);

        String jsonString = new Gson().toJson(encodedResult.getOrThrow());

        assertEquals("{\"texture\":\"exposure:test\"}", jsonString);
    }

    @Test
    void codecEncodesToEmptyIfEmpty() {
        ExposureIdentifier empty = ExposureIdentifier.EMPTY;
        DataResult<JsonElement> encodedResult = ExposureIdentifier.CODEC.encodeStart(JsonOps.INSTANCE, empty);

        String jsonString = new Gson().toJson(encodedResult.getOrThrow());

        assertEquals("\"\"", jsonString);
    }

    // --

    @Test
    void simpleStringDecodesToId() {
        String jsonString = "\"test\"";

        DataResult<Pair<ExposureIdentifier, JsonElement>> decodedResult =
                ExposureIdentifier.CODEC.decode(JsonOps.INSTANCE, new Gson().fromJson(jsonString, JsonPrimitive.class));

        ExposureIdentifier decodedId = decodedResult.getOrThrow().getFirst();

        assertEquals(ExposureIdentifier.id("test"), decodedId);
    }

    @Test
    void fullIdDecodesToId() {
        String jsonString = "{\"id\":\"test\"}";

        JsonObject parse = GsonHelper.parse(jsonString);
        DataResult<Pair<ExposureIdentifier, JsonElement>> decodedResult =
                ExposureIdentifier.CODEC.decode(JsonOps.INSTANCE, parse);

        ExposureIdentifier decodedId = decodedResult.getOrThrow().getFirst();

        assertEquals(ExposureIdentifier.id("test"), decodedId);
    }

    @Test
    void fullTextureDecodesToTexture() {
        String jsonString = "{\"texture\":\"exposure:test\"}";

        JsonObject parse = GsonHelper.parse(jsonString);
        DataResult<Pair<ExposureIdentifier, JsonElement>> decodedResult =
                ExposureIdentifier.CODEC.decode(JsonOps.INSTANCE, parse);

        ExposureIdentifier decodedId = decodedResult.getOrThrow().getFirst();

        assertEquals(ExposureIdentifier.texture(Exposure.resource("test")), decodedId);
    }
}
