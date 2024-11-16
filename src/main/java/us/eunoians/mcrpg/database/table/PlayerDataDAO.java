package us.eunoians.mcrpg.database.table;

import com.diamonddagger590.mccore.database.Database;
import com.diamonddagger590.mccore.database.table.impl.TableVersionHistoryDAO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.eunoians.mcrpg.entity.player.McRPGPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A DAO used to store data regarding a player's specific data that doesn't really belong in another table
 */
public class PlayerDataDAO {

    private static final String TABLE_NAME = "mcrpg_player_data";
    private static final int CURRENT_TABLE_VERSION = 1;

    /**
     * Attempts to create a new table for this DAO provided that the table does not already exist.
     *
     * @param connection The {@link Connection} to use to attempt the creation
     * @param database   The {@link Database} being used to attempt to create the table
     * @return {@code true} if a new table was made or {@code false} otherwise.
     */
    public static boolean attemptCreateTable(@NotNull Connection connection, @NotNull Database database) {
        //Check to see if the table already exists
        if (database.tableExists(connection, TABLE_NAME)) {
            return false;
        }

        /*****
         ** Table Description:
         ** Contains player data that doesn't have another table to be located
         *
         *
         * uuid is the {@link java.util.UUID} of the player being stored
         * ability_points is the amount of ability points the player has left to spend
         **
         ** Reasoning for structure:
         ** PK is the `uuid` field, as each player only has one uuid
         *****/
        try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE `" + TABLE_NAME + "`" +
                "(" +
                "`uuid` varchar(36) NOT NULL," +
                "`ability_points` int(11) NOT NULL DEFAULT 1," +
                "PRIMARY KEY (`uuid`)" +
                ");")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Checks to see if there are any version differences from the live version of this SQL table and then current version.
     * <p>
     * If there are any differences, it will iteratively go through and update through each version to ensure the database is
     * safe to run queries on.
     *
     * @param connection The {@link Connection} that will be used to run the changes
     */
    public static void updateTable(@NotNull Connection connection) {
        int lastStoredVersion = TableVersionHistoryDAO.getLatestVersion(connection, TABLE_NAME);
        if (lastStoredVersion >= CURRENT_TABLE_VERSION) {
            return;
        }

        //Adds table to our tracking
        if (lastStoredVersion == 0) {
            TableVersionHistoryDAO.setTableVersion(connection, TABLE_NAME, 1);
            lastStoredVersion = 1;
        }
    }

