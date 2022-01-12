package us.eunoians.mcrpg.database.tables.skills;

import us.eunoians.mcrpg.McRPG;
import us.eunoians.mcrpg.database.DatabaseManager;
import us.eunoians.mcrpg.database.tables.TableVersionHistoryDAO;
import us.eunoians.mcrpg.players.McRPGPlayer;
import us.eunoians.mcrpg.types.DefaultAbilities;
import us.eunoians.mcrpg.types.Skills;
import us.eunoians.mcrpg.types.UnlockedAbilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * A DAO used to store data regarding a player's {@link us.eunoians.mcrpg.skills.Fitness} skill
 *
 * @author DiamondDagger590
 */
public class FitnessDAO {

    private static final String TABLE_NAME = "mcrpg_fitness_data";
    private static final int CURRENT_TABLE_VERSION = 1;

    private static boolean isAcceptingQueries = true;

    /**
     * Attempts to create a new table for this DAO provided that the table does not already exist.
     *
     * @param connection      The {@link Connection} to use to attempt the creation
     * @param databaseManager The {@link DatabaseManager} being used to attempt to create the table
     * @return A {@link CompletableFuture} containing a {@link Boolean} that is {@code true} if a new table was made,
     * or {@code false} otherwise.
     */
    public static CompletableFuture<Boolean> attemptCreateTable(Connection connection, DatabaseManager databaseManager) {

        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        databaseManager.getDatabaseExecutorService().submit(() -> {

            //Check to see if the table already exists
            if (databaseManager.getDatabase().tableExists(TABLE_NAME)) {
                completableFuture.complete(false);
                return;
            }

            isAcceptingQueries = false;

            /*****
             ** Table Description:
             ** Contains player data for the archery skill
             *
             *
             * id is the id of the entry which auto increments but doesn't really serve a large purpose since it isn't
             * guaranteed to be the same for players across the board
             * uuid is the {@link java.util.UUID} of the player being stored
             * current_exp is the amount of exp a player currently has in this skill
             * current_level is the level a player currently has in this skill
             * is_roll_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.Roll} ability toggled
             * is_thick_skin_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.ThickSkin} ability toggled
             * is_bullet_proof_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.BulletProof} ability toggled
             * is_dodge_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.Dodge} ability toggled
             * is_iron_muscles_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.IronMuscles} ability toggled
             * is_runners_diet_toggled represents if the player has the {@link us.eunoians.mcrpg.abilities.fitness.RunnersDiet} ability toggled
             * is_divine_escape_toggled represents if the player ahs the {@link us.eunoians.mcrpg.abilities.fitness.DivineEscape} ability toggled
             * thick_skin_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.ThickSkin} ability
             * bullet_proof_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.BulletProof} ability
             * dodge_tier_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.Dodge} ability
             * iron_muscles_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.IronMuscles} ability
             * runners_diet_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.RunnersDiet} ability
             * divine_escape_tier represents the tier for the player's {@link us.eunoians.mcrpg.abilities.fitness.DivineEscape} ability
             * is_thick_skin_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.ThickSkin} pending to be accepted
             * is_bullet_proof_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.BulletProof} pending to be accepted
             * is_dodge_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.Dodge} pending to be accepted
             * is_iron_muscles_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.IronMuscles} pending to be accepted
             * is_runners_diet_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.RunnersDiet} pending to be accepted
             * is_divine_escape_pending represents if the player has {@link us.eunoians.mcrpg.abilities.fitness.DivineEscape} pending to be accepted
             * divine_escape_cooldown represents the cooldown for the player's {@link us.eunoians.mcrpg.abilities.fitness.DivineEscape} ability
             **
             ** Reasoning for structure:
             ** PK is the `uuid` field, as each player only has one uuid
             *****/
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE `" + TABLE_NAME + "`" +
                                                                           "(" +
                                                                           "`id` int(11) NOT NULL AUTO_INCREMENT," +
                                                                           "`uuid` varchar(32) NOT NULL," +
                                                                           "`current_exp` int(11) NOT NULL DEFAULT 0," +
                                                                           "`current_level` int(11) NOT NULL DEFAULT 0," +
                                                                           "`is_roll_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_thick_skin_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_bullet_proof_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_dodge_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_iron_muscles_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_runners_diet_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`is_divine_escape_toggled` BIT NOT NULL DEFAULT 1," +
                                                                           "`thick_skin_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`bullet_proof_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`dodge_tier_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`iron_muscles_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`runners_diet_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`divine_escape_tier` int(11) NOT NULL DEFAULT 0," +
                                                                           "`is_thick_skin_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`is_bullet_proof_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`is_dodge_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`is_iron_muscles_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`is_runners_diet_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`is_divine_escape_pending` BIT NOT NULL DEFAULT 0," +
                                                                           "`divine_escape_cooldown` int(11) NOT NULL DEFAULT 0," +
                                                                           "PRIMARY KEY (`uuid`)" +
                                                                           ");")) {
                statement.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }

            isAcceptingQueries = true;

            completableFuture.complete(true);
        });

        return completableFuture;
    }

    /**
     * Checks to see if there are any version differences from the live version of this SQL table and then current version.
     * <p>
     * If there are any differences, it will iteratively go through and update through each version to ensure the database is
     * safe to run queries on.
     *
     * @param connection The {@link Connection} that will be used to run the changes
     * @return The {@link  CompletableFuture} that is running these changes.
     */
    public static CompletableFuture<Void> updateTable(Connection connection) {

        DatabaseManager databaseManager = McRPG.getInstance().getDatabaseManager();
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        databaseManager.getDatabaseExecutorService().submit(() -> {

            if (TableVersionHistoryDAO.isAcceptingQueries()) {

                TableVersionHistoryDAO.getLatestVersion(connection, TABLE_NAME).thenAccept(lastStoredVersion -> {

                    if (lastStoredVersion >= CURRENT_TABLE_VERSION) {
                        completableFuture.complete(null);
                        return;
                    }

                    isAcceptingQueries = false;

                    //This is where we would add any updates but we don't have any
                    if (lastStoredVersion == 1) { //Would be used whenever our CURRENT_TABLE_VERSION is 2

                    }

                    isAcceptingQueries = true;
                });

            }

            completableFuture.complete(null);
        });

        return completableFuture;
    }

    /**
     * Gets a {@link SkillDataSnapshot} containing all of the player's skill data for {@link us.eunoians.mcrpg.skills.Fitness}. If
     * the provided {@link UUID} doesn't have any data, any empty {@link SkillDataSnapshot} will be returned instead with no populated maps
     * and default exp/level values set to 0
     *
     * @param connection The {@link Connection} to use to get the skill data
     * @param uuid       The {@link UUID} of the player who's data is being obtained
     * @return A {@link CompletableFuture} containing a {@link SkillDataSnapshot} that has all of the player's {@link us.eunoians.mcrpg.skills.Fitness} skill
     * data. If the provided {@link UUID} doesn't have any data, any empty {@link SkillDataSnapshot} will be returned instead with no populated maps
     * and default exp/level values set to 0
     */
    public static CompletableFuture<SkillDataSnapshot> getPlayerFitnessData(Connection connection, UUID uuid) {

        DatabaseManager databaseManager = McRPG.getInstance().getDatabaseManager();
        Skills skillType = Skills.FITNESS;
        CompletableFuture<SkillDataSnapshot> completableFuture = new CompletableFuture<>();

        databaseManager.getDatabaseExecutorService().submit(() -> {

            SkillDataSnapshot skillDAOWrapper = new SkillDataSnapshot(uuid, skillType);

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE uuid = ?;")) {

                preparedStatement.setString(1, uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {


                    while (resultSet.next()) {

                        int currentExp = resultSet.getInt("current_exp");
                        int currentLevel = resultSet.getInt("current_level");

                        skillDAOWrapper.setCurrentExp(currentExp);
                        skillDAOWrapper.setCurrentLevel(currentLevel);

                        //Default Ability
                        DefaultAbilities defaultAbility = skillType.getDefaultAbility();
                        skillDAOWrapper.addAbilityToggledData(defaultAbility, resultSet.getBoolean("is_" + defaultAbility.getDatabaseName() + "_toggled"));

                        //Unlocked Abilities
                        for(UnlockedAbilities ability : skillType.getUnlockedAbilities()){
                            skillDAOWrapper.addAbilityData(ability, resultSet);
                        }
                    }

                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }

            completableFuture.complete(skillDAOWrapper);

        });

        return completableFuture;
    }

    //TODO because I only care about loading player data rn and cba to save it
    public static void savePlayerFitnessData(Connection connection, McRPGPlayer mcRPGPlayer) {

    }

    /**
     * Checks to see if this table is accepting queries at the moment. A reason it could be false is either the table is
     * in creation or the table is being updated and for some reason a query is attempting to be run.
     *
     * @return {@code true} if this table is accepting queries
     */
    public static boolean isAcceptingQueries() {
        return isAcceptingQueries;
    }
}
