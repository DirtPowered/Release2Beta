/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.releasetobeta.data.mapping.flattening;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class DataConverter {
    private static Int2IntMap oldToNewBlocksMap = new Int2IntOpenHashMap();
    private static Int2IntMap oldToNewItemsMap = new Int2IntOpenHashMap();
    private static Int2IntMap newToOldItemsMap = new Int2IntOpenHashMap();

    private static ReleaseToBeta main;

    public DataConverter(ReleaseToBeta releaseToBeta) {
        main = releaseToBeta;

        loadMappings();
    }

    public static int getNewBlockId(int blockId, int data) {
        int sum = combineId(blockId, data);
        if (oldToNewBlocksMap.containsKey(sum)) {
            return oldToNewBlocksMap.get(sum);
        } else {
            if (!R2BConfiguration.suppressMappingsErrors)
                main.getLogger().warning("missing mapping for block " + blockId + ":" + data + "(" + sum + ")");

            int combinedNoData = combineId(blockId, 0);
            int noDataBlock = oldToNewBlocksMap.get(combinedNoData);
            return oldToNewBlocksMap.containsKey(combinedNoData) ? noDataBlock : 1;
        }
    }

    public static int getNewItemId(int itemId, int data) {
        int sum = combineId(itemId, data);
        if (oldToNewItemsMap.containsKey(sum)) {
            return oldToNewItemsMap.get(sum);
        } else {
            if (!R2BConfiguration.suppressMappingsErrors)
                main.getLogger().warning("missing mapping for item " + itemId + ":" + data + "(" + sum + ")");
            return 1; //stone
        }
    }

    public static int getOldItemId(int internalItemId) {
        if (newToOldItemsMap.containsKey(internalItemId)) {
            return newToOldItemsMap.get(internalItemId);
        } else {
            if (!R2BConfiguration.suppressMappingsErrors)
                main.getLogger().warning("missing mapping for item (" + internalItemId + ")");
            return 16; //stone
        }
    }

    private static int combineId(int id, int data) {
        return (id * 16) + data;
    }

    private void loadMappings() {
        main.getLogger().info("loading 'legacy <-> 1.15.2' mappings");

        File f;
        try {
            Path p = Paths.get("src/main/resources/mappings.json");
            if (!Files.exists(p)) {
                File mappingTempFile = File.createTempFile("mappings", ".json");
                mappingTempFile.deleteOnExit();

                try (FileOutputStream out = new FileOutputStream(mappingTempFile)) {
                    IOUtils.copy(DataConverter.class.getResourceAsStream("/mappings.json"), out);
                }

                f = mappingTempFile;
            } else {
                f = p.toFile();
            }

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(f));
            JsonObject json = jsonElement.getAsJsonObject();

            JsonArray blocksArray = json.getAsJsonArray("blocks");
            JsonArray itemsArray = json.getAsJsonArray("items");
            int count = 0;

            for (JsonElement block : blocksArray) {
                JsonObject newData = block.getAsJsonObject();

                for (Map.Entry<String, JsonElement> legacyEntry : newData.entrySet()) {
                    JsonElement data = legacyEntry.getValue();

                    int legacyIdData = Integer.parseInt(legacyEntry.getKey());
                    int internalId = data.getAsJsonObject().get("internalId").getAsInt();

                    oldToNewBlocksMap.put(legacyIdData, internalId);
                    count++;
                }
            }

            for (JsonElement item : itemsArray) {
                JsonObject newData = item.getAsJsonObject();

                for (Map.Entry<String, JsonElement> legacyEntry : newData.entrySet()) {
                    JsonElement data = legacyEntry.getValue();

                    int legacyIdData = Integer.parseInt(legacyEntry.getKey());
                    int internalId = data.getAsJsonObject().get("internalId").getAsInt();

                    oldToNewItemsMap.put(legacyIdData, internalId);
                    newToOldItemsMap.put(internalId, legacyIdData);
                    count++;
                }
            }

            main.getLogger().info("loaded " + count + " items and blocks");
        } catch (IOException e) {
            main.getLogger().error("unable to parse mappings.json");
        }
    }

    public void cleanup() {
        oldToNewItemsMap.clear();
        oldToNewBlocksMap.clear();
        newToOldItemsMap.clear();
    }
}