    /**
     * Gets a {@link PlayerDataSnapshot} containing all of the player's data for misc information such as redeemable exp. If
     * the provided {@link UUID} doesn't have any data, an empty {@link PlayerDataSnapshot} will be returned instead with null or defaulted 0 values.
     *
     * @param connection The {@link Connection} to use to get the player data
     * @param uuid       The {@link UUID} of the player whose data is being obtained
     * @return A {@link PlayerDataSnapshot} that has all of the player's misc
     * data. If the provided {@link UUID} doesn't have any data, an empty {@link PlayerDataSnapshot} will be returned instead with null or defaulted 0 values.
     */
    @NotNull
    public static PlayerDataSnapshot getPlayerData(@NotNull Connection connection, @NotNull UUID uuid) {
        PlayerDataSnapshot playerDataSnapshot = new PlayerDataSnapshot(uuid);
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE uuid = ?;")) {
            preparedStatement.setString(1, uuid.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String partyUUIDString = resultSet.getString("party_uuid");
                    UUID partyUUID = partyUUIDString.equalsIgnoreCase("nu") ? null : UUID.fromString(partyUUIDString);

                    int powerLevel = resultSet.getInt("power_level");
                    int abilityPoints = resultSet.getInt("ability_points");
                    long replaceAbilityCooldownTime = resultSet.getLong("replace_ability_cooldown_time");
                    int redeemableExp = resultSet.getInt("redeemable_exp");
                    int redeemableLevels = resultSet.getInt("redeemable_levels");
                    int boostedExp = resultSet.getInt("boosted_exp");
                    double divineEscapeExpDebuff = resultSet.getDouble("divine_escape_exp_debuff");
                    double divineEscapeDamageDebuff = resultSet.getDouble("divine_escape_damage_debuff");
                    long divineEscapeExpEndTime = resultSet.getLong("divine_escape_exp_end_time");
                    long divineEscapeDamageEndTime = resultSet.getLong("divine_escape_damage_end_time");

                    playerDataSnapshot.setPartyUUID(partyUUID);
                    playerDataSnapshot.setPowerLevel(powerLevel);
                    playerDataSnapshot.setAbilityPoints(abilityPoints);
                    playerDataSnapshot.setReplaceAbilityCooldownTime(replaceAbilityCooldownTime);
                    playerDataSnapshot.setRedeemableExp(redeemableExp);
                    playerDataSnapshot.setRedeemableLevels(redeemableLevels);
                    playerDataSnapshot.setBoostedExp(boostedExp);
                    playerDataSnapshot.setDivineEscapeExpDebuff(divineEscapeExpDebuff);
                    playerDataSnapshot.setDivineEscapeDamageDebuff(divineEscapeDamageDebuff);
                    playerDataSnapshot.setDivineEscapeExpEndTime(divineEscapeExpEndTime);
                    playerDataSnapshot.setDivineEscapeDamageEndTime(divineEscapeDamageEndTime);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerDataSnapshot;
    }

    /**
     * Saves all the player data that is stored inside this table, such as redeemable exp, for the provided {@link McRPGPlayer}.
     *
     * @param connection  The {@link Connection} to use to save the player data
     * @param mcRPGPlayer The {@link McRPGPlayer} whose data is being saved
     * @return A {@link CompletableFuture} that completes whenever the save has finished or completes with an {@link SQLException} if there
     * is an error with saving
     */
    @NotNull
    public static List<PreparedStatement> savePlayerData(@NotNull Connection connection, @NotNull McRPGPlayer mcRPGPlayer) {
        List<PreparedStatement> preparedStatements = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO " + TABLE_NAME +
                    " (uuid, power_level, ability_points, redeemable_exp, redeemable_levels, " +
                    "divine_escape_exp_debuff, divine_escape_damage_debuff, divine_escape_exp_end_time, divine_escape_damage_end_time, " +
                    "replace_ability_cooldown_time, boosted_exp, party_uuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            preparedStatement.setString(1, mcRPGPlayer.getUUID().toString());
            preparedStatements.add(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatements;
    }

    /**
     * A POJO containing all relevant player data obtained from this DAO
     */
    public static class PlayerDataSnapshot {

        private final UUID uuid;

        private UUID partyUUID;
        private int powerLevel;
        private int abilityPoints;
        private long replaceAbilityCooldownTime;
        private int redeemableExp;
        private int redeemableLevels;
        private int boostedExp;
        private double divineEscapeExpDebuff;
        private double divineEscapeDamageDebuff;
        private long divineEscapeExpEndTime;
        private long divineEscapeDamageEndTime;

        public PlayerDataSnapshot(@NotNull UUID uuid) {
            this.uuid = uuid;
        }

        /**
         * Gets the {@link UUID} of the player represented by this snapshot
         *
         * @return The {@link UUID} of the player represented by this snapshot
         */
        @NotNull
        public UUID getUuid() {
            return uuid;
        }

        /**
         * Gets the {@link UUID} of the player's {@link}
         *
         * @return The {@link UUID} of the player's {@link} or {@code null} if the
         * player isn't in a party
         */
        @Nullable
        public UUID getPartyUUID() {
            return partyUUID;
        }

        /**
         * Sets the {@link UUID} of the player's {@link} for this snapshot
         *
         * @param partyUUID The {@link UUID} of the player's {@link} for this snapshot
         *                  or {@code null} if the player isn't in a party
         */
        void setPartyUUID(@Nullable UUID partyUUID) {
            this.partyUUID = partyUUID;
        }

        /**
         * Gets the power level of the player, which is the sum of all of the player's skill levels
         *
         * @return The positive, zero inclusive power level of a player
         */
        public int getPowerLevel() {
            return powerLevel;
        }

        /**
         * Sets the power level of the player represented by this snapshot
         *
         * @param powerLevel The new positive, zero inclusive power level of the player represented by this snapshot
         */
        void setPowerLevel(int powerLevel) {
            this.powerLevel = Math.max(0, powerLevel);
        }

        /**
         * Gets the amount of ability points that the player has left to spend
         *
         * @return The positive, zero inclusive amout of ability points that the player has left to spend
         */
        public int getAbilityPoints() {
            return abilityPoints;
        }

        /**
         * Sets the amount of ability points that the player has to spend as represented by this snapshot
         *
         * @param abilityPoints The positive, zero inclusive amount of ability points for the player to be represented by this snapshot
         */
        void setAbilityPoints(int abilityPoints) {
            this.abilityPoints = Math.max(0, abilityPoints);
        }

        /**
         * Gets the time in millis that the player is able to replace an ability again for this snapshot
         *
         * @return The time in millis that the player is able to replace an ability again for this snapshot
         */
        public long getReplaceAbilityCooldownTime() {
            return replaceAbilityCooldownTime;
        }

        /**
         * Sets the time in millis that the player is able to replace an ability again according to this snapshot
         *
         * @param replaceAbilityCooldownTime The time in millis that the player is able to replace an ability again according to this snapshot
         */
        void setReplaceAbilityCooldownTime(long replaceAbilityCooldownTime) {
            this.replaceAbilityCooldownTime = replaceAbilityCooldownTime;
        }

        /**
         * Gets the amount of redeemable exp that the player has according to this snapshot
         *
         * @return The positive, zero inclusive amount of redeemable exp that the player has according to this snapshot
         */
        public int getRedeemableExp() {
            return redeemableExp;
        }

        /**
         * Sets the amount of redeemable exp that the player has according to this snapshot
         *
         * @param redeemableExp A positive, zero inclusive amount of redeemable exp that this snapshot should report the player having
         */
        void setRedeemableExp(int redeemableExp) {
            this.redeemableExp = Math.max(0, redeemableExp);
        }

        /**
         * Gets the amount of redeemable levels that the player has according to this snapshot
         *
         * @return The positive, zero inclusive amount of redeemable leves that the player has according to this snapshot
         */
        public int getRedeemableLevels() {
            return redeemableLevels;
        }

        /**
         * Sets the amount of redeemable levels that the player has according to this snapshot
         *
         * @param redeemableLevels A positive, zero inclusive amount of redeemable levels that this snapshot should report the player having
         */
        void setRedeemableLevels(int redeemableLevels) {
            this.redeemableLevels = Math.max(0, redeemableLevels);
        }

        /**
         * Gets the amount of boosted exp that the player has according to this snapshot
         *
         * @return The positive, zero inclusive amount of boosted exp that the player has according to this snapshot
         */
        public int getBoostedExp() {
            return boostedExp;
        }

        /**
         * Sets the amount of boosted exp that the player has according to this snapshot
         *
         * @param boostedExp A positive, zero inclusive amount of boosted exp that this snapshot should report the player having
         */
        void setBoostedExp(int boostedExp) {
            this.boostedExp = Math.max(0, boostedExp);
        }

        /**
         * Gets the percentage to debuff the player's exp gain by according to this snapshot
         *
         * @return The percentage to debuff the player's exp gain by according to this snapshot
         */
        public double getDivineEscapeExpDebuff() {
            return divineEscapeExpDebuff;
        }

        /**
         * Sets the percentage to debuff the player's exp gain by according to this snapshot
         *
         * @param divineEscapeExpDebuff The percentage to debuff the player's exp gain by according to this snapshot
         */
        void setDivineEscapeExpDebuff(double divineEscapeExpDebuff) {
            this.divineEscapeExpDebuff = divineEscapeExpDebuff;
        }

        /**
         * Gets the percentage to debuff the player's damage output by according to this snapshot
         *
         * @return The percentage to debuff the player's damage output by according to this snapshot
         */
        public double getDivineEscapeDamageDebuff() {
            return divineEscapeDamageDebuff;
        }

        /**
         * Sets the percentage to debuff the player's damage output by according to this snapshot
         *
         * @param divineEscapeDamageDebuff The percentage to debuff the player's damage output by according to this snapshot
         */
        void setDivineEscapeDamageDebuff(double divineEscapeDamageDebuff) {
            this.divineEscapeDamageDebuff = divineEscapeDamageDebuff;
        }

        /**
         * Gets the time in millis that the player's exp debuff ends for this snapshot
         *
         * @return The time in millis that the player's exp debuff ends for this snapshot
         */
        public long getDivineEscapeExpEndTime() {
            return divineEscapeExpEndTime;
        }

        /**
         * Gets the time in millis that the player's exp debuff ends for this snapshot
         *
         * @param divineEscapeExpEndTime The time in millis that the player's exp debuff ends for this snapshot
         */
        void setDivineEscapeExpEndTime(long divineEscapeExpEndTime) {
            this.divineEscapeExpEndTime = divineEscapeExpEndTime;
        }

        /**
         * Gets the time in millis that the player's damage debuff ends for this snapshot
         *
         * @return The time in millis that the player's damage debuff ends for this snapshot
         */
        public long getDivineEscapeDamageEndTime() {
            return divineEscapeDamageEndTime;
        }

        /**
         * Gets the time in millis that the player's damage debuff ends for this snapshot
         *
         * @param divineEscapeDamageEndTime The time in millis that the player's damage debuff ends for this snapshot
         */
        void setDivineEscapeDamageEndTime(long divineEscapeDamageEndTime) {
            this.divineEscapeDamageEndTime = divineEscapeDamageEndTime;
        }

    }
}
