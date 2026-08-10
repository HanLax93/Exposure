package io.github.mortuusars.exposure.advancements.predicate;

import com.mojang.serialization.Codec;
import io.github.mortuusars.exposure.util.ExtraData;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

/**
 * Performs partial NBT matching against frame extra data.
 */
public record ExtraDataPredicate(ExtraData data) {
    public static final Codec<ExtraDataPredicate> CODEC = TagParser.LENIENT_CODEC.xmap(
            tag -> new ExtraDataPredicate(new ExtraData(tag)),
            predicate -> predicate.data.toTag());
    public static final StreamCodec<ByteBuf, ExtraDataPredicate> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(
            tag -> new ExtraDataPredicate(new ExtraData(tag)),
            predicate -> predicate.data.toTag());

    public boolean matches(@Nullable ExtraData other) {
        return other != null && compareNbt(other.toTag());
    }

    private boolean compareNbt(@Nullable Tag other) {
        CompoundTag expected = data.toTag();
        if (other == null) {
            return false;
        } else if (other instanceof CompoundTag actual) {
            if (actual.size() < expected.size()) {
                return false;
            } else {
                for (String key : expected.keySet()) {
                    if (!NbtUtils.compareNbt(expected.get(key), actual.get(key), true)) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return expected.equals(other);
        }
    }
}
