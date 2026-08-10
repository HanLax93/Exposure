package io.github.mortuusars.exposure.core.warehouse.server;

import io.github.mortuusars.exposure.world.level.storage.ExposureIdentifier;
import io.github.mortuusars.exposure.world.camera.frame.Frame;
import io.github.mortuusars.exposure.world.level.storage.ExposureFrameHistory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FrameHistoryTests {
    @Test
    void encoding() {
        UUID randomUUID = UUID.randomUUID();

        ExposureFrameHistory history = new ExposureFrameHistory(new HashMap<>());
        history.add(randomUUID, Frame.EMPTY.toMutable().setIdentifier(ExposureIdentifier.id("test")).toImmutable());

        Tag tag = ExposureFrameHistory.CODEC.encodeStart(NbtOps.INSTANCE, history).getOrThrow();

        assertInstanceOf(CompoundTag.class, tag);
        CompoundTag compoundTag = (CompoundTag) tag;
        assertTrue(compoundTag.contains(randomUUID.toString()));
        ListTag frames = compoundTag.getListOrEmpty(randomUUID.toString());
        assertEquals(1, frames.size());
        Frame encodedFrame = Frame.CODEC.parse(NbtOps.INSTANCE, frames.getFirst()).getOrThrow();
        assertEquals("test", encodedFrame.identifier().id());
    }

    @Test
    void decoding() {
        UUID randomUUID = UUID.randomUUID();

        Frame frame = Frame.EMPTY.toMutable().setIdentifier(ExposureIdentifier.id("test")).toImmutable();

        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        listTag.add(Frame.CODEC.encode(frame, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
        tag.put(randomUUID.toString(), listTag);

        ExposureFrameHistory decodedHistory = ExposureFrameHistory.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        List<Frame> frames = decodedHistory.getFramesOf(randomUUID);

        assertEquals("test", frames.getFirst().identifier().id());
    }

    @Test
    void limit() {
        ExposureFrameHistory history = new ExposureFrameHistory(new HashMap<>());

        UUID uuid = UUID.randomUUID();

        for (int i = 0; i < ExposureFrameHistory.LIMIT + 4; i++) {
            Frame frame = Frame.EMPTY.toMutable().setIdentifier(ExposureIdentifier.id("test-" + i)).toImmutable();
            history.add(uuid, frame);
        }

        assertTrue(history.getFramesOf(uuid).size() <= ExposureFrameHistory.LIMIT);
    }
}
