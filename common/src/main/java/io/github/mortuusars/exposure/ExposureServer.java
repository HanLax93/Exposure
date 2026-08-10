package io.github.mortuusars.exposure;

import com.google.common.base.Preconditions;
import io.github.mortuusars.exposure.world.level.storage.ExposureRepository;
import io.github.mortuusars.exposure.world.level.storage.ExposureFrameHistory;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

public class ExposureServer {
    private static MinecraftServer server;
    private static ExposureRepository vault;
    private static ExposureFrameHistory frameHistory;

    public static boolean debugHighlightEntitiesInFrame = false;

    public static void init(MinecraftServer server) {
        ExposureRepository newVault = new ExposureRepository(server);
        ExposureFrameHistory newFrameHistory = ExposureFrameHistory.loadOrCreate(server);

        ExposureServer.vault = newVault;
        ExposureServer.frameHistory = newFrameHistory;
        ExposureServer.server = server;
    }

    public static boolean isInitializedFor(MinecraftServer server) {
        return ExposureServer.server == server && vault != null && frameHistory != null;
    }

    public static void shutdown(MinecraftServer server) {
        if (ExposureServer.server != server) {
            return;
        }

        ExposureServer.server = null;
        vault = null;
        frameHistory = null;
    }

    // --

    public static ExposureRepository exposureRepository() {
        return ensureInitialized(vault);
    }

    public static ExposureFrameHistory frameHistory() {
        return ensureInitialized(frameHistory);
    }

    // --

    private static <T> T ensureInitialized(@Nullable T obj) {
        Preconditions.checkNotNull(obj, "Cannot get a field in ExposureServer: server is not initialized yet.");
        return obj;
    }
}
