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
    private final static double SMOOTH_NOISE_FREQ = 0.5882352941176471D;
    private final static double TEMP_GRID = 0.02500000037252903D;
    private final static double HUMID_GRID = 0.05000000074505806D;
    private final static double SMOOTH_GRID = 0.25D;
    private double[] temperature;
    private double[] humidity;
    private double[] smoothTable;
    private NoiseOctaves2D noise1;
    private NoiseOctaves2D noise2;
    private NoiseOctaves2D noise3;

    public void initialize(long worldSeed) {
        noise1 = new NoiseOctaves2D(new Random(worldSeed * 9871L), 4);
        noise2 = new NoiseOctaves2D(new Random(worldSeed * 39811L), 4);
        noise3 = new NoiseOctaves2D(new Random(worldSeed * 543321L), 2);
    }

    private BiomeType getBiomeType(double temperature, double humidity) {
        humidity = humidity * temperature;

        if (temperature < 0.1F) {
            return BiomeType.TUNDRA;
        } else if (humidity < 0.2F) {
            return temperature < 0.5F ? BiomeType.TUNDRA : temperature < 0.95F ? BiomeType.SAVANNA : BiomeType.DESERT;
        } else if (humidity > 0.5F && temperature < 0.7F) {
            return BiomeType.SWAMPLAND;
        } else if (temperature < 0.5F) {
            return BiomeType.TAIGA;
        } else if (temperature < 0.97F) {
            return humidity < 0.35F ? BiomeType.SHRUBLAND : BiomeType.FOREST;
        } else if (humidity < 0.45F) {
            return BiomeType.PLAINS;
        } else {
            return humidity < 0.9F ? BiomeType.SEASONAL_FOREST : BiomeType.RAINFOREST;
        }
    }

    public byte[] getBiomeDataAt(int chunkX, int chunkZ) {
        if (MinecraftVersion.B_1_8_1.isNewerOrEqual(R2BConfiguration.version)) {
            return Utils.getFilledBiomeData();
        }

        return getBiomeData(chunkX * 16, chunkZ * 16);
    }

    private byte[] getBiomeData(int x, int z) {
        byte[] biomeArray = new byte[256];

        temperature = noise1.generateNoise(temperature, x, z, 16, 16, TEMP_GRID, TEMP_GRID, TEMP_NOISE_FREQ);
        humidity = noise2.generateNoise(humidity, x, z, 16, 16, HUMID_GRID, HUMID_GRID, HUMID_NOISE_FREQ);
        smoothTable = noise3.generateNoise(smoothTable, x, z, 16, 16, SMOOTH_GRID, SMOOTH_GRID, SMOOTH_NOISE_FREQ);

        for (int i = 0; i < BIOME_ARRAY_LENGTH; ++i) {
            double var9 = smoothTable[i] * 1.1D + 0.5D;
            double var11 = 0.01D;
            double var13 = 1.0D - var11;

            double rawBiomeTemperature = (temperature[i] * 0.15D + 0.7D) * var13 + var9 * var11;
            double biomeTemperature = 1.0D - (1.0D - rawBiomeTemperature) * (1.0D - rawBiomeTemperature);

            var11 = 0.002D;
            var13 = 1.0D - var11;

            double biomeHumidity = (humidity[i] * 0.15D + 0.5D) * var13 + var9 * var11;

            biomeTemperature = Doubles.constrainToRange(biomeTemperature, 0.0D, 1.0D);
            biomeHumidity = Doubles.constrainToRange(biomeHumidity, 0.0D, 1.0D);

            BiomeType biome = getBiomeType(biomeTemperature, biomeHumidity);
            biomeArray[i] = (byte) biome.getBiomeId();
        }

        return biomeArray;
    }

    enum BiomeType {
        RAINFOREST(21 /* jungle */),
        SWAMPLAND(1 /* plains */),
        SEASONAL_FOREST(4 /* forest */),
        FOREST(4 /* forest */),
        SAVANNA(35 /* savanna */),
        SHRUBLAND(151 /* mutated jungle edge */),
        TAIGA(5 /* taiga */),
        DESERT(2 /* desert */),
        PLAINS(1 /* plains */),
        TUNDRA(12 /* ice plains */);

        @Getter
        private int biomeId;

        BiomeType(int biomeId) {
            this.biomeId = biomeId;
        }
    }
}
