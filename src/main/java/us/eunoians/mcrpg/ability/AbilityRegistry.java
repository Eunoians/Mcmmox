package us.eunoians.mcrpg.ability;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

/**
 * The central {@link AbilityRegistry} for all {@link us.eunoians.mcrpg.McRPG} abilities!
 *
 * @author OxKitsuen
 */
public class AbilityRegistry {

    /**
     * A map that contains all registered abilities.
     */
    private final HashMap<NamespacedKey, Function<AbilityCreationData, ? extends BaseAbility>> registeredAbilities;

    /**
     * Construct a new {@link AbilityRegistry}
     */
    public AbilityRegistry() {
        this.registeredAbilities = new HashMap<>();
    }

    /**
     * Register a skill to the {@link AbilityRegistry}.
     *
     * @param key          the id of the ability
     * @param constructor the implementation of the ability itself.
     * @return the ability that got registered
     */
    public void registerAbility(@NotNull NamespacedKey key, Function<AbilityCreationData, ? extends BaseAbility> constructor) {
        if (getAbility(key).isPresent())
            throw new IllegalArgumentException("An ability with id: \"" + key.toString() + "\" is already registered!");
        registeredAbilities.put(key, constructor);
    }

    /**
     * Get the registered skill using the skill id (as namespaced key).
     *
     * @param abilityKey the key of the skill
     * @return an {@link Optional} containing the skill
     */
    public Optional<Function<AbilityCreationData, ? extends BaseAbility>> getAbility(NamespacedKey abilityKey) {
        if (!registeredAbilities.containsKey(abilityKey)) return Optional.empty();
        return Optional.of(registeredAbilities.get(abilityKey));
    }

    /**
     * Create a new {@link Ability} instance using the specified {@link AbilityCreationData}.
     *
     * @param abilityKey the id of the ability
     * @param creationData the creation data for the ability
     *
     * @return the instantiated {@link Ability}
     *
     * @throws IllegalArgumentException whenever the specified {@code abilityKey} isn't a valid ability!
     */
    @NotNull
    public BaseAbility createAbility (NamespacedKey abilityKey, AbilityCreationData creationData) {
        Function<AbilityCreationData, ? extends BaseAbility> constructor = getAbility(abilityKey).orElse(null);
        if (constructor == null) throw new IllegalArgumentException("An ability with id: " + abilityKey.toString() + " doesn't exist!");

        return constructor.apply(creationData);
    }
}
