package net.runelite.client.plugins.nex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NexConstant {
    public static final Set<Integer> FUMUS_IDS = new HashSet<>(List.of(11283));

    public static final Set<Integer> UMBRA_IDS = new HashSet<>(List.of(11284));

    public static final Set<Integer> CRUOR_IDS = new HashSet<>(List.of(11285));

    public static final Set<Integer> GLACIES_IDS = new HashSet<>(List.of(11286));

    public static final Set<Integer> NEX_IDS = new HashSet<>(Arrays.asList(11279, 11278, 11280, 11281, 11282));

    public static final Set<Integer> BLOOD_REAVER_IDS = new HashSet<>(List.of(11294));

    public static final NexDialogue NEX_LINE_AT_LAST = new NexDialogue(NexPhase.SPAWN, "AT LAST!", 27);

    public static final NexDialogue NEX_LINE_FUMUS_SUMMON = new NexDialogue(NexPhase.SPAWN, "Fumus!", 22);

    public static final NexDialogue NEX_LINE_UMBRA_SUMMON = new NexDialogue(NexPhase.SPAWN, "Umbra!", 18);

    public static final NexDialogue NEX_LINE_CRUOR_SUMMON = new NexDialogue(NexPhase.SPAWN, "Cruor!", 14);

    public static final NexDialogue NEX_LINE_GLACIES_SUMMON = new NexDialogue(NexPhase.SPAWN, "Glacies!", 10);

    public static final NexDialogue NEX_LINE_START_SMOKE = new NexDialogue(NexPhase.SMOKE, "Fill my soul with smoke!", 6);

    public static final NexDialogue NEX_LINE_VIRUS = new NexDialogue(NexPhase.SMOKE, "Let the virus flow through you!", -1);

    public static final String NEX_LINE_COUGH = "*Cough*";

    public static final NexDialogue NEX_LINE_ATTACK_FUMUS = new NexDialogue(NexPhase.SMOKE, "Fumus, don't fail me!", -1);

    public static final NexDialogue NEX_LINE_START_SHADOW = new NexDialogue(NexPhase.SHADOW, "Darken my shadow!", 6);

    public static final NexDialogue NEX_LINE_SHADOW = new NexDialogue(NexPhase.SHADOW, "Fear the shadow!", -1);

    public static final NexDialogue NEX_LINE_ATTACK_UMBRA = new NexDialogue(NexPhase.SHADOW, "Umbra, don't fail me!", -1);

    public static final NexDialogue NEX_LINE_START_BLOOD = new NexDialogue(NexPhase.BLOOD, "Flood my lungs with blood!", 6);

    public static final NexDialogue NEX_LINE_REAVERS = new NexDialogue(NexPhase.BLOOD, "A siphon will solve this!", -1);

    public static final NexDialogue NEX_LINE_SACRIFICE = new NexDialogue(NexPhase.BLOOD, "I demand a blood sacrifice!", -1);

    public static final NexDialogue NEX_LINE_ATTACK_CRUOR = new NexDialogue(NexPhase.BLOOD, "Cruor, don't fail me!", -1);

    public static final NexDialogue NEX_LINE_START_ICE = new NexDialogue(NexPhase.ICE, "Infuse me with the power of ice!", 6);

    public static final NexDialogue NEX_LINE_ICE_SHARD = new NexDialogue(NexPhase.ICE, "Contain this!", -1);

    public static final NexDialogue NEX_LINE_ATTACK_GLACIES = new NexDialogue(NexPhase.ICE, "Glacies, don't fail me!", -1);

    public static final NexDialogue NEX_LINE_START_ZAROS = new NexDialogue(NexPhase.ZAROS, "NOW, THE POWER OF ZAROS!", 6);

    public static final NexDialogue NEX_LINE_WRATH = new NexDialogue(NexPhase.ZAROS, "Taste my wrath!", -1);

    public static final NexDialogue[] NEX_ALL_DIALOGUES = new NexDialogue[] {
            NEX_LINE_AT_LAST, NEX_LINE_FUMUS_SUMMON, NEX_LINE_UMBRA_SUMMON, NEX_LINE_CRUOR_SUMMON, NEX_LINE_GLACIES_SUMMON, NEX_LINE_START_SMOKE, NEX_LINE_VIRUS, NEX_LINE_ATTACK_FUMUS, NEX_LINE_START_SHADOW, NEX_LINE_SHADOW,
            NEX_LINE_ATTACK_UMBRA, NEX_LINE_START_BLOOD, NEX_LINE_REAVERS, NEX_LINE_SACRIFICE, NEX_LINE_ATTACK_CRUOR, NEX_LINE_START_ICE, NEX_LINE_ICE_SHARD, NEX_LINE_ATTACK_GLACIES, NEX_LINE_START_ZAROS, NEX_LINE_WRATH };

    public static final NexTargetChange[] NEX_TARGETS = new NexTargetChange[] { new NexTargetChange(NEX_IDS, new NexDialogue[] {
            NEX_LINE_AT_LAST, NEX_LINE_FUMUS_SUMMON, NEX_LINE_UMBRA_SUMMON, NEX_LINE_CRUOR_SUMMON, NEX_LINE_GLACIES_SUMMON, NEX_LINE_START_SMOKE,
            NEX_LINE_START_SHADOW, NEX_LINE_START_BLOOD, NEX_LINE_START_ICE, NEX_LINE_START_ZAROS }),
            new NexTargetChange(FUMUS_IDS, new NexDialogue[] { NEX_LINE_ATTACK_FUMUS }),
            new NexTargetChange(UMBRA_IDS, new NexDialogue[] { NEX_LINE_ATTACK_UMBRA }),
            new NexTargetChange(CRUOR_IDS, new NexDialogue[] { NEX_LINE_ATTACK_CRUOR }),
            new NexTargetChange(GLACIES_IDS, new NexDialogue[] { NEX_LINE_ATTACK_GLACIES })
    };

    public static final Set<Integer> SHADOW_GAME_OBJECT_IDS = new HashSet<>(List.of(42942));

    public static final String BLOOD_SACRIFICE_MSG = "<col=e00a19>Nex has marked you for a blood sacrifice! RUN!</col>";
}
