package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.command.ClearInkColorCommand;
import com.cibernet.splatcraft.command.FillInkColorCommand;
import com.cibernet.splatcraft.command.ScanTurfCommand;
import com.cibernet.splatcraft.command.SetInkColorCommand;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class StringConstants {
    public static final String TEXT_NO_INK = createText("no_ink");
    public static final String TEXT_NO_INK_SET = createText("no_ink_set");

    public static final String COMMAND_CLEARINK_SUCCESS = createCommandSuccess(ClearInkColorCommand.ID);
    public static final String COMMAND_FILLINKCOLOR_SUCCESS = createCommandSuccess(FillInkColorCommand.ID);
    public static final String COMMAND_SCANTURF_SUCCESS = createCommandSuccess(ScanTurfCommand.ID);
    public static final String COMMAND_SETINKCOLOR_SUCCESS_SELF = createCommandSuccess(SetInkColorCommand.ID, "self");
    public static final String COMMAND_SETINKCOLOR_SUCCESS_OTHER = createCommandSuccess(SetInkColorCommand.ID, "other");

    public static final String DAMAGE_SOURCE_SPLAT = createDottedId("splat");
    public static final String DAMAGE_SOURCE_ROLL = createDottedId("roll");
    public static final List<String> DAMAGE_SOURCES_ALL = new ArrayList<>();

    private static String createText(String text) {
        return "text." + Splatcraft.MOD_ID + "." + text;
    }

    private static String createCommand(Identifier command, String append) {
        return "commands." + command + "." + append;
    }
    private static String createCommandSuccess(Identifier command) {
        return createCommand(command, "success");
    }
    private static String createCommandSuccess(Identifier command, String append) {
        return createCommandSuccess(command) + "." + append;
    }

    private static String createDottedId(String id) {
        return Splatcraft.MOD_ID + "." + id;
    }

    static {
        DAMAGE_SOURCES_ALL.add(DAMAGE_SOURCE_SPLAT);
        DAMAGE_SOURCES_ALL.add(DAMAGE_SOURCE_ROLL);
    }
}
