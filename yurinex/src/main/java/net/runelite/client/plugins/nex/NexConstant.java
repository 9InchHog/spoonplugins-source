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

    public static final NexDialogue NEX_LINE_AT_LAST = new NexDialogue(NexPhase.SPAWN, "AT LAST!", "Nex: <col=0000ff>AT LAST!</col>", 27);

    public static final NexDialogue NEX_LINE_FUMUS_SUMMON = new NexDialogue(NexPhase.SPAWN, "Fumus!", "Nex: <col=0000ff>Fumus!</col>", 22);

    public static final NexDialogue NEX_LINE_UMBRA_SUMMON = new NexDialogue(NexPhase.SPAWN, "Umbra!", "Nex: <col=0000ff>Umbra!</col>", 18);

    public static final NexDialogue NEX_LINE_CRUOR_SUMMON = new NexDialogue(NexPhase.SPAWN, "Cruor!", "Nex: <col=0000ff>Cruor!</col>", 14);

    public static final NexDialogue NEX_LINE_GLACIES_SUMMON = new NexDialogue(NexPhase.SPAWN, "Glacies!", "Nex: <col=0000ff>Glacies!</col>", 10);

    public static final NexDialogue NEX_LINE_START_SMOKE = new NexDialogue(NexPhase.SMOKE, "Fill my soul with smoke!", "Nex: <col=0000ff>Fill my soul with smoke!</col>", 6);

    public static final NexDialogue NEX_LINE_VIRUS = new NexDialogue(NexPhase.SMOKE, "Let the virus flow through you!", "Nex: <col=0000ff>Let the virus flow through you!</col>", -1);

    public static final NexDialogue NEX_LINE_THERE_IS = new NexDialogue(NexPhase.SMOKE, "There is...", "Nex: <col=0000ff>There is...</col>", -1);

    public static final NexDialogue NEX_LINE_NO_ESCAPE = new NexDialogue(NexPhase.SMOKE, "NO ESCAPE!", "Nex: <col=0000ff>NO ESCAPE!</col>", -1);

    public static final String NEX_LINE_COUGH = "*Cough*";

    public static final NexDialogue NEX_LINE_ATTACK_FUMUS = new NexDialogue(NexPhase.SMOKE, "Fumus, don't fail me!", "Nex: <col=0000ff>Fumus, don't fail me!</col>", -1);

    public static final NexDialogue NEX_LINE_START_SHADOW = new NexDialogue(NexPhase.SHADOW, "Darken my shadow!", "Nex: <col=0000ff>Darken my shadow!</col>", 6);

    public static final NexDialogue NEX_LINE_SHADOW = new NexDialogue(NexPhase.SHADOW, "Fear the shadow!", "Nex: <col=0000ff>Fear the shadow!</col>", -1);

    public static final NexDialogue NEX_LINE_EMBRACE = new NexDialogue(NexPhase.SHADOW, "Embrace darkness!", "Nex: <col=0000ff>Embrace darkness!</col>", -1);

    public static final NexDialogue NEX_LINE_ATTACK_UMBRA = new NexDialogue(NexPhase.SHADOW, "Umbra, don't fail me!", "Nex: <col=0000ff>Umbra, don't fail me!</col>", -1);

    public static final NexDialogue NEX_LINE_START_BLOOD = new NexDialogue(NexPhase.BLOOD, "Flood my lungs with blood!", "Nex: <col=0000ff>Flood my lungs with blood!</col>", 6);

    public static final NexDialogue NEX_LINE_REAVERS = new NexDialogue(NexPhase.BLOOD, "A siphon will solve this!", "Nex: <col=0000ff>A siphon will solve this!</col>", -1);

    public static final NexDialogue NEX_LINE_SACRIFICE = new NexDialogue(NexPhase.BLOOD, "I demand a blood sacrifice!", "Nex: <col=0000ff>I demand a blood sacrifice!</col>", -1);

    public static final NexDialogue NEX_LINE_ATTACK_CRUOR = new NexDialogue(NexPhase.BLOOD, "Cruor, don't fail me!", "Nex: <col=0000ff>Cruor, don't fail me!</col>", -1);

    public static final NexDialogue NEX_LINE_START_ICE = new NexDialogue(NexPhase.ICE, "Infuse me with the power of ice!", "Nex: <col=0000ff>Infuse me with the power of ice!</col>", 6);

    public static final NexDialogue NEX_LINE_ICE_SHARD = new NexDialogue(NexPhase.ICE, "Contain this!", "Nex: <col=0000ff>Contain this!</col>", -1);

    public static final NexDialogue NEX_LINE_ICE_CAGE = new NexDialogue(NexPhase.ICE, "Die now, in a prison of ice!", "Nex: <col=0000ff>Die now, in a prison of ice!</col>", -1);

    public static final NexDialogue NEX_LINE_ATTACK_GLACIES = new NexDialogue(NexPhase.ICE, "Glacies, don't fail me!", "Nex: <col=0000ff>Glacies, don't fail me!</col>", -1);

    public static final NexDialogue NEX_LINE_START_ZAROS = new NexDialogue(NexPhase.ZAROS, "NOW, THE POWER OF ZAROS!", "Nex: <col=0000ff>NOW, THE POWER OF ZAROS!</col>", 6);

    public static final NexDialogue NEX_LINE_WRATH = new NexDialogue(NexPhase.ZAROS, "Taste my wrath!", "Nex: <col=0000ff>Taste my wrath!</col>", -1);

    public static final NexDialogue[] NEX_ALL_DIALOGUES = new NexDialogue[] {
            NEX_LINE_AT_LAST, NEX_LINE_FUMUS_SUMMON, NEX_LINE_UMBRA_SUMMON, NEX_LINE_CRUOR_SUMMON, NEX_LINE_GLACIES_SUMMON,
            NEX_LINE_START_SMOKE, NEX_LINE_VIRUS, NEX_LINE_THERE_IS, NEX_LINE_NO_ESCAPE, NEX_LINE_ATTACK_FUMUS,
            NEX_LINE_START_SHADOW, NEX_LINE_SHADOW, NEX_LINE_EMBRACE, NEX_LINE_ATTACK_UMBRA, NEX_LINE_START_BLOOD,
            NEX_LINE_REAVERS, NEX_LINE_SACRIFICE, NEX_LINE_ATTACK_CRUOR, NEX_LINE_START_ICE, NEX_LINE_ICE_SHARD,
            NEX_LINE_ICE_CAGE, NEX_LINE_ATTACK_GLACIES, NEX_LINE_START_ZAROS, NEX_LINE_WRATH };

    public static final NexTargetChange[] NEX_TARGETS = new NexTargetChange[] { new NexTargetChange(NEX_IDS, new NexDialogue[] { NEX_LINE_AT_LAST, NEX_LINE_FUMUS_SUMMON, NEX_LINE_UMBRA_SUMMON, NEX_LINE_CRUOR_SUMMON, NEX_LINE_GLACIES_SUMMON, NEX_LINE_START_SMOKE, NEX_LINE_START_SHADOW, NEX_LINE_START_BLOOD, NEX_LINE_START_ICE, NEX_LINE_START_ZAROS }), new NexTargetChange(FUMUS_IDS, new NexDialogue[] { NEX_LINE_ATTACK_FUMUS }), new NexTargetChange(UMBRA_IDS, new NexDialogue[] { NEX_LINE_ATTACK_UMBRA }), new NexTargetChange(CRUOR_IDS, new NexDialogue[] { NEX_LINE_ATTACK_CRUOR }), new NexTargetChange(GLACIES_IDS, new NexDialogue[] { NEX_LINE_ATTACK_GLACIES }) };

    public static final Set<Integer> SHADOW_GAME_OBJECT_IDS = new HashSet<>(List.of(42942));

    public static final Set<Integer> ICE_NEX_SELF_CAGE = new HashSet<>(List.of(42943));

    public static final Set<Integer> ICE_CAGE_SHARD = new HashSet<>(List.of(42944));

    public static final Set<Integer> ICE_CAGE_PLACEHOLDER = new HashSet<>(List.of(26209));

    public static final String BLOOD_SACRIFICE_MSG = "<col=e00a19>Nex has marked you for a blood sacrifice! RUN!</col>";
}
