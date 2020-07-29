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

package com.github.dirtpowered.releasetobeta.data.biome;

import com.github.dirtpowered.betaprotocollib.data.version.MinecraftVersion;
import com.github.dirtpowered.releasetobeta.configuration.R2BConfiguration;
import com.github.dirtpowered.releasetobeta.data.biome.oldnoise.NoiseOctaves2D;
import com.github.dirtpowered.releasetobeta.utils.Utils;
import com.google.common.primitives.Doubles;
import lombok.Getter;

import java.util.Random;

public class OldChunkData {
    private final static int BIOME_ARRAY_LENGTH = 256;
    private final static double TEMP_NOISE_FREQ = 0.25D;
    private final static double HUMID_NOISE_FREQ = 0.3333333333333333D;
    private final static double TEMP_GRID = 0.02500000037252903D;
    private final static double HUMID_GRID = 0.05000000074505806D;
    private double[] temperature;
    private double[] humidity;
    private NoiseOctaves2D noise1;
    private NoiseOctaves2D noise2;

    public void initialize(long worldSeed) {
        noise1 = new NoiseOctaves2D(new Random(worldSeed * 9871L), 4);
        noise2 = new NoiseOctaves2D(new Random(worldSeed * 39811L), 4);
    }

    private BiomeType getBiomeType(double temperature, double humidity) {
        humidity = humidity * temperature;

        if (temperature < 0.1D) {
            return BiomeType.TUNDRA;
        } else if (humidity < 0.2D) {
            return temperature < 0.5D ? BiomeType.TUNDRA : temperature < 0.95D ? BiomeType.SAVANNA : BiomeType.DESERT;
        } else if (humidity > 0.5D && temperature < 0.7D) {
            return BiomeType.SWAMP;
        } else if (temperature < 0.5D) {
            return BiomeType.TAIGA;
        } else if (temperature < 0.97D) {
            return humidity < 0.35D ? BiomeType.SHRUBLAND : BiomeType.FOREST;
        } else if (humidity < 0.45D) {
            return BiomeType.PLAINS;
        } else {
            return humidity < 0.9D ? BiomeType.SEASONAL_FOREST : BiomeType.RAINFOREST;
        }
    }

    public int[] getBiomeDataAt(int chunkX, int chunkZ) {
        if (MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version)) {
            return Utils.getFilledBiomeData();
        }

        return getBiomeData(chunkX * 16, chunkZ * 16);
    }

    private int[] getBiomeData(int x, int z) {
        int[] biomeArray = new int[1024];

        temperature = noise1.generateNoise(temperature, x, z, 16, 16, TEMP_GRID, TEMP_GRID, TEMP_NOISE_FREQ);
        humidity = noise2.generateNoise(humidity, x, z, 16, 16, HUMID_GRID, HUMID_GRID, HUMID_NOISE_FREQ);

        for (int i = 0; i < BIOME_ARRAY_LENGTH; ++i) {
            double rawBiomeTemperature = (temperature[i] * 0.15D + 0.7D) * 0.99D + 0.0105;
            double biomeTemperature = 1.0D - (1.0D - rawBiomeTemperature) * (1.0D - rawBiomeTemperature);

            double biomeHumidity = (humidity[i] * 0.15D + 0.5D) * 0.99D + 0.0021;

            biomeTemperature = Doubles.constrainToRange(biomeTemperature, 0.0D, 1.0D);
            biomeHumidity = Doubles.constrainToRange(biomeHumidity, 0.0D, 1.0D);

            BiomeType biome = getBiomeType(biomeTemperature, biomeHumidity);

            // translate to new format
            for (int xOffset = 0; xOffset < 4; ++xOffset) {
                for (int zOffset = 0; zOffset < 4; ++zOffset) {
                    int index = xOffset * 4 | zOffset;

                    if (biome.getBiomeId() != -127) {
                        biomeArray[index] = biome.getBiomeId();
                    } else {
                        biomeArray[index] = 1;
                    }
                }
            }
        }

        return biomeArray;
    }

    enum BiomeType {
        RAINFOREST(21),
        SWAMP(6),
        SEASONAL_FOREST(12),
        FOREST(4),
        SAVANNA(35),
        SHRUBLAND(35),
        TAIGA(5),
        DESERT(2),
        PLAINS(1),
        TUNDRA(12);

        @Getter
        private int biomeId;

        BiomeType(int biomeId) {
            this.biomeId = biomeId;
        }
    }
}
