package com.sharif.thunder.audio;

import com.github.natanbc.lavadsp.ConverterPcmAudioFilter;
import com.github.natanbc.lavadsp.volume.VolumeConverter;
import com.github.natanbc.lavadsp.util.DoubleToDoubleFunction;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;

/**
 * Updates the effect volume, with a multiplier ranging from 0 to 5.
 */
public class VolumePcmAudioFilter extends ConverterPcmAudioFilter<VolumeConverter> {
    private volatile float volume = 1.0f;

    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount, int bufferSize) {
        super(new VolumeConverter(), downstream, channelCount, bufferSize);
    }

    public VolumePcmAudioFilter(FloatPcmAudioFilter downstream, int channelCount) {
        super(new VolumeConverter(), downstream, channelCount);
    }

    /**
     * Returns the volume multiplier. 1.0 means unmodified.
     *
     * @return The current volume.
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume multiplier. 1.0 means unmodified.
     *
     * @param volume Volume to use.
     * @return {@code this}, for chaining calls.
     */
    public VolumePcmAudioFilter setVolume(float volume) {
        getConverter().setVolume(volume);
        this.volume = volume;
        return this;
    }

    /**
     * Updates the volume multiplier, using a function that accepts the current value
     * and returns a new value.
     *
     * @param function Function used to map the depth.
     * @return {@code this}, for chaining calls
     */
    // public VolumePcmAudioFilter updateVolume(DoubleToDoubleFunction function) {
    //     return setVolume(function.apply(volume));
    // }
}