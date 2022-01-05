package net.runelite.client.plugins.spoondemonicgorilla;

import net.runelite.api.Player;

class PendingGorillaAttack {
    private DemonicGorilla attacker;

    private DemonicGorilla.AttackStyle attackStyle;

    private Player target;

    private int finishesOnTick;

    DemonicGorilla getAttacker() {
        return this.attacker;
    }

    DemonicGorilla.AttackStyle getAttackStyle() {
        return this.attackStyle;
    }

    Player getTarget() {
        return this.target;
    }

    int getFinishesOnTick() {
        return this.finishesOnTick;
    }

    PendingGorillaAttack(DemonicGorilla attacker, DemonicGorilla.AttackStyle attackStyle, Player target, int finishesOnTick) {
        this.attacker = attacker;
        this.attackStyle = attackStyle;
        this.target = target;
        this.finishesOnTick = finishesOnTick;
    }
}
