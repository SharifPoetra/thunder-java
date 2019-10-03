package com.sharif.thunder.audio;

import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.google.gson.annotations.SerializedName;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.ResamplingPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import com.sharif.thunder.Main;
import com.sharif.thunder.queue.FairQueue;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.OtherUtil;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class AudioHandler extends AudioEventAdapter implements AudioSendHandler {
    private final FairQueue<QueuedTrack> queue = new FairQueue<QueuedTrack>();
    private final List<AudioTrack> defaultQueue = new LinkedList<AudioTrack>();
    private final Set<String> votes = new HashSet<String>();
    private JDA jda;
    static AudioConfiguration configuration;
    private final PlayerManager manager;
    private final long guildId;
    private Guild guild;

    private AudioFrame lastFrame;

    @Getter private long announcingChannel;
    @Setter private AudioPlayer audioPlayer;
    @Getter private float nightcore = 1.0f;
    @Getter private boolean karaoke = false;
    @Getter private boolean vaporwave = false;
    @Getter private boolean repeating = false;
    private float karaokeWidth = 100f;
    private float karaokeBand = 220f;
    private float karaokeLevel = 1f;
    private static final float[] BASS_BOOST = {
        0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f, -0.1f,
        -0.1f, -0.1f
    };
    @Getter private boolean bassboost = false;
    private boolean deleteMessage = false;
    @Getter private int pitch = 0;
    @Getter private float tempo = 1.0f;
    private CompletableFuture<Void> task;

    @SerializedName("high-quality-nightcore")
    public boolean highQualityNightcore = false;

    protected AudioHandler(PlayerManager manager, Guild guild, AudioPlayer player) {
        this.manager = manager;
        this.guildId = guild.getIdLong();
        this.guild = guild;
        this.audioPlayer = player;
    }

    // Setters
    public void seek(long position) {
        if (audioPlayer.getPlayingTrack() != null)
            audioPlayer.getPlayingTrack().setPosition(position);
    }

    public void setKaraoke(boolean enabled) {
        this.karaoke = enabled;
        updateFilters(getPlayingTrack());
    }

    public void setKaraokeWidth(float width) {
        this.karaokeWidth = width;
        updateFilters(getPlayingTrack());
    }

    public void setKaraokeBand(float band) {
        this.karaokeBand = band;
        updateFilters(getPlayingTrack());
    }

    public void setKaraokeLevel(float level) {
        this.karaokeLevel = level;
        updateFilters(getPlayingTrack());
    }

    public void setVaporwave(boolean enabled) {
        this.vaporwave = enabled;
        updateFilters(getPlayingTrack());
    }

    public void setTempo(float tempo) {
        this.tempo = tempo;
        updateFilters(getPlayingTrack());
    }

    public void setNightcore(float speed) {
        if (speed <= 0.1 || speed > 3)
            throw new IllegalArgumentException(
                    "Nightcore speed out of range (0.1-3)!"); // prevent zero division and other
        // weird shit

        speed = Math.abs(speed);
        nightcore = speed;
        updateFilters(getPlayingTrack());
    }

    public void setBassboost(boolean bassboost) {
        this.bassboost = bassboost;
        updateFilters(getPlayingTrack());
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
        updateFilters(getPlayingTrack());
    }

    public boolean setRepeating(boolean repeating) {
        return this.repeating = repeating;
    }

    public void setAnnouncingChannel(long channelId) {
        this.announcingChannel = channelId;
    }

    // Getters
    public boolean isBassboost() {
        return bassboost;
    }

    public boolean isVaporwave() {
        return vaporwave;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public boolean isKaraoke() {
        return karaoke;
    }

    public boolean isMusicPlaying(JDA jda) {
        return guild(jda).getSelfMember().getVoiceState().inVoiceChannel()
                && audioPlayer.getPlayingTrack() != null;
    }

    // Methods
    public int addTrackToFront(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else {
            queue.addAt(0, qtrack);
            return 0;
        }
    }

    public int addTrack(QueuedTrack qtrack) {
        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(qtrack.getTrack());
            return -1;
        } else return queue.add(qtrack);
    }

    public FairQueue<QueuedTrack> getQueue() {
        return queue;
    }

    public void stopTrack() {
        this.deleteMessage = true;
        audioPlayer.stopTrack();
    }

    public void stopAndClear() {
        this.deleteMessage = true;
        queue.clear();
        defaultQueue.clear();
        audioPlayer.stopTrack();
    }

    public Set<String> getVotes() {
        return votes;
    }

    public AudioPlayer getPlayer() {
        return audioPlayer;
    }

    public AudioTrack getPlayingTrack() {
        return audioPlayer.getPlayingTrack();
    }

    public long getRequester() {
        if (audioPlayer.getPlayingTrack() == null
                || audioPlayer.getPlayingTrack().getUserData(Long.class) == null) return 0;
        return audioPlayer.getPlayingTrack().getUserData(Long.class);
    }

    public boolean playFromDefault() {
        if (!defaultQueue.isEmpty()) {
            audioPlayer.playTrack(defaultQueue.remove(0));
            return true;
        }
        if (!manager.getBot().getConfig().getStay()) manager.getBot().closeAudioConnection(guildId);
        return true;
    }

    // Audio Events
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (isRepeating()) {
            queue.add(
                    new QueuedTrack(
                            track.makeClone(),
                            track.getUserData(Long.class) == null
                                    ? 0L
                                    : track.getUserData(Long.class)));
        }

        if (queue.isEmpty()) {
            if (!playFromDefault()) {
                if (!manager.getBot().getConfig().getStay()) System.out.println(guildId);
                guild.getAudioManager().closeAudioConnection();
            }
        } else {
            QueuedTrack qt = queue.pull();
            player.playTrack(qt.getTrack());
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(guild.getSelfMember().getColor());
        eb.setAuthor(Main.PLAY_EMOJI + " Start playing");
        User u = guild.getJDA().getUserById(getRequester());
        try {
            eb.setDescription(
                    "**["
                            + track.getInfo().title
                            + "]("
                            + track.getInfo().uri
                            + ")** ["
                            + u.getAsMention()
                            + "]");
        } catch (Exception e) {
            eb.setDescription("**" + track.getInfo().title + "** [" + u.getAsMention() + "]");
        }
        TextChannel channel = guild.getTextChannelById(announcingChannel);
        channel.sendMessage(eb.build())
                .queue(
                        (m) -> {
                            OtherUtil.deleteMessageAfter(m, track.getDuration());
                        });
        votes.clear();
        updateFilters(getPlayingTrack());
    }

    // Formatting
    public Message getNowPlaying(JDA jda) {
        if (isMusicPlaying(jda)) {
            Guild guild = guild(jda);
            AudioTrack track = audioPlayer.getPlayingTrack();
            MessageBuilder mb = new MessageBuilder();
            mb.append(
                    FormatUtil.filterEveryone(
                            manager.getBot().getConfig().getSuccess()
                                    + " **Now Playing in "
                                    + guild.getSelfMember().getVoiceState().getChannel().getName()
                                    + "...**"));
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(guild.getSelfMember().getColor());
            if (getRequester() != 0) {
                User u = guild.getJDA().getUserById(getRequester());
                if (u == null) eb.setAuthor("Unknown (ID:" + getRequester() + ")", null, null);
                else
                    eb.setAuthor(
                            u.getName() + "#" + u.getDiscriminator(),
                            null,
                            u.getEffectiveAvatarUrl());
            }

            try {
                eb.setTitle(track.getInfo().title, track.getInfo().uri);
            } catch (Exception e) {
                eb.setTitle(track.getInfo().title);
            }

            if (track instanceof YoutubeAudioTrack && manager.getBot().getConfig().useNPImages()) {
                eb.setThumbnail(
                        "https://img.youtube.com/vi/" + track.getIdentifier() + "/mqdefault.jpg");
            }

            if (track.getInfo().author != null && !track.getInfo().author.isEmpty())
                eb.setFooter("Source: " + track.getInfo().author, null);

            double progress =
                    (double) audioPlayer.getPlayingTrack().getPosition() / track.getDuration();
            eb.setDescription(
                    (audioPlayer.isPaused() ? Main.PAUSE_EMOJI : Main.PLAY_EMOJI)
                            + " "
                            + FormatUtil.progressBar(progress)
                            + " `["
                            + FormatUtil.formatTime(track.getPosition())
                            + "/"
                            + FormatUtil.formatTime(track.getDuration())
                            + "]` "
                            + FormatUtil.volumeIcon(audioPlayer.getVolume()));

            return mb.setEmbed(eb.build()).build();
        } else return null;
    }

    public Message getNoMusicPlaying(JDA jda) {
        Guild guild = guild(jda);
        return new MessageBuilder()
                .setContent(
                        FormatUtil.filterEveryone(
                                manager.getBot().getConfig().getSuccess() + " **Now Playing...**"))
                .setEmbed(
                        new EmbedBuilder()
                                .setTitle("No music playing")
                                .setDescription(
                                        Main.STOP_EMOJI
                                                + " "
                                                + FormatUtil.progressBar(-1)
                                                + " "
                                                + FormatUtil.volumeIcon(audioPlayer.getVolume()))
                                .setColor(guild.getSelfMember().getColor())
                                .build())
                .build();
    }

    // filter stuff
    public boolean hasFiltersEnabled() {
        return bassboost || karaoke || vaporwave || nightcore != 1 || tempo != 1 || pitch != 0;
    }

    public void updateFilters(AudioTrack track) {
        if (hasFiltersEnabled()) {
            shouldRebuild = true;
            audioPlayer.setFilterFactory(this::getFiltersOrRebuild);
        } else {
            audioPlayer.setFilterFactory(null);
        }
    }

    public void resetFilters() {
        this.nightcore = 1.0f;
        this.tempo = 1.0f;
        this.bassboost = false;
        this.vaporwave = false;
        this.karaoke = false;
        this.pitch = 0;
        shouldRebuild = false;
        audioPlayer.setFilterFactory(null);
    }

    private volatile List<AudioFilter> lastChain;
    private volatile boolean shouldRebuild;

    private List<AudioFilter> getFiltersOrRebuild(
            AudioTrack audioTrack,
            AudioDataFormat audioDataFormat,
            UniversalPcmAudioFilter downstream) {
        if (shouldRebuild) {
            lastChain = buildChain(audioTrack, audioDataFormat, downstream);
            shouldRebuild = false;
        }

        return lastChain;
    }

    private List<AudioFilter> buildChain(
            AudioTrack audioTrack, AudioDataFormat format, UniversalPcmAudioFilter downstream) {
        List<AudioFilter> filterList = new ArrayList<>();
        FloatPcmAudioFilter filter = downstream;

        if (canChangeSpeed(getPlayingTrack())
                && (vaporwave || nightcore != 1 || tempo != 1 || pitch != 0)) {
            if (nightcore != 1 && highQualityNightcore) {
                filter =
                        new ResamplingPcmAudioFilter(
                                configuration,
                                format.channelCount,
                                filter,
                                format.sampleRate,
                                (int) (format.sampleRate / nightcore));
                filterList.add(filter);
            }

            TimescalePcmAudioFilter timescale =
                    new TimescalePcmAudioFilter(filter, format.channelCount, format.sampleRate);

            if (!highQualityNightcore) {
                timescale.setRate(nightcore);
            }

            if (vaporwave) {
                timescale.setSpeed(tempo * 0.5f).setPitchSemiTones(pitch - 7.0);
            } else {
                timescale.setSpeed(tempo).setPitchSemiTones(pitch);
            }

            filterList.add(timescale);
            filter = timescale;
        }

        if (karaoke) {
            filter =
                    new KaraokePcmAudioFilter(filter, format.channelCount, format.sampleRate)
                            .setLevel(karaokeLevel)
                            .setFilterBand(karaokeBand)
                            .setFilterWidth(karaokeWidth);

            filterList.add(filter);
        }

        if (bassboost) {
            EqualizerFactory equalizer = new EqualizerFactory();
            for (int i = 0; i < BASS_BOOST.length; i++) {
                equalizer.setGain(i, BASS_BOOST[i] + 0.1f);
            }
            audioPlayer.setFilterFactory(equalizer);
        }

        Collections.reverse(filterList);
        return filterList;
    }

    private boolean canChangeSpeed(AudioTrack track) {
        if (track == null) return false;
        if (track.getSourceManager() instanceof YoutubeAudioSourceManager
                && track.getDuration() == Long.MAX_VALUE) return false;
        return !(track.getSourceManager() instanceof TwitchStreamAudioSourceManager);
    }

    // Audio send handler methods
    @Override
    public boolean canProvide() {
        if (lastFrame == null) lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        if (lastFrame == null) lastFrame = audioPlayer.provide();
        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;
        return ByteBuffer.wrap(data);
    }

    @Override
    public boolean isOpus() {
        return true;
    }

    // Private methods
    private Guild guild(JDA jda) {
        return jda.getGuildById(guildId);
    }
}
