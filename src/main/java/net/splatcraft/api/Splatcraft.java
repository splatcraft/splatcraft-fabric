package net.splatcraft.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entrypoint helper for Splatcraft.
 */
public interface Splatcraft {
    String MOD_ID   = "splatcraft";
    String MOD_NAME = "Splatcraft";
    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
