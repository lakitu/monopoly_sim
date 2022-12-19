public class monopoly_sim {
    // region static final variables
    public static final String[] SPACES = new String[] {
        "GO",
        "Mediterranean Avenue",
        "Community Chest",
        "Baltic Avenue",
        "Income Tax",
        "Reading Railroad",
        "Oriental Avenue",
        "Chance",
        "Vermont Avenue",
        "Connecticut Avenue",
        "Jail",
        "St. Charles Place",
        "Electric Company",
        "States Avenue",
        "Virginia Avenue",
        "Pennsylvania Railroad",
        "St. James Place",
        "Community Chest",
        "Tennessee Avenue",
        "New York Avenue",
        "Free Parking",
        "Kentucky Avenue",
        "Chance",
        "Indiana Avenue",
        "Illinois Avenue",
        "B. & O. Railroad",
        "Atlantic Avenue",
        "Ventnor Avenue",
        "Water Works",
        "Marvin Gardens",
        "Go To Jail",
        "Pacific Avenue",
        "North Carolina Avenue",
        "Community Chest",
        "Pennsylvania Avenue",
        "Short Line",
        "Chance",
        "Park Place",
        "Luxury Tax",
        "Boardwalk"
    };
    public static final String[] COMMUNITY_CHEST_CARDS = new String[] {
        "Advance to GO", // IMPORTANT
        "Bank error in your favor",
        "Doctor's fees",
        "From sale of stock you get $50",
        "Get out of jail free",
        "Go To Jail", // IMPORTANT
        "Grand Opera Night",
        "Holiday Fund matures",
        "Income tax refund",
        "It is your birthday",
        "Life insurance matures",
        "Pay hospital fees of $100",
        "Pay school fees of $150",
        "Receive $25 consultancy fee",
        "You are assessed for street repairs",
        "You have won second prize in a beauty contest",
        "You inherit $100"
    };
    public static final String[] CHANCE_CARDS = new String[] {
        "Advance to GO", // IMPORTANT
        "Advance to Illinois Ave", // IMPORTANT
        "Advance to St. Charles Place", // IMPORTANT
        "Advance token to nearest Utility", // IMPORTANT
        "Advance token to nearest Railroad", // IMPORTANT
        "Bank pays you dividend of $50",
        "Get out of jail free",
        "Go back 3 spaces", // IMPORTANT
        "Go To Jail", // IMPORTANT
        "Make general repairs on all your property",
        "Pay poor tax of $15",
        "Take a trip to Reading Railroad", // IMPORTANT
        "Take a walk on the Boardwalk", // IMPORTANT
        "You have been elected Chairman of the Board",
        "Your building loan matures",
        "You have won a crossword competition"
    };
    // endregion
    static long[] landAmount = new long[40];
    static long numLands = 0L;

    int turns, players;
    int chance_index, community_chest_index;
    String[] shuffled_chance, shuffled_community_chest;

    public void set_up_game(int turns, int players) {
        this.turns = turns;
        this.players = players;

        shuffled_chance = shuffleArray(CHANCE_CARDS);
        shuffled_community_chest = shuffleArray(COMMUNITY_CHEST_CARDS);
        chance_index = community_chest_index = 0;
    }

    private static String[] shuffleArray(String[] arr) {
        // copies the array
        String[] shuffled = new String[arr.length];
        for (int i = 0; i < shuffled.length; i++) shuffled[i] = arr[i];

        // shuffles the array n^2 times
        for (int i = 0; i < shuffled.length * shuffled.length; i++) {
            // pick two random indices in the array
            int i1 = (int)Math.floor(Math.random() * shuffled.length);
            int i2 = (int)Math.floor(Math.random() * shuffled.length);
            // swap the two objects at the indices
            String temp = shuffled[i1];
            shuffled[i1] = shuffled[i2];
            shuffled[i2] = temp;
        }
        return shuffled;
    }

    /*private static boolean isProperty (int current_space) {
        String space = SPACES[current_space];
        return !(
            space.equals("GO") ||
            space.equals("Community Chest") ||
            space.equals("Income Tax") ||
            space.equals("Chance") ||
            space.equals("Jail") ||
            space.equals("Free Parking") ||
            space.equals("Go To Jail") ||
            space.equals("Luxury Tax")
        );
    }
    */

    private static int evaluateCard(String s, int current_space) {
        if (s.equals("Advance to GO")) {
            return 0;
        } else if (s.equals("Advance to Illinois Ave")) {
            return space("Illinois Avenue");
        } else if (s.equals("Advance to St. Charles Place")) {
            return space("St. Charles Place");
        } else if (s.equals("Advance token to nearest Utility")) {
            int electric_company = space("Electric Company");
            int water_works = space("Water Works");
            if (current_space > electric_company && current_space < water_works) {
                return water_works;
            } else {
                return electric_company;
            }
        } else if (s.equals("Advance token to nearest Railroad")) {
            int reading_railroad = space("Reading Railroad");
            int pennsylvania_railroad = space("Pennsylvania Railroad");
            int b_and_o_railroad = space("B. & O. Railroad");
            int short_line = space("Short Line");
            if (current_space > reading_railroad && current_space < pennsylvania_railroad) {
                return pennsylvania_railroad;
            } else if (current_space > pennsylvania_railroad && current_space < b_and_o_railroad) {
                return b_and_o_railroad;
            } else if (current_space > b_and_o_railroad && current_space < short_line) {
                return short_line;
            } else {
                return reading_railroad;
            }
        } else if (s.equals("Go back 3 spaces")) {
            if (current_space - 3 < 0) return current_space - 3 + 40;
            else return current_space - 3;
        } else if (s.equals("Go To Jail")) {
            return space("Jail");
        } else if (s.equals("Take a trip to Reading Railroad")) {
            return space("Reading Railroad");
        } else if (s.equals("Take a walk on the Boardwalk")) {
            return space("Boardwalk");
        } else {
            return current_space;
        }
    }

    private int draw_chance(int current_space) {
        int result = evaluateCard(shuffled_chance[chance_index], current_space);

        chance_index++;
        if (chance_index >= CHANCE_CARDS.length) {
            chance_index = 0;
            shuffled_chance = shuffleArray(CHANCE_CARDS);
        }

        return result;
    }

    private int draw_community_chest(int current_space) {
        int result = evaluateCard(shuffled_community_chest[community_chest_index], current_space);

        community_chest_index++;
        if (community_chest_index >= COMMUNITY_CHEST_CARDS.length) {
            community_chest_index = 0;
            shuffled_community_chest = shuffleArray(COMMUNITY_CHEST_CARDS);
        }

        return result;
    }

    /**
     * Finds the position of the input space.
     * It does not take into account which chance or community chest is closest
     * @param s space name
     * @return space position
     */
    private static int space (String s) {
        for (int i = 0; i < SPACES.length; i++) {
            if (s.equals(SPACES[i])) {
                return i;
            }
        }
        return -1; // 100,000 games of length 30, this never occurs
    }

    /**
     * Generates two random numbers from 1 to 6 and returns an array of the form
     * {sum of dice, dice 1, dice 2}
     */
    private static int[] roll() {
        // return new int[] {2, 1, 1}; // test code
        int dice1 = (int) (Math.random() * 6) + 1;
        int dice2 = (int) (Math.random() * 6) + 1;
        return new int[] {dice1 + dice2, dice1, dice2};
    }

    private int turn (int space) {
        int numDoubles = 0;
        boolean rollAgain = false;
        
        do {
            // roll the dice
            int[] roll = roll();
            // if you roll doubles
            if (roll[1] == roll[2]) {
                numDoubles++;
                if (numDoubles == 3) break;
                rollAgain = true;
            } else {
                rollAgain = false;
            }

            space = (space + roll[0]) % SPACES.length;
            // handle special cases
            int oldSpace = space;
            if (SPACES[space].equals("Chance")) {
                space = draw_chance(space);
            }
            if (SPACES[space].equals("Community Chest")) {
                space = draw_community_chest(space);
            }
            if (SPACES[space].equals("Go To Jail")) {
                space = space("Jail");
            }

            landAmount[space]++;

            // if you got sent to jail via Go To Jail card or space, act as though 3 doubles
            // were rolled
            if ((oldSpace != 10) && space == 10) {
                break;
            }
        } while (rollAgain == true);
        numLands++;
        return space;
    }

    private void play_game() {
        int[] player_spaces = new int[players];
        for (int round = 0; round < turns; round++) {
            for (int player = 0; player < player_spaces.length; player++) {
                player_spaces[player] = turn(player_spaces[player]);
            }
        }
    }

    private static void update_probabilities(int current_space) {
        landAmount[current_space]++;
        numLands++;
    }

    private static void print_probabilities() {
        for (int i = 0; i < SPACES.length; i++) {
            double probability = landAmount[i] * 1. / numLands;
            if (Double.isNaN(probability)) probability = 0;
            System.out.printf("%25s: %5s\n", SPACES[i], Double.toString(probability));
        }
    }

    private static void print_probabilities_sorted() {
        Probability[] list = new Probability[SPACES.length];
        for (int i = 0; i < SPACES.length; i++) {
            list[i] = new Probability(landAmount[i] * 1. / numLands, SPACES[i]);
        }
        insertionSort(list);

        for (int i = 0; i < list.length; i++) {
            System.out.printf("%25s: %5s\n", list[i].name, list[i].probability);
        }
    }

    public static void insertionSort(Probability[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i].probability < arr[i - 1].probability) {
                // Save a copy of the element to be inserted.
                Probability toInsert = arr[i];

                // Shift right to make room for element.
                int j = i;
                do {
                    arr[j] = arr[j - 1];
                    j = j - 1;
                } while (j > 0 && toInsert.probability < arr[j - 1].probability);

                // Put the element in place.
                arr[j] = toInsert;
            }
        }
    }

    private static class Probability {
        double probability;
        String name;
        Probability(double _probability, String _name) {
            probability = _probability;
            name = _name;
        }
    }

    public static void main (String[] args) {
        int turns = 30, players = 4;
        int numGames = (int)Math.pow(10, 7);
        monopoly_sim game = new monopoly_sim();
        for (int games = 0; games < numGames; games++) {
            game.set_up_game(turns, players);
            game.play_game();
            if (games % 100000 == 0) System.out.println(games);
        }
        // note that we don't differentiate betwen Jail and Just Visiting because
        // neither contributes to rent
        // monopoly_sim.print_probabilities_sorted();
        monopoly_sim.print_probabilities();
    }
}