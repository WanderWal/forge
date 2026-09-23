package forge.game.staticability;

import forge.game.Game;
import forge.game.card.Card;
import forge.game.zone.ZoneType;

public class StaticAbilityCombatDamageAbsolutePower {

    public static boolean combatDamageAbsolutePower(final Card card)  {
        final Game game = card.getGame();
        for (final Card ca : game.getCardsIn(ZoneType.STATIC_ABILITIES_SOURCE_ZONES)) {
            for (final StaticAbility stAb : ca.getStaticAbilities()) {
                if (!stAb.checkConditions(StaticAbilityMode.CombatDamageAbsolutePower)) {
                    continue;
                }
                if (stAb.matchesValidParam("ValidCard", card)) {
                    return true;
                }
            }
        }
        return false;
    }
}
