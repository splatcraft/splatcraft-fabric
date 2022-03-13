package net.splatcraft.impl.util;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.Identifier;
import net.splatcraft.api.inkcolor.InkColor;
import net.splatcraft.api.inkcolor.InkType;
import net.splatcraft.api.registry.SplatcraftRegistries;

public class TrackedDataUtil {
    public static InkColor inkColor(DataTracker tracker, TrackedData<String> type) {
        String data = tracker.get(type);
        Identifier id = Identifier.tryParse(data);
        return SplatcraftRegistries.INK_COLOR.get(id);
    }

    public static void inkColor(DataTracker tracker, TrackedData<String> type, InkColor data) {
        String value = data.toString();
        tracker.set(type, value);
    }

    public static InkType inkType(DataTracker tracker, TrackedData<Integer> type) {
        Integer data = tracker.get(type);
        return InkType.safeIndexOf(data);
    }

    public static void inkType(DataTracker tracker, TrackedData<Integer> type, InkType data) {
        int value = data.ordinal();
        tracker.set(type, value);
    }
}
